package cream.cli;

import fastterminal.FastTerminal;
import fastterminal.FastTerminalRenderer;
import fastterminal.FastTerminalScene;
import fasttui.component.Container;
import fasttui.layout.SplitHorizontal;
import fasttui.layout.SplitView;

public class Client {

    public static void main(String[] args) {
        Thread t = new Thread(Client::new);
        t.start();
    }


    private final FastTerminalRenderer renderer;
    private final FastTerminalScene scene;
    private final Container container;
    private final SplitView splitView;
    private final Navigator navigator;
    private final TreeNavigator treeNavigator;
    private final Input input;
    private final Editor editor;
    private ViewMode viewMode = ViewMode.EXPLORER;
    public int termCols;
    public int termRows;
    private Console console;

    private SplitHorizontal splitWithNavigator;
    private SplitHorizontal splitWithTree;

    public Client() {

        FastTerminal.setTitle("Cream CLI");
        FastTerminal.setRawMode(true);
        final int[] size = FastTerminal.getTerminalSize();
        this.termCols = size[0];
        this.termRows = size[1];
        final int contentRows = termRows - 4;

        this.scene = new FastTerminalScene(0, 0, this.termCols, this.termRows);
        this.renderer = new FastTerminalRenderer(this.termCols, this.termRows);
        this.renderer.setDiffRenderingEnabled(true);
        this.renderer.setDirtyRectanglesEnabled(true);
        this.renderer.addScene(this.scene);

        this.container = new Container(0, 0, this.termCols, this.termRows);
        this.navigator = new Navigator(this.termCols, this.termRows);
        this.treeNavigator = new TreeNavigator(this.termCols, this.termRows);
        this.input = new Input(this.termCols, this.termRows);
        this.editor = new Editor(this.termCols, this.termRows);

        final Events events = new Events(this, this.scene, this.renderer, this.container);

        this.navigator.files.setFileOpenListener(file -> {
            editor.loadFile(file);
            setViewMode(ViewMode.EDITOR);
        });

        this.splitWithNavigator = new SplitHorizontal(navigator, editor);
        this.splitWithTree = new SplitHorizontal(treeNavigator, editor);

        this.splitView = new SplitView(0, 0, termCols, contentRows);
        this.splitView.addSplit(splitWithNavigator);

        this.container.add(splitView);
        this.container.add(input);

        this.console = new Console();

        // startSplitTracker();

        this.setViewMode(ViewMode.EXPLORER);

        events.initAll(this.navigator, this.treeNavigator, this.editor, this.input, this.splitView);
        events.setupShutdownHook();

        this.scene.clear();
        this.renderer.clearPrev();
        repaint();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {
        }
    }

    public void repaint() {
        this.scene.clear();
        this.container.render(scene);
        this.renderer.render();
    }

    public void handleTerminalResize(int cols, int rows) {
        this.termCols = cols;
        this.termRows = rows;
        final int contentRows = rows - 4;

        this.renderer.resize(cols, rows);
        this.scene.resize(cols, rows);

        this.container.setWidth(cols);
        this.container.setHeight(rows);

        this.splitView.setWidth(cols);
        this.splitView.setHeight(contentRows);

        this.navigator.setHeight(contentRows);
        this.editor.setHeight(contentRows);

        this.input.onResize(cols, rows);

        this.splitView.layoutSplit();
        this.navigator.onResize();
        this.editor.onResize();

        this.scene.clear();
        this.renderer.clearPrev();
        this.repaint();
    }

    public void setViewMode(ViewMode mode) {
        this.viewMode = mode;
        switch (mode) {
            case EXPLORER -> {
                splitView.addSplit(splitWithNavigator);
                splitWithNavigator.setPaneMode(SplitHorizontal.PaneMode.LEFT_ONLY);
            }
            case TREE -> {
                splitView.addSplit(splitWithTree);
                splitWithTree.setPaneMode(SplitHorizontal.PaneMode.LEFT_ONLY);
                treeNavigator.setRoot(new java.io.File("."));
            }
            case EDITOR -> {
                splitView.addSplit(splitWithNavigator);
                splitWithNavigator.setPaneMode(SplitHorizontal.PaneMode.RIGHT_ONLY);
            }
            case SPLIT -> {
                splitView.addSplit(splitWithNavigator);
                splitWithNavigator.setPaneMode(SplitHorizontal.PaneMode.SPLIT);
            }
        }
        this.splitView.layoutSplit();
        this.navigator.onResize();
        this.treeNavigator.onResize();
        this.editor.onResize();
        this.scene.clear();
        this.renderer.clearPrev();
    }

    public ViewMode getViewMode() {
        return this.viewMode;
    }

    private void startSplitTracker() {
        Thread tracker = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }

                SplitHorizontal split = splitView.getSplit();
                if (split != null) {
                    String status = String.format(
                            "Split Status | Mode: %s | Ratio: %.2f | Left: %dpx | Right: %dpx | Divider: %dpx | Dragging: %s",
                            split.getPaneMode(),
                            split.getRatio(),
                            split.getLeft().getWidth(),
                            split.getRight().getWidth(),
                            split.getDivider().getWidth(),
                            split.isDragging()
                    );
                    console.appendLine(status);
                }
            }
        }, "split-tracker");

        tracker.setDaemon(true);
        tracker.start();
    }
}
