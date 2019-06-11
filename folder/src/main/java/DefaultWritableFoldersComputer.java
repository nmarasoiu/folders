import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;

public class DefaultWritableFoldersComputer implements WritableFoldersComputer {

    @Override
    public Tree accessibleAndWritableFolders(List<String> readableFolders, List<String> writableFolders) {
        return Tree.from(reachableAndWritableFolders(newHashSet(clean(readableFolders)), clean(writableFolders)));
    }

    private Stream<String> reachableAndWritableFolders(Set<String> readableFolders, List<String> writableFolders) {
        return writableFolders
                .stream()
//                .distinct()
                .filter(writableFolder -> readableFolders.containsAll(ancestors(writableFolder)));
    }

    private List<String> ancestors(String dir) {
        List<String> ancestorSet = new ArrayList<>();
        StringBuilder ancestorBuilder = new StringBuilder(dir.length());
        for (String name : dir.substring(1).split("/")) {
            ancestorBuilder.append("/").append(name);
            ancestorSet.add(ancestorBuilder.toString());
        }
        return ancestorSet;
    }

    private List<String> clean(List<String> readableFolders) {
        return readableFolders
                .stream()
                .map(String::trim)
                .map(str -> str.replaceAll("[/]+", "/"))
                .map(str -> str.endsWith("/") ? str.substring(0, str.length() - 1) : str)
                .collect(Collectors.toList());
    }

}
