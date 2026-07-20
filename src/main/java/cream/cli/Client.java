package cream.cli;

import cream.cli.control.Events;
import cream.cli.view.input.Heatmap;
import cream.cli.view.input.Input;
import cream.cli.view.input.Result;
import cream.cli.view.navigator.Navigator;
import fastterminal.FastTerminal;
import fastterminal.FastTerminalRenderer;
import fastterminal.FastTerminalScene;
import fasttui.component.Container;

public class Client {

    private final Navigator navigator;

    public static void main(String[] args) {
        Thread thread = new Thread(Client::new);
        thread.start();
    }

    private FastTerminalRenderer renderer;
    private FastTerminalScene scene;
    public final Container container;
    private ViewMode viewMode = ViewMode.EXPLORER;
    public int cols;
    public int rows;
    private final Result result;
    private final Heatmap heatmap;
    private final Input input;

    public Client() {
        this.setupFastTerminal();

        this.container = new Container(0, 0, this.cols, this.rows);
        this.container.setBackgroundColor(Theme.BACKGROUND);

        this.navigator = new Navigator(this.cols, this.rows, this::repaint);
        this.input = new Input(this.cols, this.rows);
        this.input.text.addStateChangeListener(source -> {
            this.onInputFocusChanged(source.isFocused());
        });
        this.result = new Result(this.input, this.cols, this.rows);
        this.heatmap = new Heatmap(this.input, this.cols, this.rows);

        this.container.add(this.navigator);
        this.container.add(this.input);
        this.container.add(this.input.popupService);
        this.container.add(this.input.popupModel);

        this.setupEvents();
        this.setupRenderer();
    }

    private void setupFastTerminal() {
        FastTerminal.setTitle("Cream CLI");
        FastTerminal.setRawMode(true);
        final int[] size = FastTerminal.getTerminalSize();
        this.cols = size[0];
        this.rows = size[1];
        this.scene = new FastTerminalScene(0, 0, this.cols, this.rows);
        this.renderer = new FastTerminalRenderer(this.cols, this.rows);
//        this.renderer.setDiffRenderingEnabled(true);
//        this.renderer.setDirtyRectanglesEnabled(true);
        this.renderer.addScene(this.scene);
    }

    private void setupEvents() {
        final Events events = new Events(this, this.scene, this.renderer, this.container);
        events.initAll(this.navigator, null, null, this.input, null);
        events.setupShutdownHook();
    }

    private void setupRenderer() {
        this.scene.clear();
        this.renderer.clearPrev();
        this.repaint();

        Thread blinkThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    return;
                }
                if (input != null && input.text != null && input.text.isFocused()) {
                    repaint();
                }
            }
        }, "caret-blink");
        blinkThread.setDaemon(true);
        blinkThread.start();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {
        }
    }

    public synchronized void repaint() {
        if (this.input != null) {
            this.input.adjustHeight(this.rows);
        }
//        this.scene.clear();
        this.container.render(scene);
        this.renderer.render();
    }

    public void handleTerminalResize(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.renderer.resize(cols, rows);
        this.scene.resize(cols, rows);
        this.container.setWidth(cols);
        this.container.setHeight(rows);

        if (this.input != null) {
            this.input.setY(rows - 4);
        }
        if (this.result != null) {
            this.result.setY(rows - 8);
        }

        this.scene.clear();
        this.renderer.clearPrev();
        this.repaint();
    }

    public void setViewMode(ViewMode mode) {
        this.viewMode = mode;
    }

    public ViewMode getViewMode() {
        return this.viewMode;
    }

    private void onInputFocusChanged(boolean focused) {
        // Tiny focus listener that can be expanded later by the user
    }
}
// [L] 🖥️ LocalAI
//[R] ☁️ RemoteAI