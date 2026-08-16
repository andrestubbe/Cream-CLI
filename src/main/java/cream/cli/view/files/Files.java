package cream.cli.view.files;

import cream.cli.view.theme.ThemeService;
import fasttui.component.ColorSet;
import fasttui.component.Container;
import fasttui.composable.ScrollVertical;

public class Files extends Container {

    private static final int transparent = -2;
    private static final ColorSet scrollFg = new ColorSet(
            ThemeService.get().getScrollbarForegroundNormal(),
            ThemeService.get().getScrollbarForegroundHover(),
            ThemeService.get().getScrollbarForegroundNormal(),
            ThemeService.get().getScrollbarForegroundNormal());
    private static final ColorSet scrollBg = new ColorSet(
            transparent,
            ThemeService.get().getScrollbarBackgroundHover(),
            transparent,
            transparent);

    private final FilesHeader filesHeader;
    private final FilesList filesList;
    private final FilesFooter filesFooter;
    private final ScrollVertical filesListScroll;

    public Files(final int x, final int y, final int width, final int height, final Runnable repaintTrigger) {
        super(x, y, width, height);
        int filesListHeight = this.height - 2;
        this.filesHeader = new FilesHeader(0, 0, this.width, 1);
        this.filesListScroll = new ScrollVertical(this.width - 1, 1, filesListHeight, scrollFg, scrollBg);
        this.filesList = new FilesList(0, 1, this.width - 1, filesListHeight, repaintTrigger);
        this.filesList.setScrollBar(this.filesListScroll);
        this.filesFooter = new FilesFooter(0, this.height - 1, this.width, 1, this.filesList.getFilesState());
        this.add(this.filesHeader);
        this.add(this.filesList);
        this.add(this.filesListScroll);
        this.add(this.filesFooter);
    }

    public void onResize() {
        final int w = getWidth();
        final int h = getHeight();
        if (this.filesHeader != null) {
            this.filesHeader.setWidth(w);
        }
        if (this.filesList != null) {
            this.filesList.setWidth(w - 1);
            this.filesList.setHeight(h - 2);
        }
        if (this.filesListScroll != null) {
            this.filesListScroll.setX(getX() + w - 1);
            this.filesListScroll.setHeight(h - 2);
        }
        if (this.filesFooter != null) {
            this.filesFooter.setY(getY() + h - 1);
            this.filesFooter.setWidth(w);
        }
        if (this.filesList != null) {
            this.filesList.syncScrollBar();
        }
    }

    public FilesHeader getFilesHeader() {
        return this.filesHeader;
    }

    public FilesList getFilesList() {
        return this.filesList;
    }

    public FilesFooter getFilesFooter() {
        return this.filesFooter;
    }

}
