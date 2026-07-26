package cream.cli.view.footer;

import cream.cli.Theme;
import fasttui.component.ColorSet;
import fasttui.component.Component;
import fasttui.component.Container;
import fasttui.component.TextField;
import fasttui.composable.Button;
import fasttui.layout.LinearLayout;

import java.util.List;

/**
 * Pure View component for the Omnibox Footer bar in CreamCLI.
 * Contains the Mode, Service, Model dropdown buttons and Context/Cost info labels.
 */
public class Footer extends Container {

    public static final ColorSet OMNIBOX_DROPDOWN_MODE_BACKGROUND_SET = Theme.OMNIBOX_MODE_BACKGROUND_SET;
    public static final ColorSet OMNIBOX_DROPDOWN_MODE_FOREGROUND_SET = Theme.OMNIBOX_MODE_FOREGROUND_SET;

    public final Button mode;
    public final Button service;
    public final Button model;
    public final PopupMode popupMode;
    public final PopupService popupService;
    public final PopupModel popupModel;
    public final TextField context;
    public final TextField cost;
    public final LinearLayout layout;

    public Footer(int cols, int rows) {
        super(1, rows - 1, cols, 1);

        this.popupMode = new PopupMode(cols, rows);
        this.popupMode.setVisible(false);
        this.popupService = new PopupService(cols, rows);
        this.popupService.setVisible(false);
        this.popupModel = new PopupModel(cols, rows);
        this.popupModel.setVisible(false);
        final Runnable onMode = () -> this.popupMode.setVisible(!this.popupMode.isVisible());
        final Runnable onService = () -> this.popupService.setVisible(!this.popupService.isVisible());
        final Runnable onModel = () -> this.popupModel.setVisible(!this.popupModel.isVisible());

        this.mode = new Button(0, 0, " Auto ↑", 1, OMNIBOX_DROPDOWN_MODE_BACKGROUND_SET, OMNIBOX_DROPDOWN_MODE_FOREGROUND_SET, onMode);
        this.service = new Button(0, 0, "Llama ↑", 1, Theme.OMNIBOX_BUTTON_BACKGROUND_SET, Theme.OMNIBOX_BUTTON_FOREGROUND_SET, onService);
        this.model = new Button(0, 0, "qwen2.5:3b ↑", 1, Theme.OMNIBOX_BUTTON_BACKGROUND_SET, Theme.OMNIBOX_BUTTON_FOREGROUND_SET, onModel);

        this.context = new TextField(0, 0, "(4,073)", Theme.OMNIBOX_INFORMATION_FOREGROUND);
        this.context.setBackgroundColor(Theme.TRANSPARENT);

        this.cost = new TextField(0, 0, "$0.00", Theme.OMNIBOX_INFORMATION_FOREGROUND);
        this.cost.setBackgroundColor(Theme.TRANSPARENT);

        final List<Component> horizontal = List.of(this.mode, this.service, this.model, this.context, this.cost);
        this.layout = new LinearLayout(LinearLayout.Direction.HORIZONTAL, LinearLayout.Alignment.LEFT, 1);
        this.layout.layout(0, 0, width, 1, horizontal);
        this.layout.setSpacing(0);

        this.add(this.mode);
        this.add(this.service);
        this.add(this.model);
        this.add(this.context);
        this.add(this.cost);
    }

    public void relayout(int y, int width) {
        setY(y);
        setWidth(width);
        final List<Component> horizontal = List.of(this.mode, this.service, this.model, this.context, this.cost);
        this.layout.layout(0, 0, width, 1, horizontal);
    }

    public void adjustHeight(int terminalRows) {
        this.relayout(this.y + 3, this.width);
    }
}
