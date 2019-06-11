import one.util.streamex.StreamEx;

import java.util.regex.Pattern;

final class PathUtil {
    private static final Pattern slashPattern = Pattern.compile("/");

    private PathUtil() {
    }

    static StreamEx<String> components(String path) {
        return StreamEx.of(
                slashPattern
                        .splitAsStream(path)
                        .skip(1)); // the paths are absolute and the first elem will be "", before the first "/"
    }
}
