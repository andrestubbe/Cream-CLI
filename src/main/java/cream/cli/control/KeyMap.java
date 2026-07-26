package cream.cli.control;

import fastkeyboard.Keys;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps physical {@link KeyCombo}s to logical {@link EditorAction}s.
 * <p>
 * Presets are provided as static factory methods. A custom map can override
 * individual bindings on top of any preset using {@link #bind}.
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * KeyMap keyMap = KeyMap.createDefaultPreset();
 * // or:
 * KeyMap keyMap = KeyMap.createEclipsePreset();
 * }</pre>
 */
public class KeyMap {

    private final Map<KeyCombo, EditorAction> bindings = new HashMap<>();

    /** Resolves a key combo to its action, or null if no binding exists. */
    public EditorAction resolve(int vKey, boolean ctrl, boolean alt, boolean shift) {
        return bindings.get(KeyCombo.of(vKey, ctrl, alt, shift));
    }

    /** Adds or overrides a single binding in this map. */
    public KeyMap bind(KeyCombo combo, EditorAction action) {
        bindings.put(combo, action);
        return this;
    }

    /** Removes a binding from this map. */
    public KeyMap unbind(KeyCombo combo) {
        bindings.remove(combo);
        return this;
    }

    // -------------------------------------------------------------------------
    // Presets
    // -------------------------------------------------------------------------

    /**
     * Default preset — preserves all keybindings as they were before the KeyMap was introduced.
     * Ctrl+Z / Ctrl+Y for Undo/Redo, standard navigation keys, etc.
     */
    public static KeyMap createDefaultPreset() {
        KeyMap m = new KeyMap();

        // Undo / Redo
        m.bind(KeyCombo.ctrl(Keys.Z),       EditorAction.UNDO);
        m.bind(KeyCombo.ctrlShift(Keys.Z),  EditorAction.REDO);
        m.bind(KeyCombo.ctrl(Keys.Y),       EditorAction.REDO);

        // Clipboard
        m.bind(KeyCombo.ctrl(Keys.C),       EditorAction.COPY);
        m.bind(KeyCombo.ctrl(Keys.X),       EditorAction.CUT);
        m.bind(KeyCombo.ctrl(Keys.V),       EditorAction.PASTE);

        // Selection & File
        m.bind(KeyCombo.ctrl(Keys.A),       EditorAction.SELECT_ALL);
        m.bind(KeyCombo.ctrl(Keys.S),       EditorAction.SAVE);

        // Navigation
        m.bind(KeyCombo.plain(Keys.UP),           EditorAction.MOVE_UP);
        m.bind(KeyCombo.plain(Keys.DOWN),         EditorAction.MOVE_DOWN);
        m.bind(KeyCombo.plain(Keys.LEFT),         EditorAction.MOVE_LEFT);
        m.bind(KeyCombo.plain(Keys.RIGHT),        EditorAction.MOVE_RIGHT);
        m.bind(KeyCombo.plain(Keys.HOME),         EditorAction.MOVE_HOME);
        m.bind(KeyCombo.plain(Keys.END),          EditorAction.MOVE_END);
        m.bind(KeyCombo.plain(Keys.PAGE_UP),      EditorAction.SCROLL_PAGE_UP);
        m.bind(KeyCombo.plain(Keys.PAGE_DOWN),    EditorAction.SCROLL_PAGE_DOWN);

        // Navigation with Shift (selection extend)
        m.bind(KeyCombo.of(Keys.UP,    false, false, true), EditorAction.MOVE_UP);
        m.bind(KeyCombo.of(Keys.DOWN,  false, false, true), EditorAction.MOVE_DOWN);
        m.bind(KeyCombo.of(Keys.LEFT,  false, false, true), EditorAction.MOVE_LEFT);
        m.bind(KeyCombo.of(Keys.RIGHT, false, false, true), EditorAction.MOVE_RIGHT);
        m.bind(KeyCombo.of(Keys.HOME,  false, false, true), EditorAction.MOVE_HOME);
        m.bind(KeyCombo.of(Keys.END,   false, false, true), EditorAction.MOVE_END);

        // Word-by-word navigation (Ctrl) and selection (Ctrl + Shift)
        m.bind(KeyCombo.ctrl(Keys.LEFT), EditorAction.MOVE_WORD_LEFT);
        m.bind(KeyCombo.ctrl(Keys.RIGHT), EditorAction.MOVE_WORD_RIGHT);
        m.bind(KeyCombo.ctrlShift(Keys.LEFT), EditorAction.MOVE_WORD_LEFT);
        m.bind(KeyCombo.ctrlShift(Keys.RIGHT), EditorAction.MOVE_WORD_RIGHT);

        // Editing & Comments
        m.bind(KeyCombo.ctrl(Keys.KEY_7),      EditorAction.TOGGLE_COMMENT); // Ctrl + 7 (German layout /)
        m.bind(KeyCombo.ctrl(Keys.OEM_2),      EditorAction.TOGGLE_COMMENT); // Ctrl + /
        m.bind(KeyCombo.ctrl(Keys.DIVIDE),     EditorAction.TOGGLE_COMMENT); // Ctrl + Numpad /
        m.bind(KeyCombo.plain(Keys.ENTER),     EditorAction.NEWLINE);
        m.bind(KeyCombo.plain(Keys.BACKSPACE), EditorAction.BACKSPACE);
        m.bind(KeyCombo.plain(Keys.DELETE),    EditorAction.DELETE);
        m.bind(KeyCombo.plain(Keys.TAB),       EditorAction.TAB);
        m.bind(KeyCombo.plain(Keys.ESC),       EditorAction.CLOSE_OR_EXIT);

        return m;
    }

    /**
     * Eclipse preset — based on the default, with additional Eclipse-style bindings layered on top.
     * <ul>
     *   <li>Ctrl+D — Delete current line (Eclipse)</li>
     *   <li>Alt+Up — Move line up (Eclipse)</li>
     *   <li>Alt+Down — Move line down (Eclipse)</li>
     *   <li>Ctrl+Alt+Down — Duplicate line (Eclipse)</li>
     * </ul>
     */
    public static KeyMap createEclipsePreset() {
        KeyMap m = createDefaultPreset();

        m.bind(KeyCombo.ctrl(Keys.D),             EditorAction.DELETE_LINE);
        m.bind(KeyCombo.alt(Keys.UP),             EditorAction.MOVE_LINE_UP);
        m.bind(KeyCombo.alt(Keys.DOWN),           EditorAction.MOVE_LINE_DOWN);
        m.bind(KeyCombo.ctrlAlt(Keys.DOWN),       EditorAction.DUPLICATE_LINE);

        return m;
    }
}
