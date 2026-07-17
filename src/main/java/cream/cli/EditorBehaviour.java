package cream.cli;

import fasttui.behaviour.Behaviour;
import fasttui.component.Component;

class EditorBehaviour implements Behaviour {

    private final Editor editor;
    private boolean mouseDown = false;

    EditorBehaviour(Editor editor) {
        this.editor = editor;
    }

    @Override
    public void onMousePressed(Component target, int cellX, int cellY) {
        mouseDown = true;
        int[] pos = editor.cellToDocPos(cellX, cellY);
        editor.moveCaret(pos[0], pos[1], false);
    }

    @Override
    public void onMouseReleased(Component target, int cellX, int cellY) {
        mouseDown = false;
    }

    @Override
    public void onMouseDragged(Component target, int cellX, int cellY) {
        if (!mouseDown) return;
        int[] pos = editor.cellToDocPos(cellX, cellY);
        if (!editor.hasSelection) {
            editor.selAnchorLine = editor.caretLine;
            editor.selAnchorCol  = editor.caretCol;
            editor.hasSelection  = true;
        }
        editor.caretLine = Math.max(0, Math.min(pos[0], editor.lines.size() - 1));
        editor.caretCol  = Math.max(0, Math.min(pos[1], editor.lines.get(editor.caretLine).length()));
        if (editor.caretLine == editor.selAnchorLine && editor.caretCol == editor.selAnchorCol) {
            editor.hasSelection = false;
        }
        editor.ensureCaretVisible();
        editor.refresh();
    }
}
