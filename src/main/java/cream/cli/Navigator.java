package cream.cli;

import fasttui.component.Container;
import fasttui.composable.ScrollVertical;

public class Navigator extends Container {

    public final Files files;
    private final ScrollVertical scroll;

    public Navigator(int cols, int rows) {
        super(0, 0, cols, rows - 4);
        int explorerHeight = rows - 4;
        this.scroll = new ScrollVertical(cols - 1, 0, explorerHeight, Theme.BORDER, Theme.BACKGROUND);
        this.files = new Files(0, 0, cols, explorerHeight);
        this.files.setScrollBar(this.scroll);
        this.files.setBackgroundColor(Theme.BACKGROUND);
        this.files.setForegroundColor(Theme.FOREGROUND);

        this.add(this.files);
        this.add(this.scroll);
    }

    public void relayout(int x, int width) {
        setX(x);
        setWidth(width);
        onResize();
    }

    public void onResize() {
        int w = getWidth();
        int h = getHeight();
        files.setWidth(w);
        files.setHeight(h);
        scroll.setX(getX() + w - 1);
        scroll.setHeight(h);
        files.syncScrollBar();
    }

    /**
     * Show/hide the navigator panel. The scrollbar visibility is managed by Files.syncScrollBar().
     */
    public void setPanelVisible(boolean visible) {
        setVisible(visible);
        if (!visible) scroll.setVisible(false);
    }

}
