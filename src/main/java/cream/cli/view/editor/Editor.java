package cream.cli.view.editor;

import cream.cli.Theme;
import cream.cli.view.editor.syntax.PlainTextHighlighter;
import cream.cli.view.editor.syntax.SyntaxHighlighter;
import cream.cli.view.ui.ScrollController;
import fasttui.component.Component;
import fasttui.component.Container;
import fasttui.composable.ScrollVertical;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Pure MVC View Container for the Editor in CreamCLI.
 * Delegates file, navigation, editing, and autocomplete logic to specialized sub-managers.
 */
public class Editor extends Container {

    public record HyperlinkRange(int line, int startCol, int endCol, File targetFile) {}

    private HyperlinkRange hoveredHyperlink = null;

    // Core
    final DocumentBuffer buffer = new DocumentBuffer();
    final Caret caret = new Caret();
    final Selection selection = new Selection();

    private SyntaxHighlighter highlighter = PlainTextHighlighter.INSTANCE;

    // Layout
    private final int cols;
    public final int visibleLines;
    static final int CODE_X = 6;
    public final ScrollController scrollController;
    private Runnable repaintTrigger;

    // Components
    private final LineNumber[] numbers;
    private final CodeLine[] codeLines;
    private final ScrollVertical scroll;
    private final EditorOverlay overlay;

    // Sub-Managers
    public final EditorFileManager fileManager = new EditorFileManager(this);
    public final CaretNavigation caretNav = new CaretNavigation(this);
    public final SelectionManager selectionMgr = new SelectionManager(this);
    public final DocumentOperations docOps = new DocumentOperations(this);
    public final EditorAutocompleteManager autocompleteMgr = new EditorAutocompleteManager(this);

    public Editor(int cols, int rows) {
        super(0, 0, cols, rows);

        this.cols = cols;
        this.visibleLines = rows - 4;
        this.numbers = new LineNumber[this.visibleLines];
        this.codeLines = new CodeLine[this.visibleLines];
        for (int r = 0; r < visibleLines; r++) {
            this.numbers[r] = new LineNumber(1, r);
            this.codeLines[r] = new CodeLine(CODE_X, r, this);
        }

        final int scrollX = cols - 1;
        final int scrollY = 0;
        this.scroll = new ScrollVertical(scrollX, scrollY, this.visibleLines, Theme.SCROLLBAR_FOREGROUND_SET, Theme.SCROLLBAR_BACKGROUND_SET);
        this.scrollController = new ScrollController(this.scroll, () -> {
            refreshLines();
            if (repaintTrigger != null) repaintTrigger.run();
        });

        this.overlay = new EditorOverlay(CODE_X, 0, cols - CODE_X - 1, visibleLines);
        this.overlay.addBehavior(new EditorBehaviour(this));
        this.addAll(getComponents());
        this.refresh();
    }

    public void setRepaintTrigger(Runnable repaintTrigger) {
        this.repaintTrigger = repaintTrigger;
    }

    // Scrolling

    public void scroll(int delta) {
        scrollController.scrollBy(delta);
    }

    // Mouse Mapping & Refresh

    public int[] cellToDocPos(int cellX, int cellY) {
        int docLine = Math.min(getScrollOffset() + cellY - getY(), buffer.lineCount() - 1);
        docLine = Math.max(0, docLine);
        int docCol = Math.max(0, Math.min(cellX - getX() - CODE_X, buffer.getLine(docLine).length()));
        return new int[]{docLine, docCol};
    }

    public void refresh() {
        scrollController.update(buffer.lineCount(), visibleLines);
        refreshLines();
    }

    private void refreshLines() {
        int offset = getScrollOffset();
        for (int r = 0; r < visibleLines; r++) {
            int docLine = offset + r;
            boolean valid = docLine < buffer.lineCount();
            numbers[r].setText(valid ? String.valueOf(docLine + 1) : "");
            codeLines[r].setDocLine(valid ? docLine : -1);
        }
    }

    // Resize
    public void relayout(int x, int width) {
        setX(x);
        setWidth(width);
        onResize();
    }

    public void onResize() {
        int x = getX();
        int w = getWidth();
        for (int r = 0; r < visibleLines; r++) {
            numbers[r].setX(x + 1);
            codeLines[r].setX(x + CODE_X);
        }
        scroll.setX(x + w - 1);
        overlay.setX(x + CODE_X);
        overlay.setWidth(Math.max(1, w - CODE_X - 1));
    }

    public Component[] getComponents() {
        return Stream.concat(Stream.concat(Arrays.stream(numbers), Arrays.stream(codeLines)), Stream.of(scroll, overlay)).toArray(Component[]::new);
    }

    public int getLineCount() {
        return buffer.lineCount();
    }

    public int getScrollOffset() {
        return scrollController.getScrollOffset();
    }

    public SyntaxHighlighter getHighlighter() {
        return highlighter;
    }

    public void setHighlighter(SyntaxHighlighter highlighter) {
        this.highlighter = highlighter;
    }

    public void setScrollOffset(int offset) {
        scrollController.setScrollOffset(offset);
    }

    public HyperlinkRange getHoveredHyperlink() {
        return hoveredHyperlink;
    }

    public void setHoveredHyperlink(HyperlinkRange range) {
        this.hoveredHyperlink = range;
    }

    public int[] getWordBoundsAt(int docLine, int docCol) {
        if (docLine < 0 || docLine >= buffer.lineCount()) return null;
        String line = buffer.getLine(docLine);
        if (docCol < 0 || docCol >= line.length()) return null;

        char c = line.charAt(docCol);
        if (!Character.isJavaIdentifierPart(c)) return null;

        int start = docCol;
        while (start > 0 && Character.isJavaIdentifierPart(line.charAt(start - 1))) {
            start--;
        }

        int end = docCol;
        while (end < line.length() && Character.isJavaIdentifierPart(line.charAt(end))) {
            end++;
        }

        return new int[]{start, end};
    }

    public String getWordAt(int docLine, int docCol) {
        int[] bounds = getWordBoundsAt(docLine, docCol);
        if (bounds == null) return null;
        return buffer.getLine(docLine).substring(bounds[0], bounds[1]);
    }
}
