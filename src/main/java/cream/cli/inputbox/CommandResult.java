package cream.cli.inputbox;

import cream.cli.Input;
import cream.cli.Theme;
import fasttui.component.BorderStyle;
import fasttui.component.Box;
import fasttui.component.TextField;

public class CommandResult extends Box {

    private final Input input;

    public CommandResult(int x, int y, int width, int height, Input input) {
        super(x, y, width, height);
        this.setBorderStyle(BorderStyle.ROUNDED_CUT_OF_BOTTOM);
        this.setBackgroundColor(Theme.INPUT_COMMAND_BORDER_NORMAL_BACKGROUND);
        this.setBorderColor(Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND);
        this.input = input;
    }

    public void setStrings(final String... texts) {
//        this.removeAll();
        final TextField[] textFields = new TextField[texts.length];
        for (int i = 0; i < texts.length; i++) {
            textFields[i] = new TextField(10, 10, "● Act", 0xFF000000);
            this.add(textFields[i]);
        }
        this.add(textFields[0]);

        TextField textField = new TextField(4, 1, "FastTUI", 0x95a8f1);
        textField.setBackgroundColor(Theme.BACKGROUND);
        TextField textField2 = new TextField(4, 2, "FastTUI (Copy)", Theme.COLOR_BLUE_8);
        textField2.setBackgroundColor(Theme.BACKGROUND);
        TextField textField3 = new TextField(4, 3, "fasttui.FastTUI", Theme.COLOR_BLUE_8);
        textField3.setBackgroundColor(Theme.BACKGROUND);

        this.add(textField);
        this.add(textField2);
        this.add(textField3);
    }
}

