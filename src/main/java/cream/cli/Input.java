package cream.cli;

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

    public final Dropdown dropdown;
    public final TextField context;
    public final TextField cost;
    public final TextField plan;
    public final TextField act;
    public final TextField shortcut;

    public Input(int cols, int rows) {
        super(0, rows - 4, cols, 4);

        final LinearLayout layout = new LinearLayout(
                LinearLayout.Direction.HORIZONTAL,
                LinearLayout.Alignment.LEFT,
                1);

        final List<String> items = Arrays.asList("qwen2.5:3b", "Llama 3 (70B)", "Mistral 7B", "Gemma 7B");
        final Consumer<Integer> onSelect = idx -> {
        };

        this.commandBox = new Box(0, 0, cols, 3);
        this.commandBox.setBorderStyle(BorderStyle.ROUNDED);
        this.commandBox.setBorderColor(Theme.INPUT_BORDER);

        this.commandText = new TextBox(2, 1, cols - 3);
        this.dropdown = new Dropdown(0, 0, 12, 1, items, onSelect);
        this.dropdown.setHeaderColors(-1, 0x777777, -1, 0x777777);
        this.dropdown.setExpandDirection(Dropdown.ExpandDirection.UP);
        this.context = new TextField(0, 0, "(4,073)", 0x777777);
        this.cost = new TextField(0, 0, "$0.00", 0x777777);
        this.plan = new TextField(0, 0, "○ Plan", 0x777777);
        this.act = new TextField(0, 0, "● Act", 0x777777);
        this.shortcut = new TextField(0, 0, "(ctrl+tab)", 0x777777);
        this.shortcut.setBackgroundColor(Theme.TRANSPARENT);

        this.commandText.setText("❯ Ask anything ...");

        final List<Component> bottomLeft = List.of(
                this.dropdown,
                this.context,
                this.cost
        );
        final List<Component> bottomRight = List.of(
                this.act,
                this.plan,
                this.shortcut
        );

        layout.layout(1, 3, 30, 1, bottomLeft);
        layout.layout(cols - 1, 3, 30, 1, bottomRight, LinearLayout.Alignment.RIGHT);

        this.add(this.commandBox);
        this.add(this.commandText);
        this.add(this.dropdown);
        this.add(this.context);
        this.add(this.cost);
        this.add(this.plan);
        this.add(this.act);
        this.add(this.shortcut);
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
        layout.layout(1, 3, 30, 1, List.of(dropdown, context, cost));
        layout.layout(cols - 1, 3, 30, 1, List.of(act, plan, shortcut), LinearLayout.Alignment.RIGHT);
    }

}
