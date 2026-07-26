package cream.cli.view.omnibox;

import cream.cli.Theme;
import fasttui.behaviour.Behaviour;
import fasttui.component.*;
import fasttui.composable.Button;
import fasttui.composable.MultilineTextBox;
import fasttui.composable.TextInput;
import fasttui.layout.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class Omnibox extends Container implements Interactive {

    public static final ColorSet OMNIBOX_DROPDOWN_MODE_BACKGROUND_SET = Theme.OMNIBOX_MODE_BACKGROUND_SET;
    public static final ColorSet OMNIBOX_DROPDOWN_MODE_FOREGROUND_SET = Theme.OMNIBOX_MODE_FOREGROUND_SET;
    public static final int COLOR_FOCUSSED_BACKGROUND = Theme.OMNIBOX_FOCUSSED_BACKGROUND;
    public static final int COLOR_FOCUSSED_BORDER = Theme.OMNIBOX_FOCUSSED_BORDER;
    public static final int COLOR_HOVERED_BACKGROUND = Theme.OMNIBOX_HOVERED_BACKGROUND;
    public static final int COLOR_HOVERED_BORDER = Theme.OMNIBOX_HOVERED_BORDER;
    public static final int COLOR_NORMAL_BACKGROUND = Theme.OMNIBOX_NORMAL_BACKGROUND;
    public static final int COLOR_NORMAL_BORDER = Theme.OMNIBOX_NORMAL_BORDER;

    public final Box box;
    public final MultilineTextBox text;
    private final TextField symbol;
    public final Button mode;
    public final Button service;
    public final Button model;
    public final TextField context;
    public final TextField cost;
    public final LinearLayout layout;
    public final PopupMode popupMode;
    public final PopupService popupService;
    public final PopupModel popupModel;
    private final List<Behaviour> behaviors = new ArrayList<>();

    public Omnibox(int cols, int rows) {
        super(1, rows - 4, cols - 2, rows);

        this.popupMode = new PopupMode(cols, rows);
        this.popupMode.setVisible(false);
        this.popupService = new PopupService(cols, rows);
        this.popupService.setVisible(false);
        this.popupModel = new PopupModel(cols, rows);
        this.popupModel.setVisible(false);
        final Runnable runnable0 = () -> this.popupMode.setVisible((!this.popupMode.isVisible()));
        final Runnable runnable1 = () -> this.popupService.setVisible((!this.popupService.isVisible()));
        final Runnable runnable2 = () -> this.popupModel.setVisible((!this.popupModel.isVisible()));

        this.box = new Box(0, 0, this.width, 3);
        this.box.setBorderStyle(BorderStyle.ROUNDED);
        this.box.setBackgroundColor(Theme.TRANSPARENT);
        this.box.setBorderColor(Theme.BORDER);

        this.symbol = new TextField(2, 1, "❯", Theme.FOREGROUND);
        this.symbol.setBackgroundColor(Theme.TRANSPARENT);

        this.text = new MultilineTextBox(4, 1, this.width - 6, 1);
        this.text.setBackgroundSet(Theme.OMNIBOX_TEXT_BACKGROUND_SET);
        this.text.setForegroundSet(Theme.OMNIBOX_TEXT_FOREGROUND_SET);
        this.text.setColorCaretBg(Theme.OMNIBOX_CARET_BG);
        this.text.setColorCaretFg(Theme.OMNIBOX_CARET_FG);
        this.text.setPlaceholderForegroundSet(Theme.OMNIBOX_PLACEHOLDER_FOREGROUND_SET);
        this.text.setPlaceholder("Command or Ask anything ...");
        this.text.setText("");
        this.text.addStateChangeListener(this.getTextStateChangeListener());

        this.behaviors.add(new InputBehaviour(this.text));

        this.mode = new Button(0, 0, " Auto ↑", 1, OMNIBOX_DROPDOWN_MODE_BACKGROUND_SET, OMNIBOX_DROPDOWN_MODE_FOREGROUND_SET, runnable0);

        this.service = new Button(0, 0, "Llama ↑", 1, Theme.OMNIBOX_BUTTON_BACKGROUND_SET, Theme.OMNIBOX_BUTTON_FOREGROUND_SET, runnable1);

        this.model = new Button(0, 0, "qwen2.5:3b ↑", 1, Theme.OMNIBOX_BUTTON_BACKGROUND_SET, Theme.OMNIBOX_BUTTON_FOREGROUND_SET, runnable2);

        this.context = new TextField(0, 0, "(4,073)", Theme.OMNIBOX_INFORMATION_FOREGROUND);
        this.context.setBackgroundColor(Theme.TRANSPARENT);

        this.cost = new TextField(0, 0, "$0.00", Theme.OMNIBOX_INFORMATION_FOREGROUND);
        this.cost.setBackgroundColor(Theme.TRANSPARENT);

        final List<Component> horizontal = List.of(this.mode, this.service, this.model, this.context, this.cost);
        this.layout = new LinearLayout(LinearLayout.Direction.HORIZONTAL, LinearLayout.Alignment.LEFT, 1);
        this.layout.layout(0, 3, this.width, this.height, horizontal);
        this.layout.setSpacing(0);

        this.add(this.box);
        this.add(this.symbol);
        this.add(this.text);
        this.add(this.mode);
        this.add(this.service);
        this.add(this.model);
        this.add(this.context);
        this.add(this.cost);
    }

    private TextInput.StateChangeListener getTextStateChangeListener() {
        return new TextInput.StateChangeListener() {
            @Override
            public void onStateChanged(TextInput source) {
                if (source.isFocused()) {
                    box.setBackgroundColor(COLOR_FOCUSSED_BACKGROUND);
                    box.setBorderColor(COLOR_FOCUSSED_BORDER);
                } else if (source.isHovered()) {
                    box.setBackgroundColor(COLOR_HOVERED_BACKGROUND);
                    box.setBorderColor(COLOR_HOVERED_BORDER);
                } else {
                    box.setBackgroundColor(COLOR_NORMAL_BACKGROUND);
                    box.setBorderColor(COLOR_NORMAL_BORDER);
                }
            }
        };
    }

    @Override
    public List<Behaviour> getBehaviors() {
        return behaviors;
    }

    @Override
    public boolean contains(int cellX, int cellY) {
        return cellX >= x && cellX < x + width && cellY >= y && cellY < y + height;
    }

    public void adjustHeight(int terminalRows) {
        int linesCount = text.doLayout().lines.size();
        int desiredTextHeight = Math.min(linesCount, 5); // cap at 5 lines
        int currentTotalHeight = desiredTextHeight + 2;
        int targetY = terminalRows - currentTotalHeight - 1;
        int diff = desiredTextHeight - text.getHeight();
        if (diff != 0 || this.y != targetY) {
            this.y = targetY;
            this.height = currentTotalHeight;
            box.setY(this.y);
            box.setHeight(currentTotalHeight);
            symbol.setY(this.y + 1);
            text.setY(this.y + 1);
            text.setHeight(desiredTextHeight);
            final List<Component> horizontal = List.of(this.mode, this.service, this.model, this.context, this.cost);
            this.layout.layout(0, desiredTextHeight + 1, this.width, this.height, horizontal);
        }
    }

}
