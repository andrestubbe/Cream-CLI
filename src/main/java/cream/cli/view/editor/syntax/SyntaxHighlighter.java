package cream.cli.view.editor.syntax;

public interface SyntaxHighlighter {
    int[] highlight(String text);

    default byte[] highlightStyles(String text) {
        return new byte[text.length()];
    }
}
