import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class DefaultWritableFoldersComputer implements WritableFoldersComputer {
    private static final Pattern slashSplitter = Pattern.compile("/");

    @Override
    public Tree accessibleAndWritableFolders(Collection<String> readableFolders, Collection<String> writableFolders) {
        Set<String> readableDirs = clean(readableFolders).collect(toSet());
        List<String> writableDirs = clean(writableFolders).collect(toList());
        return Tree.from(reachableAndWritableFolders(readableDirs, writableDirs));
    }

    private Stream<String> reachableAndWritableFolders(Set<String> readableFolders, Collection<String> writableFolders) {
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

    private Stream<String> clean(Collection<String> folders) {
        return folders
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(path -> path.replaceAll("[/]+", "/"))
                .map(path -> path.endsWith("/") ? path.substring(0, path.length() - 1) : path);
    }
}
