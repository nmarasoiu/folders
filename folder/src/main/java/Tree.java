import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Tree {
    private final String name;
    private final String path;
    private final Map<String, Tree> children;

    Tree(String name, String path) {
        this(name, path, Collections.emptyList());
    }

    private Tree(String name, String path, List<Tree> children) {
        this.name = name;
        this.path = path;
        this.children = children.stream().collect(Collectors.toMap/*ConcurrentMap*/(tree -> tree.name, tree -> tree));
    }

    Stream<String> toPaths() {
        return children.values().stream().flatMap(child -> child.toPaths().map(descendentPath -> this.path + descendentPath));
    }

    void addIfParentIsPresent(String folder) {
        if ("".equals(folder)) {
            return;
        }
        int lastSlashIndex = folder.lastIndexOf("/");
        if (lastSlashIndex < 0) {
            throw new IllegalArgumentException("The paths should be absolute and thus start with /");
        }
        String parentPath = folder.substring(0, lastSlashIndex);
        String childName = folder.substring(1 + lastSlashIndex);
        getOptionalTreeForPath(parentPath)
                .ifPresent(parentTree -> parentTree.addChild(childName));
    }

    void deleteChildrenWhichDontHaveWritableSubFolders(Set<String> writableFolders) {
        children
                .entrySet()
                .stream()
                .filter(childTreeEntry -> {
                    Tree childTree = childTreeEntry.getValue();//todo concurrency, tests
                    childTree.deleteChildrenWhichDontHaveWritableSubFolders(writableFolders);
                    return childTree.isLeaf() && !writableFolders.contains(childTree.path);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(children::remove);
    }

    private boolean isLeaf() {
        return children.isEmpty();
    }

    private Optional<Tree> getOptionalTreeForPath(String path) {
        if ("".equals(path)) {
            return Optional.of(this);
        }
        if (!path.contains("/")) {
            return getChildByName(path);
        } else {
            String[] firstFolderAndFollowingPath = path.split("/", 2);
            String firstFolder = firstFolderAndFollowingPath[0];
            String followingPath = firstFolderAndFollowingPath[1];
            return getChildByName(firstFolder)
                    .flatMap(tree -> tree.getOptionalTreeForPath(followingPath));
        }

    }

    private void addChild(String childName) {
        if (!hasChildByName(childName)) {
            children.put(childName, new Tree(childName, path + "/" + childName));
        }
    }

    private boolean hasChildByName(String childName) {
        return getChildByName(childName).isPresent();
    }

    private Optional<Tree> getChildByName(String childName) {
        return Optional.ofNullable(children.get(childName));
    }
}

