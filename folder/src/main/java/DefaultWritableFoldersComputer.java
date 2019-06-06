import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultWritableFoldersComputer implements WritableFoldersComputer {

    @Override
    public Tree accessibleAndWritableFolders(List<String> readableFolders, List<String> writableFolders) {
        return Tree.from(reachableAndWritableFolders(new HashSet<>(clean(readableFolders)), clean(writableFolders)));
    }

    private List<String> reachableAndWritableFolders(Set<String> readableFolders, List<String> writableFolders) {
        return writableFolders
                .stream()
//                .distinct()
                .filter(writableFolder -> readableFolders.containsAll(ancestors(writableFolder)))
                .collect(Collectors.toList());
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
