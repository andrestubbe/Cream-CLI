package cream.cli;

import cream.cli.control.Events;
import cream.cli.control.IOManager;
import cream.cli.view.ViewManager;
import cream.cli.view.editor.EditorFileManager;
import cream.cli.view.render.RenderEngine;
import cream.cli.view.terminal.TerminalManager;
import cream.cli.view.theme.ThemeService;

import java.io.File;

public class Client {

    public static void main(String[] args) {
        TerminalManager.setupTerminalState();
        Thread thread = new Thread(() -> {
            new Client(args);
        });
        thread.start();
    }

    private final RenderEngine renderEngine;
    private final ViewManager viewManager;
    public int cols;
    public int rows;

    public Client(final String... args) {
        ThemeService.set("rosé pine moon");
        int[] size = TerminalManager.initFastTerminal("CREAM");
        this.cols = size[0];
        this.rows = size[1];
        this.renderEngine = new RenderEngine(this.cols, this.rows);
        this.viewManager = new ViewManager(this.cols, this.rows, this::repaint, this);
        final Events events = new Events(this, args);
        this.viewManager.getNavigator().getFilesList().setDirectoryChangeListener(IOManager::saveDirectoryState);
        this.renderEngine.startCaretBlinkThread(this::isFocused, this::repaint);
        this.renderEngine.blockMainThread();
    }

    public synchronized void repaint() {
//        String[] strings = Caller.whoCalledMeToString(1, 10);
//        String[] out = Arrays.copyOf(strings, strings.length + 1);
//        out[out.length - 1] = "";
//        Console.println(out);
        if (this.viewManager != null && this.renderEngine != null) {
            this.renderEngine.repaint(this.viewManager.container);
        }
    }

    public void handleTerminalResize(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.renderEngine.resize(cols, rows);
        this.viewManager.handleTerminalResize(cols, rows);
        this.renderEngine.clearPrev();
        this.repaint();
    }

    public ViewManager getViewManager() {
        return this.viewManager;
    }

    private boolean isFocused() {
        return (this.viewManager.getOmnibox() != null &&
                this.viewManager.getOmnibox().text != null &&
                this.viewManager.getOmnibox().text.isFocused())
                ||
                (this.getViewManager().getEditor() != null &&
                        getViewManager().getEditor().isVisible());
    }
}
