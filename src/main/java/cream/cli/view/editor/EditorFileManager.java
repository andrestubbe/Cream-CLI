package cream.cli.view.editor;

import cream.cli.view.editor.syntax.SyntaxHighlighterFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles file loading, saving, and undo/redo history for Editor.
 */
public class EditorFileManager {

    private final Editor editor;
    private File currentFile = null;
    private boolean dirty = false;
    final UndoManager undoManager = new UndoManager();

    public EditorFileManager(Editor editor) {
        this.editor = editor;
    }

    public void saveSnapshot() {
        undoManager.saveSnapshot(editor.buffer.getLines(), editor.caret.getLine(), editor.caret.getCol());
    }

    public void loadFile(File file) {
        this.currentFile = file;
        this.dirty = false;
        undoManager.clear();
        editor.buffer.clear();
        editor.caret.set(0, 0);
        editor.selection.clear();
        editor.setScrollOffset(0);

        try {
            editor.buffer.getLines().clear();
            editor.buffer.getLines().addAll(Files.readAllLines(Path.of(file.getAbsolutePath())));
        } catch (Exception e) {
            editor.buffer.getLines().add("[Error reading file: " + e.getMessage() + "]");
        }

        if (editor.buffer.lineCount() == 0) {
            editor.buffer.getLines().add("");
        }

        editor.setHighlighter(SyntaxHighlighterFactory.getHighlighter(file.getName()));
        saveEditorState();
        editor.refresh();
    }

    public static Path getStateFilePath() {
        return Path.of(System.getProperty("user.home"), ".cream", "state.json");
    }

    public void saveEditorState() {
        if (currentFile == null) return;
        try {
            Path dir = getStateFilePath().getParent();
            if (!Files.exists(dir)) Files.createDirectories(dir);
            String json = String.format("{\"lastFile\":\"%s\",\"line\":%d,\"col\":%d}",
                    currentFile.getAbsolutePath().replace("\\", "\\\\"),
                    editor.caret.getLine(),
                    editor.caret.getCol());
            Files.writeString(getStateFilePath(), json);
        } catch (Exception ignored) {}
    }

    public boolean restoreEditorState() {
        try {
            Path stateFile = getStateFilePath();
            if (!Files.exists(stateFile)) return false;
            String json = Files.readString(stateFile);
            int fileIdx = json.indexOf("\"lastFile\":\"");
            if (fileIdx == -1) return false;
            int fileEnd = json.indexOf("\"", fileIdx + 12);
            String pathStr = json.substring(fileIdx + 12, fileEnd).replace("\\\\", "\\");
            File f = new File(pathStr);
            if (!f.exists()) return false;

            int line = 0, col = 0;
            int lineIdx = json.indexOf("\"line\":");
            if (lineIdx != -1) {
                int end = json.indexOf(",", lineIdx);
                if (end == -1) end = json.indexOf("}", lineIdx);
                line = Integer.parseInt(json.substring(lineIdx + 7, end).trim());
            }
            int colIdx = json.indexOf("\"col\":");
            if (colIdx != -1) {
                int end = json.indexOf(",", colIdx);
                if (end == -1) end = json.indexOf("}", colIdx);
                col = Integer.parseInt(json.substring(colIdx + 6, end).trim());
            }

            loadFile(f);
            editor.caret.set(line, col);
            editor.caretNav.ensureCaretVisible();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveFile() {
        if (currentFile == null) return false;
        try {
            Files.write(currentFile.toPath(), editor.buffer.getLines());
            this.dirty = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveAs(File newFile) {
        if (newFile == null) return false;
        try {
            Files.write(newFile.toPath(), editor.buffer.getLines());
            this.currentFile = newFile;
            this.dirty = false;
            editor.setHighlighter(SyntaxHighlighterFactory.getHighlighter(newFile.getName()));
            editor.refresh();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void undo() {
        UndoManager.State state = undoManager.undo(editor.buffer.getLines(), editor.caret.getLine(), editor.caret.getCol());
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
        UndoManager.State state = undoManager.redo(editor.buffer.getLines(), editor.caret.getLine(), editor.caret.getCol());
        if (state != null) {
            editor.buffer.getLines().clear();
            editor.buffer.getLines().addAll(state.lines);
            editor.caret.set(state.caretLine, state.caretCol);
            editor.selection.clear();
            editor.caretNav.ensureCaretVisible();
            editor.refresh();
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public File getCurrentFile() {
        return currentFile;
    }
}
