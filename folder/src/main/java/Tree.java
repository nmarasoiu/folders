import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class Tree {
    private final String name;
    private final Map<String, Tree> children = new HashMap<>();

    private Tree(String name) {
        this.name = name;
    }

    static Tree from(Stream<String> folders) {
        Tree root = new Tree("");
        folders.forEachOrdered(folder ->
                PathUtil
                        .components(folder)
                        .foldLeft(root, (currentDir, name) ->
                                currentDir.children.computeIfAbsent(name, n -> new Tree(name))
                        ));
        return root;
    }

    Stream<String> toPaths(String parentPath) {
        return children
                .values()
                .stream()
                .flatMap(child -> {
                    String childPath = ("/".equals(parentPath) ? "/" : parentPath + "/") + child.name;
                    return child.children.isEmpty() ? Stream.of(childPath) : child.toPaths(childPath);
                });
    }

    private String getName() {
        return name;
    }

    private Stream<Tree> getChildren() {
        return new ArrayList<>(children.values()).stream();
    }

    @Override
    public String toString() {
        return "Tree{" +
                "name='" + getName() + '\'' +
                ", children=" + getChildren() +
                '}';
    }
}

