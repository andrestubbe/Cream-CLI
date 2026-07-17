package cream.cli;

import fasttui.component.BorderStyle;
import fasttui.component.Box;
import fasttui.component.Component;
import fasttui.component.TextField;

public class Settings {

    public final Box box;
    public final TextField c1;
    public final TextField c2;
    public final TextField c3;
    public final TextField c4;

    public Settings(int cols, int rows) {
        this.box = new Box(0, rows - 14, cols, 10);
        this.box.setBorderStyle(BorderStyle.ROUNDED);
        this.box.setBorderColor(0x777777);
        this.c1 = new TextField(2, rows - 13, "/settings", 0x999999);
        this.c2 = new TextField(2, rows - 12, "/model", 0x999999);
        this.c3 = new TextField(2, rows - 11, "/tasks", 0x999999);
        this.c4 = new TextField(2, rows - 10, "/exit", 0x999999);
    }

    public Component[] getComponents() {
        return new Component[]{box, c1, c2, c3, c4};
    }
}
