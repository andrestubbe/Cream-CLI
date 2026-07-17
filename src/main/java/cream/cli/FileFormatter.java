package cream.cli;

import java.io.File;

public final class FileFormatter {

    private FileFormatter() {}

    public static String name(File file) {
        if (file == null) return "";
        if (file.getName().equals(FileConstants.PARENT_ENTRY)) return "↑ ..";
        return file.getName();
    }

    public static String information(File file) {
        if (file == null) return "";
        if (file.isDirectory()) return " <DIR> ";
        return " " + FileUtil.formatFileSize(file.length()) + " ";
    }

    public static String display(File file, int maxWidth) {
        final String text = " " + FileIcons.get(file) + name(file);
        if (text.length() <= maxWidth) return text;
        return text.substring(0, Math.max(0, maxWidth - 3)) + "...";
    }

    public static String footer(int count) {
        return " " + count + " items ";
    }

    public static String path(File directory, int width) {
        return FileUtil.truncatePath(directory.getAbsolutePath(), width);
    }
}
