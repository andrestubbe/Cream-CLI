package cream.cli;

import fasttui.composable.TreeView;
import fasttui.component.Container;
import fasttui.composable.ScrollVertical;
import fastterminal.FastTerminalScene;

import java.io.File;

public class TreeNavigator extends Container {

    public final TreeView tree;
    private final ScrollVertical scroll;
    private String currentPath = "";

    public TreeNavigator(int cols, int rows) {
        super(0, 0, cols, rows - 4);
        int treeHeight = rows - 5; // -4 for input, -1 for URL header
        this.scroll = new ScrollVertical(cols - 1, 1, treeHeight, Theme.SCROLL_FOREGROUND, Theme.SCROLL_BACKGROUND);
        this.tree = new TreeView(0, 1, cols - 1, treeHeight);

        this.tree.setTreeColorDirectory(0x7AA2F7);
        this.tree.setTreeColorFile(0x9DC278);
        this.tree.setTreeColorSelected(0xFFFFFF);
        this.tree.setTreeColorBackgroundSelected(0x264F78);

        this.tree.setSelectionListener(node -> {
            if (node.getFile().isFile()) {
                // File selected - could open in editor
            }
        });

        this.add(this.tree);
        this.add(this.scroll);
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!visible) return;

        // Render URL header
        String pathDisplay = Theme.ICON_OPEN_FOLDER + currentPath;
        if (pathDisplay.length() > width) {
            pathDisplay = pathDisplay.substring(0, width);
        }
        scene.writeString(x, y, pathDisplay, Theme.PATH_FOREGROUND, Theme.PATH_BACKGROUND);

        // Render children (tree and scroll)
        super.render(scene);
    }

    public void setRoot(File rootFile) {
        this.tree.setRoot(rootFile);
        this.currentPath = rootFile.getAbsolutePath();
        this.tree.expandPath(rootFile);
    }

    public void selectNext() {
        tree.selectNext();
        syncScrollBar();
    }

    public void selectPrevious() {
        tree.selectPrevious();
        syncScrollBar();
    }

    public void toggleExpanded() {
        tree.toggleExpanded();
        syncScrollBar();
    }

    public void activateSelected() {
        tree.activateSelected();
    }

    private void syncScrollBar() {
        // TreeView doesn't expose visible nodes count directly, so we skip scrollbar for now
        // Could be added later if needed
    }

    public void onResize() {
        int w = getWidth();
        int h = getHeight();
        tree.setWidth(w - 1);
        tree.setHeight(h - 1);
        scroll.setX(getX() + w - 1);
        scroll.setY(1);
        scroll.setHeight(h - 1);
    }

    public void relayout(int x, int width) {
        setX(x);
        setWidth(width);
        onResize();
    }
}
