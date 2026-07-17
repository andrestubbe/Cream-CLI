package cream.cli;

import java.io.File;
import java.util.Locale;

public final class FilesNavigation {

    private final FilesState state;
    private final FilesDirectoryLoader loader;
    private FileOpenListener listener;

    public FilesNavigation(FilesState state, FilesDirectoryLoader loader) {
        this.state = state;
        this.loader = loader;
    }

    public void setFileOpenListener(FileOpenListener listener) {
        this.listener = listener;
    }

    public boolean openSelected() {
        final File selected = selected();
        if (selected == null) {
            return false;
        }
        if (isParent(selected)) {
            return up();
        }
        if (selected.isDirectory()) {
            state.currentDirectory = selected;
            loader.load(state);
            return true;
        }
        openFile(selected);
        return false;
    }

    public boolean up() {
        final File parent = state.currentDirectory.getParentFile();
        if (parent == null) {
            return false;
        }
        state.currentDirectory = parent;
        loader.load(state);
        return true;
    }

    public void setDirectory(String path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        path = normalize(path);
        final File directory = new File(path).getAbsoluteFile();
        if (!directory.exists()) {
            return;
        }
        if (!directory.isDirectory()) {
            return;
        }
        state.currentDirectory = directory;
        loader.load(state);
    }

    private void openFile(File file) {
        if (listener == null) {
            return;
        }
        final String name = file.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".java") || name.endsWith(".txt") || name.endsWith(".md")) {
            listener.onFileOpen(file);
        }
    }

    private File selected() {
        if (state.selectedIndex < 0 || state.selectedIndex >= state.files.size()) {
            return null;
        }
        return state.files.get(state.selectedIndex);
    }

    private boolean isParent(File file) {
        return file.getName().equals(FileConstants.PARENT_ENTRY);
    }

    private String normalize(String path) {
        if (path.matches("^[A-Za-z]:$")) {
            return path + "\\";
        }
        if (path.matches("^[A-Za-z]$")) {
            return path + ":\\";
        }
        return path;
    }
}
