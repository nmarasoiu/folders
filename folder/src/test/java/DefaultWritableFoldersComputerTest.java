import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.stream.Collectors;

class DefaultWritableFoldersComputerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Property
    boolean joiningTwoLists(
            @ForAll List<String> list1,
            @ForAll List<String> list2) {
        return new DefaultWritableFoldersComputer().writableFolders(withSlashes(list1), withSlashes(list2)) != null;
    }

    private List<String> withSlashes(List<String> strings) {
        return strings.stream().map(s -> "/" + s).collect(Collectors.toList());
    }


}