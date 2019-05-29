import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class DefaultWritableFoldersComputer implements WritableFoldersComputer {
    public Tree writableFolders(List<String> onlyReadableFolders, List<String> writableFolders) {
        Tree tree = new Tree("", "/");
        Stream.of(onlyReadableFolders, writableFolders)
                .flatMap(Collection::stream)
                .map(String::trim)
                .map(path -> path.endsWith("/") ? path.substring(0, path.length() - 1) : path)
                .sorted()
                .forEachOrdered(tree::addIfParentIsPresent);//todo concurrency? happens-before should be enough, no need for ConcurrentMap>?
        //now tree contains only the accessible folders (going only thru read or write available parent folders)
        //now we should delete nodes such that all leaves are writable folders
        tree.deleteChildrenWhichDontHaveWritableFolders(new HashSet<>(writableFolders));
        return tree;
    }
}
