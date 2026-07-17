package cream.cli;

import fastterminal.FastTerminalScene;
import fasttui.component.Component;
import fasttui.component.Container;
import fasttui.component.Control;
import fasttui.composable.ScrollVertical;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Editor extends Container {

    // ── Document state ────────────────────────────────────────────────────────
    final ArrayList<String> lines = new ArrayList<>();
    boolean isJava = false;

    // ── Caret ─────────────────────────────────────────────────────────────────
    int caretLine = 0;
    int caretCol = 0;

    // ── Selection anchor (where Shift-selection started) ──────────────────────
    int selAnchorLine = 0;
    int selAnchorCol = 0;
    boolean hasSelection = false;

    // ── Layout ────────────────────────────────────────────────────────────────
    private final int cols;
    final int visibleLines;
    /**
     * Left edge of the code area (after line-number gutter).
     */
    static final int CODE_X = 6;

    // ── Components ────────────────────────────────────────────────────────────
    private final LineNumber[] numbers;
    final CodeLine[] codeLines;
    private final ScrollVertical scroll;

    /**
     * Vertical scroll offset (first visible document line).
     */
    int scrollOffset = 0;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Invisible control overlay that receives all mouse/drag events for the editor.
     */
    private final EditorOverlay overlay;

    public Editor(int cols, int rows) {
        super(0, 0, cols, rows);

        this.cols = cols;
        this.visibleLines = rows - 4;

        this.numbers = new LineNumber[visibleLines];
        this.codeLines = new CodeLine[visibleLines];

        for (int r = 0; r < visibleLines; r++) {
            numbers[r] = new LineNumber(1, r);
            codeLines[r] = new CodeLine(CODE_X, r, this);
        }

        this.scroll = new ScrollVertical(cols - 1, 0, visibleLines,
                Theme.SCROLL_FOREGROUND, Theme.SCROLL_BACKGROUND);
        this.scroll.update(1, visibleLines, 0);

        // Transparent overlay covering the code area — carries mouse behaviour
        this.overlay = new EditorOverlay(CODE_X, 0, cols - CODE_X - 1, visibleLines);
        this.overlay.addBehavior(new EditorBehaviour(this));

        lines.add(""); // start with one empty line

        this.addAll(getComponents());
        refresh();
    }

    // ── File loading ─────────────────────────────────────────────────────────

    public void loadFile(File file) {
        lines.clear();
        scrollOffset = 0;
        caretLine = 0;
        caretCol = 0;
        clearSelection();
        try {
            List<String> read = java.nio.file.Files.readAllLines(Path.of(file.getAbsolutePath()));
            lines.addAll(read);
        } catch (IOException e) {
            lines.add("[Error reading file: " + e.getMessage() + "]");
        }
        if (lines.isEmpty()) lines.add("");
        isJava = file.getName().toLowerCase().endsWith(".java");
        refresh();
    }

    // ── Scroll ───────────────────────────────────────────────────────────────

    public void scroll(int delta) {
        int maxOffset = Math.max(0, lines.size() - visibleLines);
        scrollOffset = Math.max(0, Math.min(scrollOffset + delta, maxOffset));
        refresh();
    }

    /**
     * Ensure the caret line is visible; scrolls if needed.
     */
    void ensureCaretVisible() {
        if (caretLine < scrollOffset) {
            scrollOffset = caretLine;
        } else if (caretLine >= scrollOffset + visibleLines) {
            scrollOffset = caretLine - visibleLines + 1;
        }
    }

    // ── Caret movement ───────────────────────────────────────────────────────

    public void moveCaret(int line, int col, boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection) {
            selAnchorLine = caretLine;
            selAnchorCol = caretCol;
            hasSelection = true;
        }
        caretLine = Math.max(0, Math.min(line, lines.size() - 1));
        caretCol = Math.max(0, Math.min(col, lines.get(caretLine).length()));
        if (hasSelection && caretLine == selAnchorLine && caretCol == selAnchorCol) {
            clearSelection();
        }
        ensureCaretVisible();
        refresh();
    }

    public void moveLeft(boolean extend) {
        if (caretCol > 0) {
            moveCaret(caretLine, caretCol - 1, extend);
        } else if (caretLine > 0) {
            moveCaret(caretLine - 1, lines.get(caretLine - 1).length(), extend);
        }
    }

    public void moveRight(boolean extend) {
        if (caretCol < lines.get(caretLine).length()) {
            moveCaret(caretLine, caretCol + 1, extend);
        } else if (caretLine < lines.size() - 1) {
            moveCaret(caretLine + 1, 0, extend);
        }
    }

    public void moveUp(boolean extend) {
        if (caretLine > 0) moveCaret(caretLine - 1, caretCol, extend);
    }

    public void moveDown(boolean extend) {
        if (caretLine < lines.size() - 1) moveCaret(caretLine + 1, caretCol, extend);
    }

    public void moveHome(boolean extend) {
        moveCaret(caretLine, 0, extend);
    }

    public void moveEnd(boolean extend) {
        moveCaret(caretLine, lines.get(caretLine).length(), extend);
    }

    // ── Selection ────────────────────────────────────────────────────────────

    void clearSelection() {
        hasSelection = false;
        selAnchorLine = caretLine;
        selAnchorCol = caretCol;
    }

    public void selectAll() {
        selAnchorLine = 0;
        selAnchorCol = 0;
        hasSelection = true;
        caretLine = lines.size() - 1;
        caretCol = lines.get(caretLine).length();
        ensureCaretVisible();
        refresh();
    }

    /**
     * Returns {minLine, minCol, maxLine, maxCol} of the current selection.
     */
    int[] selectionBounds() {
        if (!hasSelection) return null;
        boolean anchorFirst = selAnchorLine < caretLine
                || (selAnchorLine == caretLine && selAnchorCol <= caretCol);
        if (anchorFirst) return new int[]{selAnchorLine, selAnchorCol, caretLine, caretCol};
        else return new int[]{caretLine, caretCol, selAnchorLine, selAnchorCol};
    }

    String getSelectedText() {
        int[] b = selectionBounds();
        if (b == null) return "";
        if (b[0] == b[2]) {
            return lines.get(b[0]).substring(b[1], b[3]);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(lines.get(b[0]).substring(b[1])).append('\n');
        for (int l = b[0] + 1; l < b[2]; l++) sb.append(lines.get(l)).append('\n');
        sb.append(lines.get(b[2]).substring(0, b[3]));
        return sb.toString();
    }

    void deleteSelection() {
        int[] b = selectionBounds();
        if (b == null) return;
        if (b[0] == b[2]) {
            String line = lines.get(b[0]);
            lines.set(b[0], line.substring(0, b[1]) + line.substring(b[3]));
        } else {
            String firstPart = lines.get(b[0]).substring(0, b[1]);
            String lastPart = lines.get(b[2]).substring(b[3]);
            lines.subList(b[0], b[2] + 1).clear();
            lines.add(b[0], firstPart + lastPart);
        }
        caretLine = b[0];
        caretCol = b[1];
        clearSelection();
    }

    // ── Editing ──────────────────────────────────────────────────────────────

    public void insertChar(char c) {
        if (hasSelection) deleteSelection();
        String line = lines.get(caretLine);
        lines.set(caretLine, line.substring(0, caretCol) + c + line.substring(caretCol));
        caretCol++;
        ensureCaretVisible();
        refresh();
    }

    public void insertNewline() {
        if (hasSelection) deleteSelection();
        String line = lines.get(caretLine);
        String rest = line.substring(caretCol);
        lines.set(caretLine, line.substring(0, caretCol));
        lines.add(caretLine + 1, rest);
        caretLine++;
        caretCol = 0;
        ensureCaretVisible();
        refresh();
    }

    public void backspace() {
        if (hasSelection) {
            deleteSelection();
            refresh();
            return;
        }
        if (caretCol > 0) {
            String line = lines.get(caretLine);
            lines.set(caretLine, line.substring(0, caretCol - 1) + line.substring(caretCol));
            caretCol--;
        } else if (caretLine > 0) {
            String prev = lines.get(caretLine - 1);
            String cur = lines.remove(caretLine);
            caretLine--;
            caretCol = prev.length();
            lines.set(caretLine, prev + cur);
        }
        ensureCaretVisible();
        refresh();
    }

    public void delete() {
        if (hasSelection) {
            deleteSelection();
            refresh();
            return;
        }
        String line = lines.get(caretLine);
        if (caretCol < line.length()) {
            lines.set(caretLine, line.substring(0, caretCol) + line.substring(caretCol + 1));
        } else if (caretLine < lines.size() - 1) {
            String next = lines.remove(caretLine + 1);
            lines.set(caretLine, line + next);
        }
        refresh();
    }

    // ── Clipboard ────────────────────────────────────────────────────────────

    public void copy() {
        String text = getSelectedText();
        if (text.isEmpty()) return;
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(text), null);
        } catch (Exception ignored) {
        }
    }

    public void cut() {
        copy();
        if (hasSelection) {
            deleteSelection();
            refresh();
        }
    }

    public void paste() {
        try {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (!cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) return;
            String text = (String) cb.getData(DataFlavor.stringFlavor);
            if (hasSelection) deleteSelection();
            String[] parts = text.split("\n", -1);
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    String line = lines.get(caretLine);
                    lines.set(caretLine, line.substring(0, caretCol) + parts[0] + line.substring(caretCol));
                    caretCol += parts[0].length();
                } else {
                    String line = lines.get(caretLine);
                    String rest = line.substring(caretCol);
                    lines.set(caretLine, line.substring(0, caretCol));
                    lines.add(caretLine + 1, parts[i] + rest);
                    caretLine++;
                    caretCol = parts[i].length();
                }
            }
            ensureCaretVisible();
            refresh();
        } catch (Exception ignored) {
        }
    }

    // ── Caret-from-mouse ─────────────────────────────────────────────────────

    /**
     * Convert terminal cell coordinates to document (line, col).
     */
    int[] cellToDocPos(int cellX, int cellY) {
        int docLine = Math.min(scrollOffset + cellY, lines.size() - 1);
        docLine = Math.max(0, docLine);
        int docCol = Math.max(0, Math.min(cellX - CODE_X, lines.get(docLine).length()));
        return new int[]{docLine, docCol};
    }

    // ── Refresh ──────────────────────────────────────────────────────────────

    void refresh() {
        boolean scrollNeeded = lines.size() > visibleLines;
        scroll.setVisible(scrollNeeded);
        if (scrollNeeded) scroll.update(Math.max(1, lines.size()), visibleLines, scrollOffset);

        for (int r = 0; r < visibleLines; r++) {
            int docLine = scrollOffset + r;
            boolean valid = docLine < lines.size();
            numbers[r].setText(valid ? String.valueOf(docLine + 1) : "");
            codeLines[r].setDocLine(valid ? docLine : -1);
        }
    }

    public Component[] getComponents() {
        return Stream.concat(
                Stream.concat(
                        Stream.concat(Arrays.stream(numbers), Arrays.stream(codeLines)),
                        Stream.of(scroll)
                ),
                Stream.of(overlay)
        ).toArray(Component[]::new);
    }

    /**
     * Reposition child components after the parent panel was resized by SplitView.
     */
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

    // ── Transparent overlay for mouse hit-testing ─────────────────────────────

    static class EditorOverlay extends Control {
        EditorOverlay(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public void render(FastTerminalScene scene) { /* transparent */ }
    }

    // =========================================================================
    // Inner class: line number gutter cell
    // =========================================================================

    static class LineNumber extends Component {
        private String text = "";

        LineNumber(int x, int y) {
            super(x, y, 4, 1);
        }

        void setText(String text) {
            this.text = text != null ? text : "";
        }

        @Override
        public void render(FastTerminalScene scene) {
            if (!isVisible()) return;
            scene.writeString(x, y, text, 0x444444, -1);
        }
    }

    // =========================================================================
    // Inner class: one visible row
    // =========================================================================

    static final int SEL_BG = 0x264F78;
    static final int CARET_BG = 0xAEAFAD;
    static final int CARET_FG = 0x1A1A2E;

    static class CodeLine extends Component {
        private final Editor editor;
        private int docLine = -1;

        CodeLine(int x, int y, Editor editor) {
            super(x, y, 1, 1);
            this.editor = editor;
        }

        void setDocLine(int docLine) {
            this.docLine = docLine;
        }

        @Override
        public void render(FastTerminalScene scene) {
            if (!isVisible()) return;
            if (docLine < 0 || docLine >= editor.lines.size()) return;

            String text = editor.lines.get(docLine);
            int[] sel = editor.selectionBounds();
            int[] fgColors = buildFgColors(text);

            int len = text.length();
            for (int col = 0; col < len; col++) {
                boolean isCaret = (docLine == editor.caretLine && col == editor.caretCol);
                int bg = isCaret ? CARET_BG : getBg(sel, docLine, col);
                int fg = isCaret ? CARET_FG : fgColors[col];
                scene.writeCell(x + col, y, text.charAt(col), fg, bg);
            }

            if (docLine == editor.caretLine && editor.caretCol == len) {
                scene.writeCell(x + len, y, ' ', CARET_FG, CARET_BG);
            }

            if (sel != null && docLine >= sel[0] && docLine < sel[2] && len == 0) {
                scene.writeCell(x, y, ' ', 0xCCCCCC, SEL_BG);
            }
        }

        private int getBg(int[] sel, int docLine, int col) {
            if (sel == null) return -1;
            int sLine = sel[0], sCol = sel[1], eLine = sel[2], eCol = sel[3];
            if (docLine == sLine && docLine == eLine) {
                return (col >= sCol && col < eCol) ? SEL_BG : -1;
            } else if (docLine == sLine) {
                return col >= sCol ? SEL_BG : -1;
            } else if (docLine == eLine) {
                return col < eCol ? SEL_BG : -1;
            } else if (docLine > sLine && docLine < eLine) {
                return SEL_BG;
            }
            return -1;
        }

        private int[] buildFgColors(String text) {
            int len = text.length();
            int[] fg = new int[len];
            Arrays.fill(fg, 0xCCCCCC);
            if (!editor.isJava) return fg;

            int i = 0;
            while (i < len) {
                char c = text.charAt(i);
                if (c == '/' && i + 1 < len && text.charAt(i + 1) == '/') {
                    Arrays.fill(fg, i, len, 0x565F89);
                    break;
                }
                if (c == '"') {
                    int start = i++;
                    while (i < len && text.charAt(i) != '"') {
                        if (text.charAt(i) == '\\' && i + 1 < len) i += 2;
                        else i++;
                    }
                    if (i < len) i++;
                    Arrays.fill(fg, start, Math.min(i, len), 0x9ECE6A);
                    continue;
                }
                if (Character.isDigit(c)) {
                    int start = i;
                    while (i < len) {
                        char ch = text.charAt(i);
                        if (Character.isDigit(ch) || ch == 'x'
                                || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) i++;
                        else break;
                    }
                    Arrays.fill(fg, start, i, 0xFF9E64);
                    continue;
                }
                if (Character.isJavaIdentifierStart(c)) {
                    int start = i;
                    while (i < len && Character.isJavaIdentifierPart(text.charAt(i))) i++;
                    String word = text.substring(start, i);
                    int color = 0xC0CAF5;
                    if (isKeyword(word)) color = 0xBB9AF7;
                    else if (!word.isEmpty() && Character.isUpperCase(word.charAt(0))) color = 0x2AC3DE;
                    Arrays.fill(fg, start, i, color);
                    continue;
                }
                if (!Character.isWhitespace(c)) fg[i] = 0xA9B1D6;
                i++;
            }
            return fg;
        }

        private static boolean isKeyword(String w) {
            return switch (w) {
                case "package", "import", "public", "private", "protected",
                     "final", "static", "class", "interface", "enum",
                     "new", "for", "while", "if", "else", "return", "switch", "case", "default",
                     "this", "super", "extends", "implements",
                     "int", "long", "double", "float", "boolean",
                     "char", "byte", "short", "void", "null", "true", "false" -> true;
                default -> false;
            };
        }
    }
}
