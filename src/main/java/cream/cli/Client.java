package cream.cli;

import cream.cli.control.Events;
import cream.cli.view.ViewManager;
import cream.cli.view.editor.Editor;
import cream.cli.view.editor.EditorFileManager;
import cream.cli.view.files.Navigator;
import cream.cli.view.omnibox.Omnibox;
import cream.cli.view.render.RenderEngine;
import cream.cli.view.result.ResultProgress;
import cream.cli.view.result.ResultSearch;
import cream.cli.view.terminal.TerminalManager;
import cream.cli.view.ui.Dialog;
import fastansi.FastANSI;
import fastterminal.FastTerminalRenderer;
import fastterminal.FastTerminalScene;
import fasttui.component.Container;

import java.io.File;

public class Client {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        TerminalManager.setupTerminalState();
        TerminalManager.setupCreamLog();
        Thread thread = new Thread(Client::new);
        thread.start();
    }

    private final RenderEngine renderEngine;
    private final ViewManager viewManager;
    public final Container container;
    public final Dialog dialog;
    public final ResultSearch resultSearch;
    public final ResultProgress resultProgress;
    public int cols;
    public int rows;

    public Client() {
        int[] size = TerminalManager.initFastTerminal("Cream CLI");
        this.cols = size[0];
        this.rows = size[1];
        this.renderEngine = new RenderEngine(this.cols, this.rows);
        this.viewManager = new ViewManager(this.cols, this.rows, this::repaint, this);
        this.container = this.viewManager.container;
        this.dialog = this.viewManager.dialog;
        this.resultSearch = this.viewManager.resultSearch;
        this.resultProgress = this.viewManager.resultProgress;
        final Events events = new Events(this);

        // Register directory-change listener so Client controls persistence
        if (this.viewManager.navigator != null) {
            this.viewManager.navigator.files.setDirectoryChangeListener(this::saveDirectoryState);
        }

        // Restore last session
        if (this.viewManager.editor != null && this.viewManager.editor.fileManager.restoreEditorState()) {
            this.showEditor();
        } else {
            File lastDir = EditorFileManager.readLastDir();
            if (lastDir != null && this.viewManager.navigator != null) {
                this.viewManager.navigator.files.navigateTo(lastDir);
            }
        }
        this.renderEngine.clearPrev();
        this.repaint();
        this.renderEngine.startCaretBlinkThread(
                () -> (this.getOmnibox() != null && this.getOmnibox().text != null && this.getOmnibox().text.isFocused())
                        || (this.getEditor() != null && this.getEditor().isVisible()),
                this::repaint
        );
        this.renderEngine.blockMainThread();
    }

    public synchronized void repaint() {
        if (this.viewManager != null && this.renderEngine != null) {
            this.viewManager.prepareRepaint(this.rows);
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

    /**
     * Called by EditorController before switching to the explorer. Persists file + caret.
     */
    public void saveState() {
        if (this.viewManager != null && this.viewManager.editor != null) {
            this.viewManager.editor.fileManager.saveEditorState();
        }
    }

    /**
     * Called when the navigator enters a different directory.
     */
    public void saveDirectoryState(File dir) {
        EditorFileManager.saveDirectoryState(dir);
    }

    public void showExplorer() {
        System.out.print(FastANSI.CLEAR_SCREEN + FastANSI.CURSOR_HOME);
        if (this.renderEngine != null) {
            this.renderEngine.clearPrev();
        }
        // Navigate the file browser to the directory of the currently open file
        if (this.viewManager.navigator != null && this.viewManager.editor != null) {
            java.io.File current = this.viewManager.editor.fileManager.getCurrentFile();
            if (current != null && current.getParentFile() != null) {
                this.viewManager.navigator.files.navigateTo(current.getParentFile());
            }
        }
        this.viewManager.showExplorer();
        this.repaint();
    }

    public void showEditor() {
        System.out.print(FastANSI.CLEAR_SCREEN + FastANSI.CURSOR_HOME);
        if (this.renderEngine != null) {
            this.renderEngine.clearPrev();
        }
        this.viewManager.showEditor();
        this.repaint();
    }

    public FastTerminalScene getScene() {
        return this.renderEngine.getScene();
    }

    public FastTerminalRenderer getRenderer() {
        return this.renderEngine.getRenderer();
    }

    public Navigator getNavigator() {
        return this.viewManager.navigator;
    }

    public Editor getEditor() {
        return this.viewManager.editor;
    }

    public Omnibox getOmnibox() {
        return this.viewManager.omnibox;
    }

    public Container getContainer() {
        return this.container;
    }

    public ViewManager getViewManager() {
        return this.viewManager;
    }
}
