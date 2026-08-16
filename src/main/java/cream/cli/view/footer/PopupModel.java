package cream.cli.view.footer;

import cream.cli.view.theme.ThemeService;
import cream.cli.view.ui.Cell;
import cream.cli.view.ui.Popup;
import cream.cli.view.ui.Row;
import fasttui.component.ColorSet;

public class PopupModel extends Popup {

    private final int totalRows;

    public PopupModel(int cols, int rows) {
        super(17, -14, 40, 14);
        this.totalRows = rows;
        this.updateForService("llama");
    }

    public void updateForService(String serviceName) {
        this.clearRows();

        final String[][] content = switch (serviceName.toLowerCase()) {
            case "anthropic" -> new String[][]{
                    {"❯", "claude-3-5-sonnet-latest", "online"},
                    {"❯", "claude-3-5-haiku-latest", "online"},
                    {"❯", "claude-3-opus-latest", "online"}
            };
            case "openai" -> new String[][]{
                    {"❯", "gpt-4o", "online"},
                    {"❯", "gpt-4o-mini", "online"},
                    {"❯", "o1", "online"},
                    {"❯", "o3-mini", "online"}
            };
            case "deepseek" -> new String[][]{
                    {"❯", "deepseek-chat", "online"},
                    {"❯", "deepseek-reasoner", "online"}
            };
            case "gemini" -> new String[][]{
                    {"❯", "gemini-2.0-flash", "online"},
                    {"❯", "gemini-1.5-pro", "online"}
            };
            default -> new String[][]{
                    {"❯", "qwen2.5:3b", "1.9 GB"},
                    {"❯", "llama3.2:3b", "2.0 GB"},
                    {"❯", "phi4-mini:3.8b", "2.5 GB"},
                    {"❯", "smollm2:1.7b", "1.8 GB"},
                    {"❯", "moondream:latest", "1.7 GB"}
            };
        };

        final int margin = 1;
        final int[] length = columnLengths(content);
        final int space = 2;
        final int[] widths = {2, length[1] + space, length[2] + space};
        this.width = widths[0] + widths[1] + widths[2] + margin * 2;
        this.height = Math.min(14, content.length + 2);
        this.setY(-this.height);

        ColorSet rowBg = new ColorSet(ThemeService.get().getPopupBackgroundNormal(), ThemeService.get().getPopupBackgroundRowHover(), ThemeService.get().getPopupBackgroundRowHover(), ThemeService.get().getPopupBackgroundRowHover());
        ColorSet indicatorFg = new ColorSet(ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection());
        ColorSet normalFg = new ColorSet(ThemeService.get().getPopupForegroundNormal(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection());
        ColorSet infoFg = new ColorSet(ThemeService.get().getPopupMuted(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection(), ThemeService.get().getPopupForegroundSelection());

        final ColorSet[] POPUP_FOREGROUND_ROW_SETS = {
                indicatorFg,
                normalFg,
                infoFg
        };

        for (int i = 0; i < content.length; i++) {
            final int px = margin;
            final int py = margin + i;
            final int pWidth = this.width - margin * 2;
            final Row row = new Row(px, py, pWidth, rowBg, () -> setVisible(false));
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