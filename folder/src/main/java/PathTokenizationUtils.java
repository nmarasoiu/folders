import one.util.streamex.StreamEx;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class PathTokenizationUtils {
    private static final Pattern slashPattern = Pattern.compile("/");

    private PathTokenizationUtils() {
    }

    public static StreamEx<String> split(String path) {
        return StreamEx.of(
                slashPattern
                        .splitAsStream(path)
                        .skip(1)); // the paths are absolute and the first elem will be "", before the first "/"
    }
}
