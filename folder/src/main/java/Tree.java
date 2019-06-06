import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Tree {
    private final String name;
    private final Map<String, Tree> children = new HashMap<>();

    private Tree(String name) {
        this.name = name;
    }

    static Tree from(Collection<String> folders) {
        Tree root = new Tree("");
        folders
                .stream()
                .map(path -> path.substring(1))
                .forEachOrdered(folder -> {
                    Tree currentDir = root;
                    for (String name : folder.split("/")) {
                        currentDir = currentDir.children.computeIfAbsent(name, n -> new Tree(name));
                    }
                });
        return root;
    }

    Set<String> paths() {
        return toPaths("/").collect(Collectors.toSet());
    }

    private Stream<String> toPaths(String parentPath) {
        return children
                .values()
                .stream()
                .flatMap(child -> {
                    String childPath = "/".equals(parentPath) ? "/" + child.name : parentPath + "/" + child.name;
                    return child.children.isEmpty() ? Stream.of(childPath) : child.toPaths(childPath);
                });
    }

    public String getName() {
        return name;
    }

    public Collection<Tree> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    @Override
    public String toString() {
        return "Tree{" +
                "name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}

