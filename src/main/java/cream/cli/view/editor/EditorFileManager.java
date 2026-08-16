package cream.cli.view.editor;

import cream.cli.control.IOManager;
import cream.cli.view.editor.syntax.SyntaxHighlighterFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles file loading, saving, and undo/redo history for Editor.
 */
public class EditorFileManager {

    private final Editor editor;
    final EditorHistoryManager editorHistoryManager;
    private File currentFile = null;
    private boolean dirty = false;

    public EditorFileManager(Editor editor) {
        this.editor = editor;
        this.editorHistoryManager = new EditorHistoryManager();
    }

    public boolean restoreEditorState() {
        try {
            Path stateFile = IOManager.getStateFilePath();
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
            editor.getEditorCaret().set(line, col);
            editor.getEditorCaretNavigation().ensureCaretVisible();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void saveSnapshot() {
        editorHistoryManager.saveSnapshot(editor.getEditorDocumentBuffer().getLines(), editor.getEditorCaret().getLine(), editor.getEditorCaret().getCol());
    }

    public void loadFile(File file) {
        this.currentFile = file;
        this.dirty = false;
        editorHistoryManager.clear();
        editor.getEditorDocumentBuffer().clear();
        editor.getEditorCaret().set(0, 0);
        editor.getEditorSelection().clear();
        editor.setScrollOffset(0);

        try {
            editor.getEditorDocumentBuffer().getLines().clear();
            editor.getEditorDocumentBuffer().getLines().addAll(Files.readAllLines(Path.of(file.getAbsolutePath())));
        } catch (Exception e) {
            editor.getEditorDocumentBuffer().getLines().add("[Error reading file: " + e.getMessage() + "]");
        }

        if (editor.getEditorDocumentBuffer().lineCount() == 0) {
            editor.getEditorDocumentBuffer().getLines().add("");
        }

        editor.setHighlighter(SyntaxHighlighterFactory.getHighlighter(file.getName()));
        saveEditorState();
        editor.refresh();
    }

    public void saveEditorState() {
        if (currentFile == null) return;
        try {
            Path dir = IOManager.getStateFilePath().getParent();
            if (!Files.exists(dir)) Files.createDirectories(dir);
            String parent = currentFile.getParent() != null
                    ? currentFile.getParent().replace("\\", "\\\\")
                    : "";
            String json = String.format("{\"lastFile\":\"%s\",\"lastDir\":\"%s\",\"line\":%d,\"col\":%d}",
                    currentFile.getAbsolutePath().replace("\\", "\\\\"),
                    parent,
                    editor.getEditorCaret().getLine(),
                    editor.getEditorCaret().getCol());
            Files.writeString(IOManager.getStateFilePath(), json);
        } catch (Exception ignored) {
        }
    }

    public boolean saveFile() {
        if (currentFile == null) return false;
        try {
            Files.write(currentFile.toPath(), editor.getEditorDocumentBuffer().getLines());
            this.dirty = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveAs(File newFile) {
        if (newFile == null) return false;
        try {
            Files.write(newFile.toPath(), editor.getEditorDocumentBuffer().getLines());
            this.currentFile = newFile;
            this.dirty = false;
            editor.setHighlighter(SyntaxHighlighterFactory.getHighlighter(newFile.getName()));
            editor.refresh();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    public File getCurrentFile() {
        return this.currentFile;
    }

    public EditorHistoryManager getEditorUndoManager() {
        return this.editorHistoryManager;
    }
}
