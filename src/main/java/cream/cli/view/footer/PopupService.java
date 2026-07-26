package cream.cli.view.footer;

import cream.cli.Theme;
import cream.cli.view.ui.Cell;
import cream.cli.view.ui.Popup;
import cream.cli.view.ui.Row;
import fasttui.component.ColorSet;

public class PopupService extends Popup {

    public PopupService(int cols, int rows) {
        super(8, rows - 12, 25, 11);
        this.createList();
    }

    private void createList() {

        String[][] content = {
                {"❯", "llama", "locally"},
                {"❯", "Ollama", "locally"},
                {"❯", "LM Studio", "locally"},
                {"❯", "Anthropic", "online"},
                {"❯", "DeepSeek", "online"},
                {"❯", "Gemini", "online"},
                {"❯", "Grok", "online"},
                {"❯", "Mistral", "online"},
                {"❯", "OpenAI", "online"}
        };

        final int margin = 1;
        final int[] length = columnLengths(content);
        final int space = 2;
        final int[] widths = {2, length[1] + space, length[2] + space};
        this.width = widths[0] + widths[1] + widths[2] + margin * 2;


        final ColorSet POPUP_BACKGROUND_ROW_SET = Theme.POPUP_BACKGROUND_ROW_SET;
        final ColorSet[] themes = {
                Theme.POPUP_FOREGROUND_INDICATOR_SET,
                Theme.POPUP_FOREGROUND_NORMAL_SET,
                Theme.POPUP_FOREGROUND_INFORMATION_SET
        };

        for (int i = 0; i < content.length; i++) {
            Row row = new Row(2, 1 + i, this.width - 2, POPUP_BACKGROUND_ROW_SET, () -> setVisible(false));
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
        int max2 = 0;
        for (String[] row : content) {
            if (row[0].length() > max0) max0 = row[0].length();
            if (row[1].length() > max1) max1 = row[1].length();
            if (row[2].length() > max2) max2 = row[2].length();
        }
        return new int[]{max0, max1, max2};
    }

}