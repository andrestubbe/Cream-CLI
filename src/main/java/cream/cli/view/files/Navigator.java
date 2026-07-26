package cream.cli.view.files;

import cream.cli.Theme;
import fasttui.component.Container;
import fasttui.composable.ScrollVertical;

/**
 * Pure TUI View for Files Navigator panel in CreamCLI (MVC Architecture).
 * Contains sub-views (ColumnHeader, Files, ScrollVertical, FilesFooter).
 */
public class Navigator extends Container {

    public final ColumnHeader columnHeader;
    public final Files files;
    public final FilesFooter footer;
    private final ScrollVertical scroll;

    public Navigator(int cols, int rows, Runnable repaintTrigger) {
        super(0, 0, cols, rows - 4);
        int explorerHeight = rows - 4;

        this.files = new Files(0, 2, cols - 1, explorerHeight - 3, repaintTrigger);
        this.columnHeader = new ColumnHeader(0, 1, cols);
        this.footer = new FilesFooter(0, explorerHeight - 1, cols, this.files.getState());

        this.scroll = new ScrollVertical(cols - 1, 2, explorerHeight - 3, Theme.SCROLLBAR_FOREGROUND_SET, Theme.SCROLLBAR_BACKGROUND_SET);
        this.files.setScrollBar(this.scroll);
        this.files.setBackgroundColor(Theme.BACKGROUND);
        this.files.setForegroundColor(Theme.FOREGROUND);

        this.add(this.columnHeader);
        this.add(this.files);
        this.add(this.scroll);
        this.add(this.footer);
    }

    public void relayout(int x, int width) {
        setX(x);
        setWidth(width);
        onResize();
    }

    public void onResize() {
        int w = getWidth();
        int h = getHeight();

        if (columnHeader != null) columnHeader.setWidth(w);

        if (files != null) {
            files.setWidth(w - 1);
            files.setHeight(h - 3);
        }
        if (scroll != null) {
            scroll.setX(getX() + w - 1);
            scroll.setHeight(h - 3);
        }
        if (footer != null) {
            footer.setY(getY() + h - 1);
            footer.setWidth(w);
        }
        if (files != null) {
            files.syncScrollBar();
        }
    }

    public void setPanelVisible(boolean visible) {
        setVisible(visible);
        if (!visible) scroll.setVisible(false);
    }

    public Files getFiles() {
        return files;
    }
}
