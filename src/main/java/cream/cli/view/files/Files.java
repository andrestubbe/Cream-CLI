package cream.cli.view.files;

import fastterminal.FastTerminalScene;
import fasttui.component.Control;
import fasttui.composable.ScrollVertical;

public class Files extends Control {

    private final FilesState state;
    private final FilesDirectoryLoader loader;
    private final FilesSelection selection;
    private final FilesNavigation navigation;
    private final FilesRenderer renderer;

    private final Runnable repaintTrigger;
    private ScrollVertical scrollBar;

    public Files(int x, int y, int width, int height, Runnable repaintTrigger) {
        super(x, y, width, height);
        this.repaintTrigger = repaintTrigger;
        this.state = new FilesState();
        this.loader = new FilesDirectoryLoader();
        this.selection = new FilesSelection(state);
        this.navigation = new FilesNavigation(state, loader, repaintTrigger);
        this.renderer = new FilesRenderer(state);
        this.loader.load(state, repaintTrigger);
        this.addBehavior(new FilesBehaviour(this));
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        this.syncScrollBar();

        // 1. Draw container background first
        if (backgroundColor != -1) {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    scene.writeCell(x + c, y + r, ' ', -1, backgroundColor);
                }
            }
        }

        // 2. Render files list
        this.renderer.render(scene, this.x, this.y, this.width, this.height, this.backgroundColor, this.foregroundColor);

        // 3. Render child components on top
        for (fasttui.component.Component child : children) {
            if (child.isVisible()) {
                child.render(scene);
            }
        }
    }

    public void refreshFiles() {
        loader.load(state);
    }

    public void toggleShowHidden() {
        state.showHidden = !state.showHidden;
        refreshFiles();
    }

    public void scroll(int delta) {
        selection.scroll(delta, height);
        syncScrollBar();
    }

    public void selectPrevious() {
        selectPrevious(false);
    }

    public void selectPrevious(boolean shift) {
        selection.previous(height, shift);
        syncScrollBar();
    }

    public void selectNext() {
        selectNext(false);
    }

    public void selectNext(boolean shift) {
        selection.next(height, shift);
        syncScrollBar();
    }

    public boolean activateSelected() {
        return navigation.openSelected();
    }

    public void navigateUp() {
        navigation.up();
    }

    public void hoverFile(String name) {
        selection.hover(name, height);
    }

    public void hoverFilePath(String path) {
        selection.hoverPath(path, height);
    }

    public void setFileOpenListener(FileOpenListener listener) {
        navigation.setFileOpenListener(listener);
    }

    public void setDirectoryChangeListener(java.util.function.Consumer<java.io.File> listener) {
        navigation.setDirectoryChangeListener(listener);
    }

    public void navigateTo(java.io.File dir) {
        if (dir == null || !dir.isDirectory()) return;
        state.currentDirectory = dir;
        loader.load(state, repaintTrigger);
    }

    public void setScrollBar(ScrollVertical scrollBar) {
        this.scrollBar = scrollBar;
        if (this.scrollBar != null) {
            this.scrollBar.setScrollListener(offset -> {
                state.scrollOffset = offset;
                this.syncScrollBar();
                if (repaintTrigger != null) {
                    repaintTrigger.run();
                }
            });
        }
    }

    public void setCurrentDirectory(String path) {
        navigation.setDirectory(path);
    }

    public void setCurrentDirectorySync(String path) {
        navigation.setDirectorySync(path);
    }

    public void syncScrollBar() {
        if (scrollBar == null) {
            return;
        }

        final int visible = height;
        final boolean needed = state.files.size() > visible;

        scrollBar.setVisible(needed);

        if (needed) {
            scrollBar.update(state.files.size(), visible, state.scrollOffset);
        }
    }

    public int getScrollOffset() {
        return state.scrollOffset;
    }

    public int getFileCount() {
        return state.files.size();
    }

    public void select(int index) {
        if (index >= 0 && index < state.files.size()) {
            state.selectedIndex = index;
        }
    }

    public FilesState getState() {
        return state;
    }
}
