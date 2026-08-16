package cream.cli.view.editor;

import cream.cli.view.editor.syntax.PlainTextHighlighter;
import cream.cli.view.editor.syntax.SyntaxHighlighter;
import cream.cli.view.theme.ThemeService;
import cream.cli.view.ui.ScrollController;
import fasttui.component.ColorSet;
import fasttui.component.Component;
import fasttui.component.Container;
import fasttui.composable.ScrollVertical;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

public class Editor extends Container {

    private final EditorFileManager editorFileManager;
    private final EditorCaretNavigation editorCaretNavigation;
    private final EditorSelectionManager editorSelectionManager;
    private final EditorDocumentOperations editorDocumentOperations;
    private final EditorAutocompleteManager editorAutocompleteManager;
    private final EditorDocumentBuffer editorDocumentBuffer;
    private final EditorCaret editorCaret;
    private final EditorSelection editorSelection;
    private final EditorLineNumber[] editorLineNumbers;
    private final EditorCodeLine[] editorCodeLines;
    private final EditorOverlay overlay;
    private final ScrollVertical editorScroll;
    private final ScrollController editorScrollController;

    private SyntaxHighlighter highlighter;
    private HyperlinkRange hoveredHyperlink = null;
    private Runnable repaintTrigger;

    static final int CODE_X = 6;

    public Editor(final int x, final int y, final int width, final int Height, final Runnable repaintTrigger) {
        super(x, y, width, Height);
        this.repaintTrigger = repaintTrigger;
        this.editorFileManager = new EditorFileManager(this);
        this.editorCaretNavigation = new EditorCaretNavigation(this);
        this.editorSelectionManager = new EditorSelectionManager(this);
        this.editorDocumentOperations = new EditorDocumentOperations(this);
        this.editorAutocompleteManager = new EditorAutocompleteManager(this);
        this.editorDocumentBuffer = new EditorDocumentBuffer();
        this.editorCaret = new EditorCaret();
        this.editorSelection = new EditorSelection();
        this.editorLineNumbers = new EditorLineNumber[this.height];
        this.editorCodeLines = new EditorCodeLine[this.height];
        for (int rows = 0; rows < this.height; rows++) {
            this.editorLineNumbers[rows] = new EditorLineNumber(1, rows);
            this.editorCodeLines[rows] = new EditorCodeLine(CODE_X, rows, this);
        }
        int transparent = -2;
        ColorSet scrollFg = new ColorSet(ThemeService.get().getScrollbarForegroundNormal(), ThemeService.get().getScrollbarForegroundHover(), ThemeService.get().getScrollbarForegroundNormal(), ThemeService.get().getScrollbarForegroundNormal());
        ColorSet scrollBg = new ColorSet(transparent, ThemeService.get().getScrollbarBackgroundHover(), transparent, transparent);
        this.editorScroll = new ScrollVertical(this.width - 1, 0, this.height, scrollFg, scrollBg);
        this.editorScrollController = new ScrollController(this.editorScroll, () -> {
            this.refreshLines();
            this.repaintTrigger.run();
        });
        this.highlighter = PlainTextHighlighter.INSTANCE;
        this.overlay = new EditorOverlay(CODE_X, 0, this.width - CODE_X - 1, this.getHeight());
        this.overlay.addBehavior(new EditorBehaviour(this));
        this.addAll(getComponents());
        this.refresh();
    }

    public void scroll(int delta) {
        editorScrollController.scrollBy(delta);
    }

    public int[] cellToDocPos(int cellX, int cellY) {
        int docLine = Math.min(getScrollOffset() + cellY - getY(), editorDocumentBuffer.lineCount() - 1);
        docLine = Math.max(0, docLine);
        int docCol = Math.max(0, Math.min(cellX - getX() - CODE_X, editorDocumentBuffer.getLine(docLine).length()));
        return new int[]{docLine, docCol};
    }

    public void refresh() {
        editorScrollController.update(editorDocumentBuffer.lineCount(), this.getHeight());
        refreshLines();
    }

    private void refreshLines() {
        int offset = getScrollOffset();
        for (int r = 0; r < this.getHeight(); r++) {
            int docLine = offset + r;
            boolean valid = docLine < editorDocumentBuffer.lineCount();
            editorLineNumbers[r].setText(valid ? String.valueOf(docLine + 1) : "");
            editorCodeLines[r].setDocLine(valid ? docLine : -1);
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
        for (int r = 0; r < this.getHeight(); r++) {
            editorLineNumbers[r].setX(x + 1);
            editorLineNumbers[r].setY(r);
            editorCodeLines[r].setX(x + CODE_X);
            editorCodeLines[r].setY(r);
        }
        editorScroll.setX(x + w - 1);
        overlay.setX(x + CODE_X);
        overlay.setWidth(Math.max(1, w - CODE_X - 1));
    }

    public Component[] getComponents() {
        return Stream.concat(
                Stream.concat(
                        Arrays.stream(editorLineNumbers),
                        Arrays.stream(editorCodeLines)
                ),
                Stream.of(editorScroll, overlay)).toArray(Component[]::new);
    }

    public EditorFileManager getEditorFileManager() {
        return this.editorFileManager;
    }

    public EditorCaretNavigation getEditorCaretNavigation() {
        return this.editorCaretNavigation;
    }

    public EditorSelectionManager getEditorSelectionManager() {
        return this.editorSelectionManager;
    }

    public EditorDocumentOperations getEditorDocumentOperations() {
        return this.editorDocumentOperations;
    }

    public EditorAutocompleteManager getEditorAutocompleteManager() {
        return this.editorAutocompleteManager;
    }

    public int getLineCount() {
        return this.editorDocumentBuffer.lineCount();
    }

    public int getScrollOffset() {
        return this.editorScrollController.getScrollOffset();
    }

    public SyntaxHighlighter getHighlighter() {
        return this.highlighter;
    }

    public HyperlinkRange getHoveredHyperlink() {
        return this.hoveredHyperlink;
    }

    public EditorDocumentBuffer getEditorDocumentBuffer() {
        return this.editorDocumentBuffer;
    }

    public EditorCaret getEditorCaret() {
        return this.editorCaret;
    }

    public EditorSelection getEditorSelection() {
        return this.editorSelection;
    }

    public ScrollController getEditorScrollController() {
        return this.editorScrollController;
    }

    public int[] getWordBoundsAt(int docLine, int docCol) {
        if (docLine < 0 || docLine >= editorDocumentBuffer.lineCount()) return null;
        String line = editorDocumentBuffer.getLine(docLine);
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

    public String getWordAt(final int docLine, final int docCol) {
        int[] bounds = this.getWordBoundsAt(docLine, docCol);
        if (bounds == null) return null;
        return editorDocumentBuffer.getLine(docLine).substring(bounds[0], bounds[1]);
    }

    public void setRepaintTrigger(final Runnable repaintTrigger) {
        this.repaintTrigger = repaintTrigger;
    }

    public void setHighlighter(final SyntaxHighlighter syntaxHighlighter) {
        this.highlighter = syntaxHighlighter;
    }

    public void setScrollOffset(final int scrollOffset) {
        this.editorScrollController.setScrollOffset(scrollOffset);
    }

    public void setHoveredHyperlink(final HyperlinkRange hyperlinkRange) {
        this.hoveredHyperlink = hyperlinkRange;
    }

    public record HyperlinkRange(int line, int startCol, int endCol, File targetFile) {
    }

}
