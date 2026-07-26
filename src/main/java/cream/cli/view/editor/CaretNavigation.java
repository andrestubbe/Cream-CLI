package cream.cli.view.editor;

/**
 * Handles caret positioning, selection, and viewport visibility for Editor.
 */
public class CaretNavigation {

    private final Editor editor;

    public CaretNavigation(Editor editor) {
        this.editor = editor;
    }

    public void ensureCaretVisible() {
        editor.scrollController.ensureVisible(editor.caret.getLine());
    }

    public void moveCaret(int line, int col, boolean extend) {
        if (!extend) {
            editor.selection.clear();
        } else if (!editor.selection.isActive()) {
            editor.selection.start(editor.caret.getLine(), editor.caret.getCol());
        }

        line = Math.max(0, Math.min(line, editor.buffer.lineCount() - 1));
        col = Math.max(0, Math.min(col, editor.buffer.getLine(line).length()));

        editor.caret.set(line, col);

        if (editor.selection.isActive() &&
                editor.caret.getLine() == editor.selection.getAnchorLine() &&
                editor.caret.getCol() == editor.selection.getAnchorCol()) {
            editor.selection.clear();
        }

        ensureCaretVisible();
        editor.refresh();
    }

    public void moveLeft(boolean extend) {
        int caretLine = editor.caret.getLine();
        int caretCol = editor.caret.getCol();
        if (caretCol > 0) {
            moveCaret(caretLine, caretCol - 1, extend);
        } else if (caretLine > 0) {
            moveCaret(caretLine - 1, editor.buffer.getLine(caretLine - 1).length(), extend);
        }
    }

    public void moveRight(boolean extend) {
        int caretLine = editor.caret.getLine();
        int caretCol = editor.caret.getCol();
        String line = editor.buffer.getLine(caretLine);
        if (caretCol < line.length()) moveCaret(caretLine, caretCol + 1, extend);
        else if (caretLine < editor.buffer.lineCount() - 1) moveCaret(caretLine + 1, 0, extend);
    }

    public void moveWordLeft(boolean extend) {
        int caretLine = editor.caret.getLine();
        int caretCol = editor.caret.getCol();
        if (caretCol == 0) {
            if (caretLine > 0) {
                int prevLen = editor.buffer.getLine(caretLine - 1).length();
                moveCaret(caretLine - 1, prevLen, extend);
            }
            return;
        }
        String line = editor.buffer.getLine(caretLine);
        int col = caretCol - 1;
        // Skip trailing spaces/non-word characters
        while (col > 0 && !Character.isJavaIdentifierPart(line.charAt(col))) {
            col--;
        }
        // Skip word characters to find start of word
        while (col > 0 && Character.isJavaIdentifierPart(line.charAt(col - 1))) {
            col--;
        }
        moveCaret(caretLine, col, extend);
    }

    public void moveWordRight(boolean extend) {
        int caretLine = editor.caret.getLine();
        int caretCol = editor.caret.getCol();
        String line = editor.buffer.getLine(caretLine);
        int len = line.length();
        if (caretCol >= len) {
            if (caretLine < editor.buffer.lineCount() - 1) {
                moveCaret(caretLine + 1, 0, extend);
            }
            return;
        }
        int col = caretCol;
        // Skip word characters
        while (col < len && Character.isJavaIdentifierPart(line.charAt(col))) {
            col++;
        }
        // Skip trailing spaces/punctuation to start of next word
        while (col < len && !Character.isJavaIdentifierPart(line.charAt(col))) {
            col++;
        }
        moveCaret(caretLine, col, extend);
    }

    public void moveUp(boolean extend) {
        int caretLine = editor.caret.getLine();
        if (caretLine > 0) {
            moveCaret(caretLine - 1, editor.caret.getCol(), extend);
        }
    }

    public void moveDown(boolean extend) {
        int caretLine = editor.caret.getLine();
        if (caretLine < editor.buffer.lineCount() - 1) {
            moveCaret(caretLine + 1, editor.caret.getCol(), extend);
        }
    }

    public void moveHome(boolean extend) {
        this.moveCaret(editor.caret.getLine(), 0, extend);
    }

    public void moveEnd(boolean extend) {
        int caretLine = editor.caret.getLine();
        this.moveCaret(caretLine, editor.buffer.getLine(caretLine).length(), extend);
    }

    public void selectAll() {
        editor.selection.start(0, 0);
        int last = editor.buffer.lineCount() - 1;
        editor.caret.set(last, editor.buffer.getLine(last).length());
        ensureCaretVisible();
        editor.refresh();
    }
}
