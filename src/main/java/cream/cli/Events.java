package cream.cli;

import fastansi.FastANSI;
import fastfileindex.FastFileIndex;
import fastkeyboard.FastKeyboard;
import fastkeyboard.FastKeyboardImpl;
import fastkeyboard.Keys;
import fastmouse.FastMouseListener;
import fastterminal.AnsiMouse;
import fastterminal.FastTerminal;
import fastterminal.FastTerminalRenderer;
import fastterminal.FastTerminalScene;
import fasttui.behaviour.EventDispatcher;
import fasttui.component.Container;
import fasttui.layout.SplitHorizontal;
import fasttui.layout.SplitView;

public class Events {

    private final Client client;
    private final FastTerminalScene scene;
    private final FastTerminalRenderer renderer;
    private final Container container;

    private SplitView splitView;
    private Navigator navigator;
    private TreeNavigator treeNavigator;
    private Editor editor;
    private Input input;

    public Events(Client client, FastTerminalScene scene, FastTerminalRenderer renderer, Container container) {
        this.client = client;
        this.scene = scene;
        this.renderer = renderer;
        this.container = container;
    }

    // ------------------------------------------------------------
    // PUBLIC API
    // ------------------------------------------------------------

    public void initAll(Navigator navigator, TreeNavigator treeNavigator, Editor editor, Input input, SplitView splitView) {
        this.navigator = navigator;
        this.treeNavigator = treeNavigator;
        this.editor = editor;
        this.input = input;
        this.splitView = splitView;

        initTerminal();
        initKeyboard();
        initMouse();
        initResizeWatcher();
    }

