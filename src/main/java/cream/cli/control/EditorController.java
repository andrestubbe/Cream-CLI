package cream.cli.control;

import cream.cli.Client;
import cream.cli.view.editor.Editor;
import cream.cli.view.editor.autocomplete.PopupAutocomplete;
import cream.cli.view.ui.Dialog;

/**
 * Translates physical key input into logical {@link EditorAction}s via the active {@link KeyMap},
 * then executes those actions against the Editor.
 * <p>
 * To change key bindings, replace or modify the {@code keyMap} field.
 * Presets are available via {@link KeyMap#createDefaultPreset()} and {@link KeyMap#createEclipsePreset()}.
 */
public class EditorController implements FocusTarget {

    private final Editor editor;
    private final Client client;

    /** The active key map. Default set to Eclipse preset. */
    public KeyMap keyMap = KeyMap.createEclipsePreset();

    public EditorController(Editor editor, Client client) {
        this.editor = editor;
        this.client = client;
    }

    @Override
    public boolean isFocused() {
        return editor != null && editor.isVisible();
    }

    @Override
    public boolean handleKeyInput(int vKey, String ch, boolean pressed) {
        if (!isFocused() || !pressed) return false;

        // Let the autocomplete popup consume input first if it's active
        PopupAutocomplete popup = editor.autocompleteMgr.getAutocompletePopup();
        if (popup != null && popup.isFocused()) {
            if (popup.handleKeyInput(vKey, ch, pressed)) {
                return true;
            }
        }

        FocusManager fm = client.getViewManager() != null ? client.getViewManager().focusManager : null;
        boolean shift = fm != null && fm.isShiftHeld();
        boolean ctrl  = fm != null && fm.isCtrlHeld();
        boolean alt   = fm != null && fm.isAltHeld();

        // Resolve logical action from key map
        EditorAction action = keyMap.resolve(vKey, ctrl, alt, shift);

        if (action != null) {
            return executeAction(action, shift, popup);
        }

        // Fallback: printable character input (only when no modifier is held)
        if (!ctrl && !alt && ch != null && !ch.isEmpty()) {
            char c = ch.charAt(0);
            if (c >= 32) {
                editor.docOps.insertChar(c);
                if (Character.isJavaIdentifierPart(c) || c == '.') {
                    editor.autocompleteMgr.triggerAutocomplete();
                } else {
                    if (popup != null) popup.hide();
                }
                return true;
            }
        }

        return false;
    }

    private boolean executeAction(EditorAction action, boolean shift, PopupAutocomplete popup) {
        switch (action) {
            // --- Undo / Redo ---
            case UNDO            -> editor.docOps.undo();
            case REDO            -> editor.docOps.redo();

            // --- Clipboard ---
            case COPY            -> editor.docOps.copy();
            case CUT             -> editor.docOps.cut();
            case PASTE           -> editor.docOps.paste();

            // --- Selection ---
            case SELECT_ALL      -> editor.selectionMgr.selectAll();

            // --- File ---
            case SAVE            -> { editor.fileManager.saveFile(); client.repaint(); }

            // --- Navigation ---
            case MOVE_UP         -> editor.caretNav.moveUp(shift);
            case MOVE_DOWN       -> editor.caretNav.moveDown(shift);
            case MOVE_LEFT       -> editor.caretNav.moveLeft(shift);
            case MOVE_RIGHT      -> editor.caretNav.moveRight(shift);
            case MOVE_WORD_LEFT  -> editor.caretNav.moveWordLeft(shift);
            case MOVE_WORD_RIGHT -> editor.caretNav.moveWordRight(shift);
            case MOVE_HOME       -> editor.caretNav.moveHome(shift);
            case MOVE_END        -> editor.caretNav.moveEnd(shift);
            case SCROLL_PAGE_UP  -> editor.scroll(-editor.visibleLines);
            case SCROLL_PAGE_DOWN-> editor.scroll(editor.visibleLines);

            // --- Editing ---
            case NEWLINE         -> editor.docOps.insertNewline();
            case BACKSPACE       -> { editor.docOps.backspace(); editor.autocompleteMgr.triggerAutocomplete(); }
            case DELETE          -> editor.docOps.delete();
            case TAB             -> { for (int i = 0; i < 4; i++) editor.docOps.insertChar(' '); }

            // --- Line operations (Eclipse-style, stubs ready for implementation) ---
            case TOGGLE_COMMENT  -> editor.docOps.toggleComment();
            case DELETE_LINE     -> editor.docOps.deleteCurrentLine();
            case MOVE_LINE_UP    -> editor.docOps.moveLineUp();
            case MOVE_LINE_DOWN  -> editor.docOps.moveLineDown();
            case DUPLICATE_LINE  -> editor.docOps.duplicateCurrentLine();

            // --- Close / Exit ---
            case CLOSE_OR_EXIT   -> {
                if (editor.fileManager.isDirty()) {
                    client.dialog.setFileName(
                        editor.fileManager.getCurrentFile() != null
                            ? editor.fileManager.getCurrentFile().getName() : null);
                    client.dialog.setOptionListener(option -> {
                        client.dialog.setVisible(false);
                        if (option == Dialog.Option.SAVE) {
                            editor.fileManager.saveFile();
                            client.saveState();
                            client.showExplorer();
                        } else if (option == Dialog.Option.DONT_SAVE) {
                            editor.fileManager.setDirty(false);
                            client.saveState();
                            client.showExplorer();
                        }
                        client.repaint();
                    });
                    client.dialog.setVisible(true);
                } else {
                    client.saveState();
                    client.showExplorer();
                }
            }
        }
        return true;
    }
}
