package cream.cli;

import fasttui.composable.TreeView;
import fasttui.component.Container;
import fasttui.composable.ScrollVertical;

import java.io.File;

public class TreeNavigator extends Container {

    public final TreeView tree;
    private final ScrollVertical scroll;

    public TreeNavigator(int cols, int rows) {
        super(0, 0, cols, rows - 4);
        int treeHeight = rows - 4;
        this.scroll = new ScrollVertical(cols - 1, 0, treeHeight, Theme.SCROLL_FOREGROUND, Theme.SCROLL_BACKGROUND);
        this.tree = new TreeView(0, 0, cols - 1, treeHeight);

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

    public void setRoot(File rootFile) {
        this.tree.setRoot(rootFile);
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
        tree.setHeight(h);
        scroll.setX(getX() + w - 1);
        scroll.setHeight(h);
    }

    public void relayout(int x, int width) {
        setX(x);
        setWidth(width);
        onResize();
    }
}
