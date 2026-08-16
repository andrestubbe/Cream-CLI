package cream.cli.view.footer;

import cream.cli.view.theme.ThemeService;
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

    public final Button mode;
    public final Button service;
    public final Button model;
    public final Button tasks;
    public final PopupMode popupMode;
    public final PopupService popupService;
    public final PopupModel popupModel;
    public final PopupTasks popupTasks;
    public final LinearLayout layout;

    public Footer(int cols, int rows) {
        super(1, rows - 1, cols, 1);

        this.popupMode = new PopupMode(cols, rows);
        this.popupMode.setVisible(false);
        this.popupService = new PopupService(cols, rows);
        this.popupService.setVisible(false);
        this.popupModel = new PopupModel(cols, rows);
        this.popupModel.setVisible(false);
        this.popupTasks = new PopupTasks(cols, rows);

        ColorSet modeBg = new ColorSet(ThemeService.get().getFooterModeBackgroundNormal(), ThemeService.get().getFooterModeBackgroundNormal(), ThemeService.get().getFooterModeBackgroundNormal(), ThemeService.get().getFooterModeBackgroundNormal());
        ColorSet modeFg = new ColorSet(ThemeService.get().getFooterModeForegroundNormal(), ThemeService.get().getFooterModeForegroundNormal(), ThemeService.get().getFooterModeForegroundNormal(), ThemeService.get().getFooterModeForegroundNormal());
        int transparent = -2;
        ColorSet btnBg = new ColorSet(transparent, transparent, transparent, transparent);
        ColorSet btnFg = new ColorSet(ThemeService.get().getFooterButtonForegroundNormal(), ThemeService.get().getFooterButtonForegroundHover(), ThemeService.get().getFooterButtonForegroundNormal(), ThemeService.get().getFooterButtonForegroundHover());

        final Runnable onMode = () -> this.popupMode.setVisible(!this.popupMode.isVisible());
        final Runnable onService = () -> this.popupService.setVisible(!this.popupService.isVisible());
        final Runnable onModel = () -> this.popupModel.setVisible(!this.popupModel.isVisible());
        final Runnable onTasks = () -> {
            this.popupTasks.updateTasks(this.width, rows);
            this.popupTasks.setVisible(!this.popupTasks.isVisible());
        };

        this.mode = new Button(0, 0, " fast ", 0, modeBg, modeFg, onMode);
        this.service = new Button(0, 0, " openAI ", 0, btnBg, btnFg, onService);
        this.model = new Button(0, 0, " gpt-5o ", 0, btnBg, btnFg, onModel);
        this.tasks = new Button(0, 0, " ○ Tasks ", 0, btnBg, btnFg, onTasks);

        this.popupMode.setOnSelectAction(() -> {
            int idx = this.popupMode.getSelectedIndex();
            String label = this.popupMode.getLabelAt(idx);
            if (label != null) {
                String text = " " + label + " ↑";
                this.mode.setText(text);
                this.mode.setWidth(text.length() + 2);
                this.relayout(rows, this.width);
            }
        });

        this.popupService.setOnSelectAction(() -> {
            int idx = this.popupService.getSelectedIndex();
            String label = this.popupService.getLabelAt(idx);
            if (label != null) {
                this.service.setText(label + " ↑");
                this.service.setWidth((label + " ↑").length() + 2);
                this.popupModel.updateForService(label);
                String firstModel = this.popupModel.getLabelAt(0);
                if (firstModel != null) {
                    String text = firstModel + " ↑";
                    this.model.setText(text);
                    this.model.setWidth(text.length() + 2);
                }
                this.relayout(rows, this.width);
            }
        });

        this.popupModel.setOnSelectAction(() -> {
            int idx = this.popupModel.getSelectedIndex();
            String label = this.popupModel.getLabelAt(idx);
            if (label != null) {
                String text = label + " ↑";
                this.model.setText(text);
                this.model.setWidth(text.length() + 2);
                this.relayout(rows, this.width);
            }
        });

        final List<Component> horizontal = List.of(
                this.mode,
                this.service,
                this.model);
        this.layout = new LinearLayout(LinearLayout.Direction.HORIZONTAL, LinearLayout.Alignment.LEFT, 1);
        this.layout.layout(0, 0, width, 1, horizontal);
        this.layout.setSpacing(0);

        this.tasks.setX(Math.max(0, width - this.tasks.getWidth() - 1));
        this.tasks.setY(0);

        this.add(this.mode);
        this.add(this.service);
        this.add(this.model);
        this.add(this.tasks);
        this.add(this.popupMode);
        this.add(this.popupService);
        this.add(this.popupModel);
        this.add(this.popupTasks);
    }

    public void relayout(int rows, int width) {
        setY(rows - 1);
        setWidth(width);
        final List<Component> horizontal = List.of(this.mode, this.service, this.model);
        this.layout.layout(0, 0, width, 1, horizontal);

        // Position Tasks button at the far right edge of the footer
        int tasksWidth = this.tasks.getWidth();
        this.tasks.setX(Math.max(0, width - tasksWidth - 1));
        this.tasks.setY(0);
    }

    public void updateTaskButton(boolean hasActiveTasks) {
        String symbol = hasActiveTasks ? "●" : "○";
        String label = " " + symbol + " Tasks ";
        this.tasks.setText(label);
        this.tasks.setWidth(label.length());
        if (width > 0) {
            this.tasks.setX(Math.max(0, width - this.tasks.getWidth() - 1));
        }
    }

}
