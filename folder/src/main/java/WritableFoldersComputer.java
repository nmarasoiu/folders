import java.util.Collection;

public interface WritableFoldersComputer {
    Tree accessibleAndWritableFolders(Collection<String> readableFolders, Collection<String> writableFolders);
}
