package cream.cli;

import fastterminal.FastTerminalScene;
import fasttui.component.*;

public class Bubble extends Container{

    public final Box botBox;
    public final TextArea botTextArea;
    public final TextField botEmjit;
    public final TextField botTime;
    private final Box userBox;
    private final TextField userEmjit;
    private final TextArea userTextArea;
    private final Container container;

    public Bubble(int cols, int rows) {
        super(0,0,cols,10);
        this.container = new Container(0, rows - 19, cols, 19);
        container.setBackgroundColor(0x111111);

        String userText = "Wir sollten die letzte Method mehere male durchgehen und variieren bis wir die beste speicher und speed allokation haben.";
        int userY = rows - 18;
        this.userBox = new Box(6, userY, cols - 12, 4);
        this.userBox.setBorderStyle(BorderStyle.ROUNDED);
        this.userBox.setBorderColor(0x222222);
        this.userEmjit = new TextField(3, userY + 1, "\uD83D\uDC64", Theme.TRANSPARENT);
        this.userTextArea = new TextArea(8, userY + 1, cols - 14, 10);
        this.userTextArea.setForegroundColor(0x666666);
        this.userTextArea.setText(userText);

        String botText = "Ich kann diese Methode beliebig variieren, bis ihre Ausführungsgeschwindigkeit optimal ist.\n" +
                "Dafür analysiere ich mehrere Zyklen und messe jede Iteration präzise.\n" +
                "Sobald ein Muster entsteht, passe ich den Algorithmus automatisch an.";
        int botY = rows - 14;
        this.botBox = new Box(6, botY, cols - 12, 6);
        this.botBox.setBorderStyle(BorderStyle.ROUNDED);
        this.botBox.setBorderColor(0x444444);
        this.botEmjit = new TextField(3, botY + 1, "\uD83E\uDD16", Theme.TRANSPARENT);
        this.botTextArea = new TextArea(8, botY + 1, cols - 10, 10);
        this.botTextArea.setForegroundColor(0xCCCCCC);
        this.botTextArea.setText(botText);
        this.botTime = new TextField(8, botY + 6, "0.123 s", Theme.FILE_INFORMATION);
    }

    public Component[] getComponents() {
        return new Component[]{container, userEmjit, userBox, userTextArea, botEmjit, botBox, botTextArea, botTime};
    }
}
