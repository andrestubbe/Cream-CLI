package cream.cli.view.omnibox;

import cream.cli.view.theme.ThemeService;
import fasttui.behaviour.Behaviour;
import fasttui.component.*;
import fasttui.composable.MultilineTextBox;
import fasttui.composable.TextInput.StateChangeListener;

import java.util.ArrayList;
import java.util.List;

public final class Omnibox extends Container implements Interactive {

    private static final String OMNIBOX_TEXT_PLACEHOLDER = "Command or Ask anything ...";
    private static final String OMNIBOX_SYMBOL = "❯";

    private static final int transparent = -2;
    private static final int Foreground = ThemeService.get().getForeground();
    private static final int PopupForegroundSelection = ThemeService.get().getPopupForegroundSelection();
    private static final int FilesFileSelectionBackground = ThemeService.get().getFilesFileSelectionBackground();
    private static final int OmniboxBoxBorderNormal = ThemeService.get().getOmniboxBoxBorderNormal();
    private static final ColorSet textFg = new ColorSet(
            ThemeService.get().getOmniboxTextForegroundNormal(),
            ThemeService.get().getOmniboxTextForegroundNormal(),
            ThemeService.get().getOmniboxTextForegroundNormal(),
            ThemeService.get().getOmniboxTextForegroundNormal());
    private static final ColorSet placeholderFg = new ColorSet(
            ThemeService.get().getOmniboxInformationForeground(),
            ThemeService.get().getOmniboxInformationForeground(),
            ThemeService.get().getOmniboxInformationForeground(),
            ThemeService.get().getOmniboxInformationForeground());
    private static final ColorSet textBg = new ColorSet(
            transparent,
            transparent,
            transparent,
            transparent);

    public final Box box;
    public final MultilineTextBox text;
    private final TextField symbol;
    private final TextField contextChip;
    private final List<Behaviour> behaviors = new ArrayList<>();

    private String attachedContextText = null;
    private String chipLabel = null;

    public Omnibox(int x, int y, int width, int height) {
        super(x, y, width, height);

        this.box = new Box(0, 0, this.width, 3);
        this.box.setBorderStyle(BorderStyle.ROUNDED);
        this.box.setBackgroundColor(transparent);
        this.box.setBorderColor(OmniboxBoxBorderNormal);

        this.symbol = new TextField(2, 1, OMNIBOX_SYMBOL, Foreground);
        this.symbol.setBackgroundColor(transparent);

        this.contextChip = new TextField(4, 1, "", PopupForegroundSelection);
        this.contextChip.setBackgroundColor(FilesFileSelectionBackground);
        this.contextChip.setVisible(false);

        final int textMargin = 3;
        final int textWidth = this.width - textMargin * 2;
        final int textHeight = 1;
        this.text = new MultilineTextBox(4, 1, textWidth, textHeight);
        this.text.setPlaceholder(OMNIBOX_TEXT_PLACEHOLDER);
        this.text.setBackgroundSet(textBg);

        this.text.setForegroundSet(textFg);
        this.text.setColorCaretBg(ThemeService.get().getOmniboxCaretBg());
        this.text.setColorCaretFg(ThemeService.get().getOmniboxCaretFg());
        this.text.setPlaceholderForegroundSet(placeholderFg);
        this.text.addStateChangeListener(this.getTextStateChangeListener());

        this.behaviors.add(new OmniBoxTextBehaviour(this.text));

        this.add(this.box);
        this.add(this.symbol);
        this.add(this.contextChip);
        this.add(this.text);
    }

    public void setContextChip(String label, String contextContent) {
        if (label != null && !label.isEmpty()) {
            this.chipLabel = label;
            String chipText = " 📄 " + label + " ✕ ";
            this.contextChip.setText(chipText);

            int visualWidth = 0;
            for (int i = 0; i < chipText.length(); ) {
                int cp = chipText.codePointAt(i);
                visualWidth += fastemojis.FastEmojis.getWidth(cp);
                i += Character.charCount(cp);
            }

            this.contextChip.setWidth(visualWidth);
            this.contextChip.setX(4);
            this.contextChip.setY(1);
            this.contextChip.setVisible(true);
            this.attachedContextText = contextContent;

            // When context chip is active, place multiline text box on second line (row 2)
            this.text.setX(4);
            this.text.setWidth(Math.max(10, this.width - 6));
            this.text.setY(2);
            adjustHeight(this.y + this.height + 1);
        } else {
            clearContextChip();
        }
    }

    public void clearContextChip() {
        this.contextChip.setVisible(false);
        this.contextChip.setText("");
        this.attachedContextText = null;
        this.chipLabel = null;
        this.text.setX(4);
        this.text.setY(1);
        this.text.setWidth(Math.max(10, this.width - 6));
        adjustHeight(this.y + this.height + 1);
    }

    public String getAttachedContextText() {
        return attachedContextText;
    }

    public String getChipLabel() {
        return chipLabel;
    }

    private StateChangeListener getTextStateChangeListener() {
        return source -> {
            int transparent = -2;
            if (source.isFocused()) {
                this.setBoxColor(transparent, ThemeService.get().getOmniboxBoxBorderFocus());
            } else if (source.isHovered()) {
                this.setBoxColor(transparent, ThemeService.get().getOmniboxBoxBorderHover());
            } else {
                this.setBoxColor(transparent, ThemeService.get().getOmniboxBoxBorderNormal());
            }
        };
    }

    @Override
    public boolean contains(final int cellX, final int cellY) {
        return cellX >= getAbsoluteX() &&
                cellX < getAbsoluteX() + this.width &&
                cellY >= getAbsoluteY() &&
                cellY < getAbsoluteY() + this.height;
    }

    public void adjustHeight(final int terminalRows) {
        boolean hasChip = (chipLabel != null && !chipLabel.isEmpty());
        int linesCount = text.doLayout().lines.size();
        int desiredTextHeight = Math.max(1, Math.min(linesCount, 5)); // cap text at 5 lines
        int currentTotalHeight = desiredTextHeight + (hasChip ? 4 : 2); // add 2 rows gap for chip header line
        int targetY = terminalRows - currentTotalHeight - 1;
        int textOffset = hasChip ? 3 : 1; // place text & symbol on row 3 when chip is active

        this.y = targetY;
        this.height = currentTotalHeight;
        box.setX(0);
        box.setY(0);
        box.setHeight(currentTotalHeight);
        symbol.setX(2);
        symbol.setY(textOffset);
        contextChip.setY(1);
        contextChip.setX(4);
        text.setX(4);
        text.setY(textOffset);
        text.setHeight(desiredTextHeight);
    }

    @Override
    public List<Behaviour> getBehaviors() {
        return this.behaviors;
    }

    private void setBoxColor(final int backgroundColor, final int borderColor) {
        this.box.setBackgroundColor(backgroundColor);
        this.box.setBorderColor(borderColor);
    }

    public MultilineTextBox getText() {
        return this.text;
    }

    public TextField getContextChip() {
        return this.contextChip;
    }
}
