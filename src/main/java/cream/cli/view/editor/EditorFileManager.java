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
            String parent = currentFile.getParent() != null
                    ? currentFile.getParent().replace("\\", "\\\\")
                    : "";
            String json = String.format("{\"lastFile\":\"%s\",\"lastDir\":\"%s\",\"line\":%d,\"col\":%d}",
                    currentFile.getAbsolutePath().replace("\\", "\\\\"),
                    parent,
                    editor.caret.getLine(),
                    editor.caret.getCol());
            Files.writeString(getStateFilePath(), json);
        } catch (Exception ignored) {}
    }

    public static void saveDirectoryState(java.io.File dir) {
        try {
            Path stateFile = getStateFilePath();
            Path stateDir = stateFile.getParent();
            if (!Files.exists(stateDir)) Files.createDirectories(stateDir);
            // Preserve lastFile if present
            String existing = Files.exists(stateFile) ? Files.readString(stateFile) : "{}";
            String lastFile = "";
            int fi = existing.indexOf("\"lastFile\":\"");
            if (fi != -1) {
                int fe = existing.indexOf("\"", fi + 12);
                lastFile = existing.substring(fi + 12, fe);
            }
            String json;
            if (!lastFile.isEmpty()) {
                json = String.format("{\"lastFile\":\"%s\",\"lastDir\":\"%s\",\"line\":0,\"col\":0}",
                        lastFile, dir.getAbsolutePath().replace("\\", "\\\\"));
            } else {
                json = String.format("{\"lastDir\":\"%s\"}",
                        dir.getAbsolutePath().replace("\\", "\\\\"));
            }
            Files.writeString(stateFile, json);
        } catch (Exception ignored) {}
    }

    /** Saves only lastDir, clearing lastFile — used when closing a file intentionally. */
    public static void saveDirectoryOnly(java.io.File dir) {
        try {
            Path stateFile = getStateFilePath();
            Path stateDir = stateFile.getParent();
            if (!Files.exists(stateDir)) Files.createDirectories(stateDir);
            String json = String.format("{\"lastDir\":\"%s\"}",
                    dir.getAbsolutePath().replace("\\", "\\\\"));
            Files.writeString(stateFile, json);
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

    /** Returns the last visited directory from state.json, or null if not set / doesn't exist. */
    public static java.io.File readLastDir() {
        try {
            Path stateFile = getStateFilePath();
            if (!Files.exists(stateFile)) return null;
            String json = Files.readString(stateFile);
            int idx = json.indexOf("\"lastDir\":\"");
            if (idx == -1) return null;
            int end = json.indexOf("\"", idx + 11);
            String pathStr = json.substring(idx + 11, end).replace("\\\\", "\\");
            java.io.File dir = new java.io.File(pathStr);
            return dir.exists() && dir.isDirectory() ? dir : null;
        } catch (Exception e) {
            return null;
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
