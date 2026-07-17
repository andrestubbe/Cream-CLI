package cream.cli;

import fastterminal.FastTerminalScene;
import fasttui.component.Control;
import fasttui.composable.ScrollVertical;

public class Files extends Control {

    private final FilesState state;
    private final FilesDirectoryLoader loader;
    private final FilesSelection selection;
    private final FilesNavigation navigation;
    private final FilesRenderer renderer;

    private ScrollVertical scrollBar;

    public Files(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.state = new FilesState();
        this.loader = new FilesDirectoryLoader();
        this.selection = new FilesSelection(state);
        this.navigation = new FilesNavigation(state, loader);
        this.renderer = new FilesRenderer(state);
        this.loader.load(state);
        this.addBehavior(new FilesBehaviour(this));
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;
        this.syncScrollBar();
        this.renderer.render(scene, this.x, this.y, this.width, this.height, this.backgroundColor, this.foregroundColor);
    }

    public void refreshFiles() {
        loader.load(state);
    }

    public void scroll(int delta) {
        selection.scroll(delta, height - 2);
        syncScrollBar();
    }

    public void selectPrevious() {
        selection.previous(height - 2);
        syncScrollBar();
    }

    public void selectNext() {
        selection.next(height - 2);
        syncScrollBar();
    }

    public boolean activateSelected() {
        return navigation.openSelected();
    }

    public void navigateUp() {
        navigation.up();
    }

    public void hoverFile(String name) {
        selection.hover(name, height - 2);
    }

    public void hoverFilePath(String path) {
        selection.hoverPath(path, height - 2);
    }

    public void setFileOpenListener(FileOpenListener listener) {
        navigation.setFileOpenListener(listener);
    }

    public void setScrollBar(ScrollVertical scrollBar) {
        this.scrollBar = scrollBar;
    }

    public void setCurrentDirectory(String path) {
        navigation.setDirectory(path);
    }

    public void syncScrollBar() {
        if (scrollBar == null) {
            return;
        }

        final int visible = height - 2;
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
}
