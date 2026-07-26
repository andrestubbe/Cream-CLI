package cream.cli.view.editor;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class DocumentOperations {

    private final Editor editor;

    public DocumentOperations(Editor editor) {
        this.editor = editor;
    }

    public void insertChar(char c) {
        editor.fileManager.saveSnapshot();
        if (editor.selection.isActive()) editor.selectionMgr.deleteSelection();
        int l = editor.caret.getLine();
        int col = editor.caret.getCol();
        String line = editor.buffer.getLine(l);
        editor.buffer.setLine(l, line.substring(0, col) + c + line.substring(col));
        editor.caret.set(l, col + 1);
        editor.fileManager.setDirty(true);
        editor.caretNav.ensureCaretVisible();
        editor.refresh();
    }

    public void insertNewline() {
        editor.fileManager.saveSnapshot();
        if (editor.selection.isActive()) editor.selectionMgr.deleteSelection();
        int l = editor.caret.getLine();
        int col = editor.caret.getCol();
        String line = editor.buffer.getLine(l);
        String rest = line.substring(col);
        editor.buffer.setLine(l, line.substring(0, col));
        editor.buffer.insertLine(l + 1, rest);
        editor.caret.set(l + 1, 0);
        editor.fileManager.setDirty(true);
        editor.caretNav.ensureCaretVisible();
        editor.refresh();
    }

    public void backspace() {
        editor.fileManager.saveSnapshot();
        if (editor.selection.isActive()) {
            editor.selectionMgr.deleteSelection();
            editor.fileManager.setDirty(true);
            editor.refresh();
            return;
        }
        int l = editor.caret.getLine();
        int col = editor.caret.getCol();
        if (col > 0) {
            String line = editor.buffer.getLine(l);
            editor.buffer.setLine(l, line.substring(0, col - 1) + line.substring(col));
            editor.caret.set(l, col - 1);
            editor.fileManager.setDirty(true);
        } else if (l > 0) {
            String prev = editor.buffer.getLine(l - 1);
            String cur = editor.buffer.getLine(l);
            editor.buffer.removeLine(l);
            editor.caret.set(l - 1, prev.length());
            editor.buffer.setLine(editor.caret.getLine(), prev + cur);
            editor.fileManager.setDirty(true);
        }
        editor.caretNav.ensureCaretVisible();
        editor.refresh();
    }

    public void delete() {
        editor.fileManager.saveSnapshot();
        if (editor.selection.isActive()) {
            editor.selectionMgr.deleteSelection();
            editor.fileManager.setDirty(true);
            editor.refresh();
            return;
        }
        int l = editor.caret.getLine();
        int col = editor.caret.getCol();
        String line = editor.buffer.getLine(l);
        if (col < line.length()) {
            editor.buffer.setLine(l, line.substring(0, col) + line.substring(col + 1));
            editor.fileManager.setDirty(true);
        } else if (l < editor.buffer.lineCount() - 1) {
            String next = editor.buffer.getLine(l + 1);
            editor.buffer.removeLine(l + 1);
            editor.buffer.setLine(l, line + next);
            editor.fileManager.setDirty(true);
        }
        editor.refresh();
    }

    public void copy() {
        String text = editor.selectionMgr.getSelectedText();
        if (text.isEmpty()) return;
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
    }

    public void cut() {
        copy();
        if (editor.selection.isActive()) {
            editor.selectionMgr.deleteSelection();
            editor.refresh();
        }
    }

    public void paste() {
        try {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (!cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) return;
            String text = (String) cb.getData(DataFlavor.stringFlavor);

            if (editor.selection.isActive()) editor.selectionMgr.deleteSelection();

            String[] parts = text.split("\n", -1);

            for (int i = 0; i < parts.length; i++) {
                int l = editor.caret.getLine();
                int col = editor.caret.getCol();
                if (i == 0) {
                    String cur = editor.buffer.getLine(l);
                    editor.buffer.setLine(l, cur.substring(0, col) + parts[0] + cur.substring(col));
                    editor.caret.set(l, col + parts[0].length());
                } else {
                    String cur = editor.buffer.getLine(l);
                    String rest = cur.substring(col);
                    editor.buffer.setLine(l, cur.substring(0, col));
                    editor.buffer.insertLine(l + 1, parts[i] + rest);
                    editor.caret.set(l + 1, parts[i].length());
                }
            }

            editor.caretNav.ensureCaretVisible();
            editor.refresh();

        } catch (Exception ignored) {
        }
    }

    /** Deletes the entire line at the current caret position (Eclipse: Ctrl+D). */
    public void deleteCurrentLine() {
        editor.fileManager.saveSnapshot();
        int l = editor.caret.getLine();
        if (editor.buffer.lineCount() == 1) {
            editor.buffer.setLine(0, "");
            editor.caret.set(0, 0);
        } else {
            editor.buffer.removeLine(l);
            int newLine = Math.min(l, editor.buffer.lineCount() - 1);
            editor.caret.set(newLine, Math.min(editor.caret.getCol(), editor.buffer.getLine(newLine).length()));
        }
        editor.selection.clear();
        editor.fileManager.setDirty(true);
        editor.caretNav.ensureCaretVisible();
        editor.refresh();
    }

    /** Duplicates the current line, inserting a copy below (Eclipse: Ctrl+Alt+Down). */
    public void duplicateCurrentLine() {
        editor.fileManager.saveSnapshot();
        int l = editor.caret.getLine();
        String line = editor.buffer.getLine(l);
        editor.buffer.insertLine(l + 1, line);
        editor.caret.set(l + 1, editor.caret.getCol());
        editor.fileManager.setDirty(true);
        editor.caretNav.ensureCaretVisible();
        editor.refresh();
    }

    /** Moves the current line one position up (Eclipse: Alt+Up). */
    public void moveLineUp() {
        int l = editor.caret.getLine();
        if (l == 0) return;
        editor.fileManager.saveSnapshot();
        String cur = editor.buffer.getLine(l);
        String above = editor.buffer.getLine(l - 1);
        editor.buffer.setLine(l - 1, cur);
        editor.buffer.setLine(l, above);
        editor.caret.set(l - 1, editor.caret.getCol());
        editor.fileManager.setDirty(true);
        editor.caretNav.ensureCaretVisible();
        editor.refresh();
    }

    /** Moves the current line one position down (Eclipse: Alt+Down). */
    public void moveLineDown() {
        int l = editor.caret.getLine();
        if (l >= editor.buffer.lineCount() - 1) return;
        editor.fileManager.saveSnapshot();
        String cur = editor.buffer.getLine(l);
        String below = editor.buffer.getLine(l + 1);
        editor.buffer.setLine(l + 1, cur);
        editor.buffer.setLine(l, below);
        editor.caret.set(l + 1, editor.caret.getCol());
        editor.fileManager.setDirty(true);
        editor.caretNav.ensureCaretVisible();
        editor.refresh();
    }

    public void undo() {
        UndoManager.State state = editor.fileManager.undoManager.undo(
                editor.buffer.getLines(), editor.caret.getLine(), editor.caret.getCol()
        );
        if (state != null) {
            editor.buffer.getLines().clear();
            editor.buffer.getLines().addAll(state.lines);
            editor.caret.set(state.caretLine, state.caretCol);
            editor.selection.clear();
            editor.caretNav.ensureCaretVisible();
            editor.refresh();
        }
    }

    public void redo() {
        UndoManager.State state = editor.fileManager.undoManager.redo(
                editor.buffer.getLines(), editor.caret.getLine(), editor.caret.getCol()
        );
        if (state != null) {
            editor.buffer.getLines().clear();
            editor.buffer.getLines().addAll(state.lines);
            editor.caret.set(state.caretLine, state.caretCol);
            editor.selection.clear();
            editor.caretNav.ensureCaretVisible();
            editor.refresh();
        }
    }

    /** Toggles line comments (//) on current line or active selection (Ctrl+/). */
    public void toggleComment() {
        editor.fileManager.saveSnapshot();

        int startLine, endLine;
        if (editor.selection.isActive()) {
            int[] sel = editor.selectionMgr.selectionBounds();
            startLine = sel[0];
            endLine = sel[2];
            if (sel[3] == 0 && endLine > startLine) {
                endLine--;
            }
        } else {
            startLine = editor.caret.getLine();
            endLine = startLine;
        }

        // Determine if ALL non-empty lines in the range start with "//"
        boolean allCommented = true;
        int nonEmptyCount = 0;
        for (int l = startLine; l <= endLine; l++) {
            String line = editor.buffer.getLine(l);
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                nonEmptyCount++;
                if (!trimmed.startsWith("//")) {
                    allCommented = false;
                    break;
                }
            }
        }
        if (nonEmptyCount == 0) allCommented = false;

        // Apply or remove comment markers
        for (int l = startLine; l <= endLine; l++) {
            String line = editor.buffer.getLine(l);
            if (allCommented) {
                // Uncomment: strip "// " or "//"
                int idx = line.indexOf("//");
                if (idx != -1) {
                    String prefix = line.substring(0, idx);
                    String rest = line.substring(idx + 2);
                    if (rest.startsWith(" ")) rest = rest.substring(1);
                    editor.buffer.setLine(l, prefix + rest);
                }
            } else {
                // Comment: insert "// " at indentation level
                int indent = 0;
                while (indent < line.length() && Character.isWhitespace(line.charAt(indent))) {
                    indent++;
                }
                String prefix = line.substring(0, indent);
                String rest = line.substring(indent);
                editor.buffer.setLine(l, prefix + "// " + rest);
            }
        }

        editor.fileManager.setDirty(true);
        editor.caretNav.ensureCaretVisible();
        editor.refresh();
    }
}
