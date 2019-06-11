import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;

public class DefaultWritableFoldersComputer implements WritableFoldersComputer {
    private static final Pattern slashSplitter = Pattern.compile("/");

    @Override
    public Tree accessibleAndWritableFolders(List<String> readableFolders, List<String> writableFolders) {
        return Tree.from(reachableAndWritableFolders(newHashSet(clean(readableFolders)), clean(writableFolders)));
    }

    private Stream<String> reachableAndWritableFolders(Set<String> readableFolders, List<String> writableFolders) {
        return writableFolders
                .stream()
                .filter(writableFolder -> ancestors(writableFolder).allMatch(readableFolders::contains));
    }

    private Stream<String> ancestors(String dir) {
        StringBuilder ancestorBuilder = new StringBuilder(dir.length());
        return slashSplitter
                .splitAsStream(dir.substring(1))
                .map(name -> {
                    ancestorBuilder.append("/").append(name);
                    return ancestorBuilder.toString();
                });
    }

    private List<String> clean(List<String> readableFolders) {
        return readableFolders
                .stream()
                .map(String::trim)
                .map(str -> str.replaceAll("[/]+", "/"))
                .map(str -> str.endsWith("/") ? str.substring(0, str.length() - 1) : str)
//                .distinct()
                .collect(Collectors.toList());//toSet? but maybe nicer to keep the order
    }

}
