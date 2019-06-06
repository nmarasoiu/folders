import java.util.List;

public interface WritableFoldersComputer {
    Tree accessibleAndWritableFolders(List<String> readableFolders, List<String> writableFolders);
}
