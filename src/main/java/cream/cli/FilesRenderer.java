package cream.cli;

import fastterminal.FastTerminalScene;

import java.io.File;

public final class FilesRenderer {

    private final FilesState state;

    public FilesRenderer(FilesState state) {
        this.state = state;
    }

    public void render(FastTerminalScene scene, int x, int y, int width, int height, int backgroundColor, int foregroundColor) {
        renderHeader(scene, x, y, width);
        renderList(scene, x, y, width, height, backgroundColor, foregroundColor);
        renderFooter(scene, x, y, width, height);
    }

    private void renderHeader(FastTerminalScene scene, int x, int y, int width) {
        final String path = FileFormatter.path(state.currentDirectory, width - 3);
        scene.writeString(x, y, Theme.ICON_OPEN_FOLDER + path, Theme.PATH_FOREGROUND, Theme.PATH_BACKGROUND);
    }

    private void renderList(FastTerminalScene scene, int x, int y, int width, int height, int background, int foreground) {
        final int visibleHeight = height - FileConstants.HEADER_HEIGHT - FileConstants.FOOTER_HEIGHT;
        if (visibleHeight <= 0) {
            return;
        }
        if (state.selectedIndex >= state.files.size()) {
            state.selectedIndex = state.files.size() - 1;
        }
        for (int i = 0; i < visibleHeight; i++) {
            final int index = state.scrollOffset + i;

            if (index >= state.files.size()) {
                break;
            }

            renderRow(scene, state.files.get(index), index, x, y + 1 + i, width, background, foreground);
        }
    }

    private void renderRow(FastTerminalScene scene, File file, int index, int x, int y, int width, int background, int foreground) {
        final boolean selected = state.selectedIndex == index;
        final int bg = selected ? Theme.FILE_SELECTION_BACKGROUND : background;
        final int fg = selected ? Theme.FILE_SELECTION_FOREGROUND : foreground;
        if (selected) {
            for (int c = x; c < x + width; c++) {
                scene.writeCell(c, y, ' ', fg, bg);
            }
        }
        final String text = FileFormatter.display(file, width - 12);
        scene.writeString(x + FileConstants.TEXT_PADDING_LEFT, y, text, fg, bg);
        final String info = FileFormatter.information(file);
        if (!info.isEmpty()) {
            final int sx = x + width - info.length() - FileConstants.SIZE_PADDING_RIGHT;
            if (sx > x + FileConstants.MIN_SIZE_COLUMN_WIDTH) {
                scene.writeString(sx, y, info, selected ? Theme.FILE_SELECTION_FOREGROUND : Theme.FILE_INFORMATION, bg);
            }
        }
    }

    private void renderFooter(FastTerminalScene scene, int x, int y, int width, int height) {
        scene.writeString(x + 1, y + height - 1, FileFormatter.footer(state.files.size()), Theme.FILE_FOOTER, Theme.TRANSPARENT);
    }
}
