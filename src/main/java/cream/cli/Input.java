package cream.cli;

import cream.cli.inputbox.CommandResult;
import fasttui.component.*;
import fasttui.composable.Dropdown;
import fasttui.composable.TextBox;
import fasttui.layout.LinearLayout;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Input extends Container {

    public final Box commandBox;
    public final TextBox commandText;

    public final Dropdown dropdown0;
    public final Dropdown dropdown1;
    public final TextField context;
    public final TextField cost;
    private final CommandResult commandResult;
    private final TextField commandSymbol;

    public Input(int cols, int rows) {
        super(0, 0, cols, rows);

        final LinearLayout layout = new LinearLayout(
                LinearLayout.Direction.HORIZONTAL,
                LinearLayout.Alignment.LEFT,
                1);

        this.commandBox = new Box(0, rows - 4, cols, 3);
        this.commandBox.setBorderStyle(BorderStyle.ROUNDED);
        this.commandBox.setBackgroundColor(Theme.INPUT_COMMAND_BORDER_NORMAL_BACKGROUND);
        this.commandBox.setBorderColor(Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND);

        this.commandSymbol = new TextField(2, rows - 3, "❯", Theme.FOREGROUND);
        this.commandSymbol.setBackgroundColor(Theme.BACKGROUND);

        this.commandText = new TextBox(4, rows - 3, cols - 5);
        this.commandText.setBackgroundColor(Theme.INPUT_COMMAND_BORDER_NORMAL_BACKGROUND);
        this.commandText.setForegroundColor(Theme.FOREGROUND);
        this.commandText.setText("FastT");
//        this.commandText.setText("Ask anything ...");

        this.commandResult = new CommandResult(0, rows - 8, cols, 5, this);
        this.commandResult.setStrings("FastTUI", "fasttui.FastTUI");

        final List<String> items0 = Arrays.asList("Llama");
        final Consumer<Integer> onSelect0 = idx -> {
        };
        final List<String> items1 = Arrays.asList("qwen2.5:3b", "Llama 3 (70B)", "Mistral 7B", "Gemma 7B");
        final Consumer<Integer> onSelect1 = idx -> {
        };

        this.dropdown0 = new Dropdown(0, 0, 12, 1, items0, onSelect0);
        this.dropdown0.setExpandDirection(Dropdown.ExpandDirection.UP);
        this.dropdown0.setHeaderColors(
                Theme.BACKGROUND,
                Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND,
                Theme.BACKGROUND,
                Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND,
                Theme.BACKGROUND,
                Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND
        );
        this.dropdown1 = new Dropdown(0, 0, 12, 1, items1, onSelect1);
        this.dropdown1.setExpandDirection(Dropdown.ExpandDirection.UP);
        this.dropdown1.setHeaderColors(
                Theme.BACKGROUND,
                Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND,
                Theme.BACKGROUND,
                Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND,
                Theme.BACKGROUND,
                Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND
        );

        this.context = new TextField(0, 0, "(4,073)", Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND);
        this.context.setBackgroundColor(Theme.BACKGROUND);

        this.cost = new TextField(0, 0, "$0.00", Theme.INPUT_COMMAND_BORDER_NORMAL_FOREGROUND);
        this.cost.setBackgroundColor(Theme.BACKGROUND);

        final List<Component> bottomLeft = List.of(
                this.dropdown0,
                this.dropdown1,
                this.context,
                this.cost
        );
        layout.layout(1, rows - 1, 30, 1, bottomLeft);

        this.add(this.commandBox);
        this.add(this.commandSymbol);
        this.add(this.commandText);
        this.add(this.dropdown0);
        this.add(this.dropdown1);
        this.add(this.context);
        this.add(this.cost);
        this.add(this.commandResult);
    }

    public void onResize(int cols, int rows) {
        setY(rows - 4);
        setWidth(cols);
        commandBox.setWidth(cols);
        commandText.setWidth(cols - 3);

        final LinearLayout layout = new LinearLayout(
                LinearLayout.Direction.HORIZONTAL,
                LinearLayout.Alignment.LEFT,
                1);
        layout.layout(1, 3, 30, 1, List.of(dropdown1, context, cost));
    }

}
