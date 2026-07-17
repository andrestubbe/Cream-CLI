package cream.cli;

import java.io.File;

public final class FilesSelection {

    private final FilesState state;

    public FilesSelection(FilesState state) {
        this.state = state;
    }

    public void previous(int visibleHeight) {
        if (state.files.isEmpty()) {
            return;
        }
        if (state.selectedIndex > 0) {
            state.selectedIndex--;
        } else if (state.selectedIndex < 0) {
            state.selectedIndex = 0;
        }
        ensureVisible(visibleHeight);
    }

    public void next(int visibleHeight) {
        if (state.files.isEmpty()) {
            return;
        }
        if (state.selectedIndex < state.files.size() - 1) {
            state.selectedIndex++;
        } else if (state.selectedIndex < 0) {
            state.selectedIndex = 0;
        }
        ensureVisible(visibleHeight);
    }

    public void hover(String filename, int visibleHeight) {
        if (filename == null) {
            return;
        }
        for (int i = 0; i < state.files.size(); i++) {
            if (state.files.get(i).getName().equalsIgnoreCase(filename)) {
                state.selectedIndex = i;
                ensureVisible(visibleHeight);
                return;
            }
        }
    }

    public void hoverPath(String path, int visibleHeight) {
        if (path == null) {
            return;
        }
        final String target = new File(path).getAbsolutePath();
        for (int i = 0; i < state.files.size(); i++) {
            if (state.files.get(i).getAbsolutePath().equals(target)) {
                state.selectedIndex = i;
                ensureVisible(visibleHeight);
                return;
            }
        }
    }

    public void ensureVisible(int visibleHeight) {
        if (visibleHeight <= 0) {
            return;
        }
        if (state.selectedIndex < state.scrollOffset) {
            state.scrollOffset = state.selectedIndex;
        } else if (state.selectedIndex >= state.scrollOffset + visibleHeight) {
            state.scrollOffset = state.selectedIndex - visibleHeight + 1;
        }
    }

    public void scroll(int delta, int visibleHeight) {
        if (visibleHeight <= 0) {
            return;
        }
        final int max = Math.max(0, state.files.size() - visibleHeight);
        state.scrollOffset = Math.max(0, Math.min(state.scrollOffset + delta, max));
    }

    public File selected() {
        if (state.selectedIndex < 0 || state.selectedIndex >= state.files.size()) {
            return null;
        }
        return state.files.get(state.selectedIndex);
    }
}
