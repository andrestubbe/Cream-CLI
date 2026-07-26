package cream.cli.view.footer;

import cream.cli.Theme;
import cream.cli.view.ui.Cell;
import cream.cli.view.ui.Popup;
import cream.cli.view.ui.Row;
import fasttui.component.ColorSet;

public class PopupModel extends Popup {

    public PopupModel(int cols, int rows) {
        super(18, rows - 15, 40, 14);

        final String[][] content = {
                {"❯", "moondream:latest", "1.7 GB"},
                {"❯", "qwen3:1.7b", "1.4 GB"},
                {"❯", "qwen2.5:1.5b", "986 MB"},
                {"❯", "qwen2.5-coder:3b", "1.9 GB"},
                {"❯", "qwen2.5-coder:0.5b", "397 MB"},
                {"❯", "qwen2.5-coder:1.5b", "986 MB"},
                {"❯", "qwen2.5:3b", "1.9 GB"},
                {"❯", "phi4-mini:3.8b", "2.5 GB"},
                {"❯", "phi3:3.8b", "2.2 GB"},
                {"❯", "smollm2:1.7b", "1.8 GB"},
                {"❯", "llama3.2:3b", "2.0 GB"},
                {"❯", "moondream:latest", "1.7 GB"},
                {"❯", "qwen3:1.7b", "1.4 GB"},
                {"❯", "qwen2.5:1.5b", "986 MB"},
                {"❯", "qwen2.5-coder:3b", "1.9 GB"},
                {"❯", "qwen2.5-coder:0.5b", "397 MB"},
                {"❯", "qwen2.5-coder:1.5b", "986 MB"},
                {"❯", "qwen2.5:3b", "1.9 GB"},
                {"❯", "phi4-mini:3.8b", "2.5 GB"},
                {"❯", "phi3:3.8b", "2.2 GB"},
                {"❯", "smollm2:1.7b", "1.8 GB"},
                {"❯", "llama3.2:3b", "2.0 GB"},
                {"❯", "llama3.2:1b", "1.3 GB"}};

        final int margin = 1;
        final int[] length = columnLengths(content);
        final int space = 2;
        final int[] widths = {2, length[1] + space, length[2] + space};
        this.width = widths[0] + widths[1] + widths[2] + margin * 2;

        final ColorSet POPUP_BACKGROUND_ROW_SET = Theme.POPUP_BACKGROUND_ROW_SET;
        final ColorSet[] POPUP_FOREGROUND_ROW_SETS = {
                Theme.POPUP_FOREGROUND_INDICATOR_SET,
                Theme.POPUP_FOREGROUND_NORMAL_SET,
                Theme.POPUP_FOREGROUND_INFORMATION_SET
        };

        for (int i = 0; i < content.length; i++) {
            final int px = margin;
            final int py = margin + i;
            final int pWidth = this.width - margin * 2;
            final Row row = new Row(px, py, pWidth, POPUP_BACKGROUND_ROW_SET, () -> setVisible(false));
            for (int col = 0; col < content[i].length; col++) {
                final String text = content[i][col];
                final int width = widths[col];
                final ColorSet colorSet = POPUP_FOREGROUND_ROW_SETS[col];
                final Cell cell = new Cell(text, width, colorSet);
                row.addCell(cell);
            }
            this.addRow(row);
        }
    }

    public int[] columnLengths(final String[][] content) {
        int max0 = 0;
        int max1 = 0;
        int max2 = 0;
        for (String[] row : content) {
            if (row[0].length() > max0) max0 = row[0].length();
            if (row[1].length() > max1) max1 = row[1].length();
            if (row[2].length() > max2) max2 = row[2].length();
        }
        return new int[]{max0, max1, max2};
    }

}