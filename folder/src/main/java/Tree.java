import one.util.streamex.StreamEx;

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

    static Tree from(StreamEx<String> folders) {
        return folders.foldLeft(
                new Tree(""),
                (tree, folder) -> {
                    PathUtil
                            .components(folder)
                            .foldLeft(tree, (currentDir, name) ->
                                    currentDir.children.computeIfAbsent(name, n -> new Tree(name))
                            );
                    return tree;
                });
    }

    Stream<String> toPaths(String parentPath) {
        return children
                .values()
                .stream()
                .flatMap(child -> {
                    String childPath = ("/".equals(parentPath) ? "" : parentPath ) + "/" + child.name;
                    return child.children.isEmpty() ? Stream.of(childPath) : child.toPaths(childPath);
                });
    }

    public String getName() {
        return name;
    }

    public Stream<Tree> getChildren() {
        return new ArrayList<>(children.values()).stream();
    }

}

