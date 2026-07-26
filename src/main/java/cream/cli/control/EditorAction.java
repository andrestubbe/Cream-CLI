package cream.cli.control;

/**
 * Logical editor actions, independent of any physical key binding.
 * <p>
 * New actions can be added here without touching the key-dispatch logic.
 * To change which key triggers an action, modify {@link KeyMap} instead.
 */
public enum EditorAction {
    // Editing
    UNDO,
    REDO,
    COPY,
    CUT,
    PASTE,
    SELECT_ALL,
    DELETE_LINE,
    DUPLICATE_LINE,
    MOVE_LINE_UP,
    MOVE_LINE_DOWN,
    TOGGLE_COMMENT,

    // Navigation
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_WORD_LEFT,
    MOVE_WORD_RIGHT,
    MOVE_HOME,
    MOVE_END,
    SCROLL_PAGE_UP,
    SCROLL_PAGE_DOWN,

    // Actions
    NEWLINE,
    BACKSPACE,
    DELETE,
    TAB,
    SAVE,
    CLOSE_OR_EXIT
}
