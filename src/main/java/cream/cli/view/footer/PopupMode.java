package cream.cli.view.footer;

import cream.cli.Theme;
import cream.cli.view.ui.Cell;
import cream.cli.view.ui.Popup;
import cream.cli.view.ui.Row;
import fasttui.component.ColorSet;

public class PopupMode extends Popup {

    public PopupMode(int cols, int rows) {
        super(1, rows - 7, 14, 6);
        this.backgroundColor = Theme.OMNIBOX_MODE_BACKGROUND_SET.normal;
        this.foregroundColor = Theme.OMNIBOX_MODE_FOREGROUND_SET.normal;
        this.createList();
    }

    private void createList() {
        String[][] content = {
                {"❯", "Auto"},
                {"❯", "Agent"},
                {"❯", "Bot"},
                {"❯", "Command"}
        };

        final int margin = 1;
        final int[] length = columnLengths(content);
        final int space = 2;
        final int[] widths = {2, length[1] + space};
        this.width = widths[0] + widths[1] + margin * 2;

        final ColorSet POPUP_BACKGROUND_ROW_SET = Theme.POPUP_BACKGROUND_ROW_SET;
        ColorSet[] themes = {
                Theme.POPUP_FOREGROUND_INDICATOR_SET,
                Theme.POPUP_FOREGROUND_NORMAL_SET
        };

        for (int i = 0; i < content.length; i++) {
            final int px = margin;
            final int py = margin + i;
            final int pWidth = this.width - margin * 2;
            Row row = new Row(px, py, pWidth, POPUP_BACKGROUND_ROW_SET, () -> setVisible(false));
            for (int col = 0; col < content[i].length; col++) {
                Cell cell = new Cell(content[i][col], widths[col], themes[col]);
                row.addCell(cell);
            }
            this.addRow(row);
        }
    }

    public int[] columnLengths(final String[][] content) {
        int max0 = 0;
        int max1 = 0;
        for (String[] row : content) {
            if (row[0].length() > max0) max0 = row[0].length();
            if (row[1].length() > max1) max1 = row[1].length();
        }
        return new int[]{max0, max1};
    }
}