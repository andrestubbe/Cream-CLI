package cream.cli;

import java.io.File;
import java.util.Locale;

public final class FileIcons {

    private FileIcons() {}

    public static String get(File file) {
        if (file == null) return Theme.ICON_DOCUMENT;
        final String name = file.getName();
        if (name.equals(FileConstants.PARENT_ENTRY)) return " ";
        if (file.isDirectory()) return Theme.ICON_FOLDER;
        final int dot = name.lastIndexOf('.');
        if (dot > 0 && dot < name.length() - 1) {
            final String ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
            final String icon = Theme.ICONS.get(ext);
            if (icon != null) return icon;
        }
        return Theme.ICON_DOCUMENT;
    }
}
