import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DefaultWritableFoldersComputerTest {
    @Property
    boolean joiningTwoLists(@ForAll List<String> onlyReadableFolders, @ForAll List<String> writableFolders) {
        return testEquality(withSlashes(onlyReadableFolders), withSlashes(writableFolders));
    }

    private boolean testEquality(List<String> onlyReadableFolders, List<String> writableFolders) {
        Set<String> expectedPaths = filterToOnlyReachable(
                writableFolders,
                Stream.of(writableFolders, onlyReadableFolders, Collections.singletonList("/"))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()));
        Set<String> obtainedPaths = new DefaultWritableFoldersComputer().writableFolders(onlyReadableFolders, writableFolders).toPaths().collect(Collectors.toSet());

        //todo remove this
        if(expectedPaths.contains("/")){
            obtainedPaths.add("/");
        }

        boolean equals = obtainedPaths.equals(expectedPaths);
        if (!equals) {
            System.out.println("---");
            System.out.println("expectedPaths=" + arranged(expectedPaths));
            System.out.println("obtainedPaths=" + arranged(obtainedPaths));
            System.out.println("---");
        }
        return equals;

    }

    private TreeSet<String> arranged(Set<String> set) {
        return set.stream().map(s -> s.isEmpty() ? "<empty string>" : s).collect(Collectors.toCollection(TreeSet::new));
    }

    private Set<String> filterToOnlyReachable(List<String> writableFolders, Set<String> accessibleFolders) {
        return writableFolders
                .stream()
                .filter(writableFolder -> {
                    String parent = parent(writableFolder);
                    return isRoot(parent) || accessibleFolders.contains(parent);
                })
                .collect(Collectors.toSet());
    }

    private boolean isRoot(String parent) {
        return parent.length() == 0;
    }

    private String parent(String folder) {
        int lastIndexOf = folder.lastIndexOf(".");
        return lastIndexOf < 0 ? "" : folder.substring(0, lastIndexOf);
    }

    private List<String> withSlashes(List<String> strings) {
        return strings
                .stream()
                .map(String::trim)
                .map(string -> string.isEmpty() ? "/" :
                        string
                                .chars()
                                .mapToObj(character -> "/" + (char)('a'+(character%12)))
                                .collect(StringBuilder::new,
                                        StringBuilder::append,
                                        StringBuilder::append)
                                .toString())
                .collect(Collectors.toList());
    }
}