    public void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
    }

    // ------------------------------------------------------------
    // TERMINAL INIT / EXIT
    // ------------------------------------------------------------

    private void initTerminal() {
        System.out.print(FastANSI.ALT_BUFFER_ON + FastANSI.CURSOR_HIDE);
    }

    private void exit() {
        FastTerminal.setRawMode(false);
        System.out.print(FastANSI.ALT_BUFFER_OFF + FastANSI.CURSOR_SHOW + FastANSI.RESET);
    }

    // ------------------------------------------------------------
    // KEYBOARD
    // ------------------------------------------------------------

    private void initKeyboard() {

        final boolean[] ctrlHeld = {false};
        final boolean[] shiftHeld = {false};

        FastKeyboard keyboard = new FastKeyboardImpl();

        keyboard.startListening((h, vKey, mc, pressed, e0, ts, ch) -> {

            boolean inEditor = client.getViewMode() == ViewMode.EDITOR;
            boolean inExplorer = client.getViewMode() == ViewMode.EXPLORER
                    || client.getViewMode() == ViewMode.SPLIT;
            boolean inTree = client.getViewMode() == ViewMode.TREE;

            if (vKey == Keys.CTRL) { ctrlHeld[0] = pressed; return; }
            if (vKey == Keys.SHIFT || vKey == Keys.LSHIFT || vKey == Keys.RSHIFT) {
                shiftHeld[0] = pressed; return;
            }

            if (vKey == Keys.A && !pressed) {
                triggerFileIndex();
            }

            if (!pressed) return;
            if (!FastTerminal.isTerminalFocused()) return;

            // CTRL shortcuts
            if (ctrlHeld[0]) {
                switch (vKey) {
                    case Keys.Q -> System.exit(0);
                    case Keys.D -> {
                        if (client.getViewMode() == ViewMode.EDITOR ||
                                client.getViewMode() == ViewMode.SPLIT) {

                            client.setViewMode(
                                    client.getViewMode() == ViewMode.SPLIT
                                            ? ViewMode.EDITOR
                                            : ViewMode.SPLIT
                            );
                            client.repaint();
                        }
                        return;
                    }
                    case Keys.T -> {
                        if (client.getViewMode() == ViewMode.EXPLORER) {
                            client.setViewMode(ViewMode.TREE);
                        } else if (client.getViewMode() == ViewMode.TREE) {
                            client.setViewMode(ViewMode.EXPLORER);
                        }
                        client.repaint();
                        return;
                    }
                    case Keys.A -> { if (inEditor) editor.selectAll(); return; }
                    case Keys.C -> { if (inEditor) editor.copy(); return; }
                    case Keys.X -> { if (inEditor) editor.cut(); return; }
                    case Keys.V -> { if (inEditor) editor.paste(); return; }
                }
            }

            // Normal keys
            switch (vKey) {
                case Keys.ESC -> {
                    if (client.getViewMode() == ViewMode.EDITOR ||
                            client.getViewMode() == ViewMode.SPLIT) {
                        client.setViewMode(ViewMode.EXPLORER);
                    }
                }
                case Keys.UP -> {
                    if (inExplorer) navigator.files.selectPrevious();
                    else if (inTree) treeNavigator.selectPrevious();
                    else if (inEditor) editor.moveUp(shiftHeld[0]);
                    client.repaint();
                }
                case Keys.DOWN -> {
                    if (inExplorer) navigator.files.selectNext();
                    else if (inTree) treeNavigator.selectNext();
                    else if (inEditor) editor.moveDown(shiftHeld[0]);
                    client.repaint();
                }
                case Keys.LEFT -> {
                    if (inTree) treeNavigator.toggleExpanded();
                    else if (inEditor) editor.moveLeft(shiftHeld[0]);
                }
                case Keys.RIGHT -> {
                    if (inTree) treeNavigator.toggleExpanded();
                    else if (inEditor) editor.moveRight(shiftHeld[0]);
                }
                case Keys.HOME -> { if (inEditor) editor.moveHome(shiftHeld[0]); }
                case Keys.END -> { if (inEditor) editor.moveEnd(shiftHeld[0]); }
                case Keys.PAGE_UP -> { if (inEditor) editor.scroll(-editor.visibleLines); }
                case Keys.PAGE_DOWN -> { if (inEditor) editor.scroll(editor.visibleLines); }
                case Keys.ENTER -> {
                    if (inExplorer) navigator.files.activateSelected();
                    else if (inTree) treeNavigator.activateSelected();
                    else if (inEditor) editor.insertNewline();
                    client.repaint();
                }
                case Keys.BACKSPACE -> {
                    if (inExplorer) navigator.files.navigateUp();
                    else if (inEditor) editor.backspace();
                }
                case Keys.DELETE -> { if (inEditor) editor.delete(); }
                default -> {
                    if (!ctrlHeld[0] && ch != null && !ch.isEmpty()) {
                        char c = ch.charAt(0);
                        if (c >= 32) {
                            // TextBox input disabled for now
                            // input.commandText.insertChar(c);
                            client.repaint();
                        }
                    }
                }
            }
        });
    }

    // ------------------------------------------------------------
    // MOUSE
    // ------------------------------------------------------------

    private void initMouse() {

        final int[] mouseCell = {-1, -1};
        final boolean[] mouseDown = {false};

        AnsiMouse.open(new FastMouseListener() {

            @Override
            public void onMouseMove(long h, int dx, int dy, int absX, int absY) {
                mouseCell[0] = absX;
                mouseCell[1] = absY;

                splitView.dispatchMouseMove(absX, absY, mouseDown[0]);
                EventDispatcher.dispatchMouseMove(container, absX, absY);

                if (splitView.isDividerDragging()) {
                    navigator.onResize();
                    editor.onResize();
                }

                client.repaint();
            }

            @Override
            public void onMouseButton(long h, int buttonId, boolean isPressed) {
                if (buttonId != 0) return;

                mouseDown[0] = isPressed;

                splitView.dispatchMouseClick(mouseCell[0], mouseCell[1], isPressed);
                EventDispatcher.dispatchMouseClick(container, mouseCell[0], mouseCell[1], isPressed);

                client.repaint();
            }

            @Override
            public void onMouseWheel(long h, int delta) {

                ViewMode mode = client.getViewMode();

                if (mode == ViewMode.EXPLORER) {
                    navigator.files.scroll(delta);
                } else if (mode == ViewMode.EDITOR) {
                    editor.scroll(delta);
                } else if (mode == ViewMode.SPLIT) {
                    SplitHorizontal split = splitView.getSplit();
                    if (mouseCell[0] < split.getDivider().getX()) {
                        navigator.files.scroll(delta);
                    } else {
                        editor.scroll(delta);
                    }
                }

                client.repaint();
            }
        });
    }

    // ------------------------------------------------------------
    // RESIZE WATCHER
    // ------------------------------------------------------------

    private void initResizeWatcher() {
        Thread watcher = new Thread(() -> {
            while (true) {
                try { Thread.sleep(50); } catch (InterruptedException e) { return; }

                int[] size = FastTerminal.getWindowSize(client.termCols, client.termRows);
                if (size[0] == client.termCols && size[1] == client.termRows) continue;

                client.handleTerminalResize(size[0], size[1]);
            }
        }, "terminal-resize");

        watcher.setDaemon(true);
        watcher.start();
    }

    // ------------------------------------------------------------
    // FILE INDEX ACTION
    // ------------------------------------------------------------

    private void triggerFileIndex() {
        new Thread(() -> {
            String[] roots = {"C:\\"};
            final long[] lastRepaint = {0};

            FastFileIndex.buildWithProgress(
                    roots,
                    (fastfileindex.ProgressCallback) (current, total, path) -> {
                        if (path != null) {
                            int lastSep = path.lastIndexOf('\\');
                            String directory = lastSep >= 0 ? path.substring(0, lastSep) : "";
                            String filename  = lastSep >= 0 ? path.substring(lastSep + 1) : path;

                            navigator.files.setCurrentDirectory(directory);
                            navigator.files.hoverFile(filename);
                        }

                        long now = System.currentTimeMillis();
                        if (now - lastRepaint[0] >= 16) {
                            lastRepaint[0] = now;
                            client.repaint();
                        }
                    }
            );

            client.repaint();
        }, "file-index-thread").start();
    }

}
