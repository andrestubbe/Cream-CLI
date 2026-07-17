package cream.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FilesDirectoryLoader {

    private static final Comparator<File> SORT = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);

    public void load(FilesState state) {
        state.files.clear();
        final File directory = state.currentDirectory;
        if (directory == null) {
            return;
        }
        addParent(directory, state.files);
        addFiles(directory, state.files);
        state.selectedIndex = -1;
        state.scrollOffset = 0;
    }

    private void addParent(File directory, List<File> target) {
        if (directory.getParentFile() != null) {
            target.add(new File(directory, FileConstants.PARENT_ENTRY));
        }
    }

    private void addFiles(File directory, List<File> target) {
        final File[] list = directory.listFiles();
        if (list == null) {
            return;
        }
        final List<File> directories = new ArrayList<>();
        final List<File> files = new ArrayList<>();
        for (File file : list) {
            if (file.isHidden()) {
                continue;
            }
            if (file.isDirectory()) {
                directories.add(file);
            } else {
                files.add(file);
            }
        }
        directories.sort(SORT);
        files.sort(SORT);
        target.addAll(directories);
        target.addAll(files);
    }
}
