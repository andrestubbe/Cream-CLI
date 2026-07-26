package cream.cli.view.files;

import cream.cli.model.FileCategory;
import cream.cli.view.editor.EditorFileManager;
import java.io.File;
import java.util.Locale;

public final class FilesNavigation {

    private final FilesState state;
    private final FilesDirectoryLoader loader;
    private final Runnable repaintTrigger;
    private FileOpenListener listener;

    public FilesNavigation(FilesState state, FilesDirectoryLoader loader, Runnable repaintTrigger) {
        this.state = state;
        this.loader = loader;
        this.repaintTrigger = repaintTrigger;
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
            syncWorkspaceModel();
            loader.load(state, repaintTrigger);
            EditorFileManager.saveDirectoryState(selected);
            return true;
        }
        openFile(selected);
        return false;
    }

    public boolean up() {
        if (state.currentDirectory == null) {
            return false;
        }
        final File parent = state.currentDirectory.getParentFile();
        state.currentDirectory = parent;
        syncWorkspaceModel();
        loader.load(state, repaintTrigger);
        if (parent != null) EditorFileManager.saveDirectoryState(parent);
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
        syncWorkspaceModel();
        loader.load(state, repaintTrigger);
    }

    public void setDirectorySync(String path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        path = normalize(path);
        final File directory = new File(path).getAbsoluteFile();
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        state.currentDirectory = directory;
        syncWorkspaceModel();
        loader.loadSync(state, repaintTrigger);
    }

    private void syncWorkspaceModel() {
        if (state.workspaceModel != null && state.currentDirectory != null) {
            state.workspaceModel.setCurrentDirectorySilently(state.currentDirectory.toPath());
        }
    }

    private void openFile(File file) {
        if (listener == null) {
            return;
        }
        if (FileCategory.fromPath(file.getName()).isOpenableInEditor()) {
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
