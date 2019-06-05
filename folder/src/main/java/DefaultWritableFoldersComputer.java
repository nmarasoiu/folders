import java.util.*;
import java.util.stream.Stream;

public class DefaultWritableFoldersComputer implements WritableFoldersComputer {
    public Tree writableFolders(List<String> onlyReadableFolders, List<String> writableFolders) {
        Set<String> writableFolderSet = new HashSet<>(writableFolders);
        Tree tree = new Tree("", "/");
        Stream.of(Collections.singleton("/"), onlyReadableFolders, writableFolderSet)
                .flatMap(Collection::stream)
                .map(String::trim)
                .map(path -> path.endsWith("/") ? path.substring(0, path.length() - 1) : path)
                .sorted()
                .forEachOrdered(tree::addIfParentIsPresent);//todo concurrency? happens-before should be enough, no need for ConcurrentMap>?
        //now tree contains only the accessible folders (going only thru read or write available parent folders)
        //now we should delete nodes such that all leaves are writable folders
        tree.deleteChildrenWhichDontHaveWritableSubFolders(writableFolderSet);
        return tree;
    }
}
