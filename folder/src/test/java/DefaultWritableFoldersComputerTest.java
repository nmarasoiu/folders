import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class DefaultWritableFoldersComputerTest {

    private static final List<String> ROOT = Collections.singletonList("/");
    private static final WritableFoldersComputer DEFAULT_WRITABLE_FOLDERS_COMPUTER = new DefaultWritableFoldersComputer();

    @Test
    void simpleTests() {
        List<String> writableFolders = asList("/a", "/b", "/b/c/d", "/b/f/e");
        List<String> readableFolders = concat(writableFolders, asList("/", "/b/c"));
        assertThat(
                DEFAULT_WRITABLE_FOLDERS_COMPUTER
                        .accessibleAndWritableFolders(readableFolders, writableFolders).toPaths("/")
                        .collect(Collectors.toSet()),
                equalTo(new HashSet<>(asList("/a", "/b/c/d")))
        );
    }

    private List<String> concat(List<String> list1, List<String> list2) {
        return Stream.concat(list1.stream(),list2.stream()).collect(Collectors.toList());
    }

}
