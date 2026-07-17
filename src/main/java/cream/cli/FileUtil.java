package cream.cli;

import java.text.DecimalFormat;

public class FileUtil {

    public static String truncatePath(String path, int maxLen) {
        if (path.length() <= maxLen) return path;
        return "..." + path.substring(path.length() - maxLen + 3);
    }

    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        final int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
