#StandardSQL
with
    web_analytics as (
      select
        *,
        parse_date('%Y%m%d', web_analytics.date) as sessionDate
      from
        `data-to-insights.ecommerce.web_analytics` as web_analytics
    ),

    visitor_product_weekly_t as (
      select
        fullVisitorId,
        productSKU,
        v2ProductName as productName,
        53 * extract(isoyear from sessionDate) + extract(isoweek from sessionDate) - 1 as isoYearWeek,
        sum(productquantity) as quantitySum,
        sum(productPrice) as pricesSum, --is this always $?
        max(sessionDate) as lastSessionDate
      from
        web_analytics
      cross join
        unnest(web_analytics.hits) as hits_t
      cross join
        unnest(hits_t.product) as products_t
      where
        hits_t.eCommerceAction.action_type='6' --complete purchase
      group by
        fullVisitorId,
        productSKU,
        v2ProductName,
        isoYearWeek
    ),

    visitor_product_week_with_prev_week_t as (
        select
            row_number() over(order by isoYearWeek) AS rownumber,
            fullVisitorId,
            productSKU,
            isoYearWeek,
            lag(isoYearWeek,1) over (
                partition by fullVisitorId, productSKU
                order by isoYearWeek
            ) as prevYearWeek
        from
            visitor_product_weekly_t
        group by
            fullVisitorId,
            productSKU,
            isoYearWeek
    ),

    visitor_product_week_with_island_t as (
        select
            fullVisitorId,
            productSKU,
            isoYearWeek,
            sum (
                case when isoYearWeek - prevYearWeek = 1 then 0 else 1 end
            ) over (
                partition by fullVisitorId, productSKU order by rownumber
            ) as islandId
        from visitor_product_week_with_prev_week_t
    ),
    
    visitor_product_island_t as (
        select
            fullVisitorId,
            productSKU,
            islandId,
            count(*) as numberOfConsecutiveWeeks
        from
            visitor_product_week_with_island_t
        group by
            fullVisitorId,
            productSKU,
            islandId
    ),

    visitor_product_longest_island_t as (
        select
            fullVisitorId,
            productSKU,
            max(numberOfConsecutiveWeeks) as consecutiveWeeksCount
        from
            visitor_product_island_t
        group by
            fullVisitorId,
            productSKU
        having
            max(numberOfConsecutiveWeeks) > 1
    ),

    visitor_product_t as (
      select
        fullVisitorId,
        productSKU,
        productName,
        sum(quantitySum) as quantity,
        sum(pricesSum)/1000/1000 as totalValue, --is this always $?
        max(lastSessionDate) as lastWeek
      from
        visitor_product_weekly_t as t1
      group by
        fullVisitorId,
        productSKU,
        productName
    ),

    visitor_product_with_consecutive_weeks_t as (
        select
            t1.*,
            t2.consecutiveWeeksCount
        from
            visitor_product_t t1
        join
            visitor_product_longest_island_t t2
            on t1.fullVisitorId = t2.fullVisitorId
            and t1.productSKU = t2.productSKU
    )

select
    fullVisitorId,
    array_agg(struct(productSKU, productName, quantity, totalValue, lastWeek, consecutiveWeeksCount)) as products
from
    visitor_product_with_consecutive_weeks_t
group by
    fullVisitorId
order by
    max(consecutiveWeeksCount) desc,
    max(lastWeek) desc,
    max(totalValue) desc,
    max(quantity) desc
