package cream.cli.view.footer;

import cream.cli.view.theme.ThemeService;
import cream.cli.view.ui.Cell;
import cream.cli.view.ui.Popup;
import cream.cli.view.ui.Row;
import fastterminal.FastTerminal;
import fasttui.component.ColorSet;

public class PopupService extends Popup {

    public PopupService(int cols, int rows) {
        super(7, -11, 25, 11);
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


        ColorSet rowBg = new ColorSet(ThemeService.get().getPopupBackgroundNormal(), ThemeService.get().getPopupBackgroundRowHover(), ThemeService.get().getPopupBackgroundRowHover(), ThemeService.get().getPopupBackgroundRowHover());
        ColorSet indicatorFg = new ColorSet(ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection());
        ColorSet normalFg = new ColorSet(ThemeService.get().getPopupForegroundNormal(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection());
        ColorSet infoFg = new ColorSet(ThemeService.get().getPopupMuted(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection());

        final ColorSet[] themes = {
                indicatorFg,
                normalFg,
                infoFg
        };

        for (int i = 0; i < content.length; i++) {
            Row row = new Row(2, 1 + i, this.width - 2, rowBg, () -> setVisible(false));
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