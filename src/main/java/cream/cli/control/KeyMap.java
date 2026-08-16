package cream.cli.control;

import cream.cli.control.handler.EditorAction;
import fastkeyboard.Keys;

import java.util.HashMap;
import java.util.Map;

public class KeyMap {

    private final Map<KeyCombo, EditorAction> bindings = new HashMap<>();

    public EditorAction resolve(int vKey, boolean ctrl, boolean alt, boolean shift) {
        return bindings.get(KeyCombo.of(vKey, ctrl, alt, shift));
    }

    public KeyMap bind(KeyCombo combo, EditorAction action) {
        bindings.put(combo, action);
        return this;
    }

    public KeyMap unbind(KeyCombo combo) {
        bindings.remove(combo);
        return this;
    }

    public static KeyMap createDefaultPreset() {
        KeyMap m = new KeyMap();

        // Undo / Redo
        m.bind(KeyCombo.ctrl(Keys.Z), EditorAction.UNDO);
        m.bind(KeyCombo.ctrlShift(Keys.Z), EditorAction.REDO);
        m.bind(KeyCombo.ctrl(Keys.Y), EditorAction.REDO);

        // Clipboard
        m.bind(KeyCombo.ctrl(Keys.C), EditorAction.COPY);
        m.bind(KeyCombo.ctrl(Keys.X), EditorAction.CUT);
        m.bind(KeyCombo.ctrl(Keys.V), EditorAction.PASTE);

        // Selection & File
        m.bind(KeyCombo.ctrl(Keys.A), EditorAction.SELECT_ALL);
        m.bind(KeyCombo.ctrl(Keys.S), EditorAction.SAVE);

        // Navigation
        m.bind(KeyCombo.plain(Keys.UP), EditorAction.MOVE_UP);
        m.bind(KeyCombo.plain(Keys.DOWN), EditorAction.MOVE_DOWN);
        m.bind(KeyCombo.plain(Keys.LEFT), EditorAction.MOVE_LEFT);
        m.bind(KeyCombo.plain(Keys.RIGHT), EditorAction.MOVE_RIGHT);
        m.bind(KeyCombo.plain(Keys.HOME), EditorAction.MOVE_HOME);
        m.bind(KeyCombo.plain(Keys.END), EditorAction.MOVE_END);
        m.bind(KeyCombo.plain(Keys.PAGE_UP), EditorAction.SCROLL_PAGE_UP);
        m.bind(KeyCombo.plain(Keys.PAGE_DOWN), EditorAction.SCROLL_PAGE_DOWN);

        // Navigation with Shift (selection extend)
        m.bind(KeyCombo.of(Keys.UP, false, false, true), EditorAction.MOVE_UP);
        m.bind(KeyCombo.of(Keys.DOWN, false, false, true), EditorAction.MOVE_DOWN);
        m.bind(KeyCombo.of(Keys.LEFT, false, false, true), EditorAction.MOVE_LEFT);
        m.bind(KeyCombo.of(Keys.RIGHT, false, false, true), EditorAction.MOVE_RIGHT);
        m.bind(KeyCombo.of(Keys.HOME, false, false, true), EditorAction.MOVE_HOME);
        m.bind(KeyCombo.of(Keys.END, false, false, true), EditorAction.MOVE_END);

        // Word-by-word navigation (Ctrl) and selection (Ctrl + Shift)
        m.bind(KeyCombo.ctrl(Keys.LEFT), EditorAction.MOVE_WORD_LEFT);
        m.bind(KeyCombo.ctrl(Keys.RIGHT), EditorAction.MOVE_WORD_RIGHT);
        m.bind(KeyCombo.ctrlShift(Keys.LEFT), EditorAction.MOVE_WORD_LEFT);
        m.bind(KeyCombo.ctrlShift(Keys.RIGHT), EditorAction.MOVE_WORD_RIGHT);

        // View Scrolling (Ctrl + Up/Down)
        m.bind(KeyCombo.ctrl(Keys.UP), EditorAction.SCROLL_UP);
        m.bind(KeyCombo.ctrl(Keys.DOWN), EditorAction.SCROLL_DOWN);

        // Editing & Comments
        m.bind(KeyCombo.ctrl(Keys.KEY_7), EditorAction.TOGGLE_COMMENT);
        m.bind(KeyCombo.ctrl(Keys.OEM_2), EditorAction.TOGGLE_COMMENT);
        m.bind(KeyCombo.ctrl(Keys.DIVIDE), EditorAction.TOGGLE_COMMENT);
        m.bind(KeyCombo.plain(Keys.ENTER), EditorAction.NEWLINE);
        m.bind(KeyCombo.plain(Keys.BACKSPACE), EditorAction.BACKSPACE);
        m.bind(KeyCombo.plain(Keys.DELETE), EditorAction.DELETE);
        m.bind(KeyCombo.plain(Keys.TAB), EditorAction.TAB);
        m.bind(KeyCombo.plain(Keys.ESC), EditorAction.CLOSE_OR_EXIT);

        // Content Assist (Eclipse: CTRL+Space)
        m.bind(KeyCombo.ctrl(Keys.SPACE), EditorAction.TRIGGER_AUTOCOMPLETE);

        return m;
    }

    public static KeyMap createEclipsePreset() {
        KeyMap m = createDefaultPreset();

        m.bind(KeyCombo.ctrl(Keys.D), EditorAction.DELETE_LINE);
        m.bind(KeyCombo.alt(Keys.UP), EditorAction.MOVE_LINE_UP);
        m.bind(KeyCombo.alt(Keys.DOWN), EditorAction.MOVE_LINE_DOWN);
        m.bind(KeyCombo.ctrlAlt(Keys.DOWN), EditorAction.DUPLICATE_LINE);

        return m;
    }
}
