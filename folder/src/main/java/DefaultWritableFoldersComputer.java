import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class DefaultWritableFoldersComputer implements WritableFoldersComputer {
    private static final Pattern slashSplitter = Pattern.compile("/");

    @Override
    public Tree accessibleAndWritableFolders(List<String> readableFolders, List<String> writableFolders) {
        Set<String> readableDirs = clean(readableFolders).collect(toSet());
        List<String> writableDirs = clean(writableFolders).collect(toList());
        return Tree.from(reachableAndWritableFolders(readableDirs, writableDirs));
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
                .map(name -> ancestorBuilder.append("/").append(name).toString());
    }

    private Stream<String> clean(List<String> readableFolders) {
        return readableFolders
                .stream()
                .map(String::trim)
                .map(str -> str.replaceAll("[/]+", "/"))
                .map(str -> str.endsWith("/") ? str.substring(0, str.length() - 1) : str);
    }

}
