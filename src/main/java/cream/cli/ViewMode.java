package cream.cli;

public enum ViewMode {
    /** Only the file explorer is visible. */
    EXPLORER,

    /** Tree view (left) and editor (right) side-by-side. */
    TREE,

    /** Explorer (left) and editor (right) side-by-side. */
    SPLIT,

    /** Only the editor is visible (full-screen). */
    EDITOR
}
