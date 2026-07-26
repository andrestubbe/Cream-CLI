package cream.cli.view.editor;

import cream.cli.Theme;
import fastterminal.FastStyle;
import fastterminal.FastTerminalScene;
import fasttui.component.Component;

import java.util.Arrays;

public class CodeLine extends Component {

    static final int EDITOR_SELECTION_BG = Theme.EDITOR_SELECTION_BG;
    static final int EDITOR_CARET_BG = Theme.EDITOR_CARET_BG;
    static final int EDITOR_CARET_FG = Theme.EDITOR_CARET_FG;

    private final Editor editor;
    private int docLine = -1;

    public CodeLine(int x, int y, Editor editor) {
        super(x, y, 1, 1);
        this.editor = editor;
    }

    public void setDocLine(int docLine) {
        this.docLine = docLine;
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!isVisible()) return;

        int editorWidth = editor.getWidth();
        int maxEditorCol = Math.max(0, editorWidth - x - 1); // Leave 1 column for vertical scrollbar

        if (docLine < 0 || docLine >= editor.buffer.lineCount()) {
            // Out of bounds / empty row below document end: clear full line width
            for (int col = 0; col < maxEditorCol; col++) {
                scene.writeCell(x + col, y, ' ', Theme.SYNTAX_DEFAULT, Theme.TRANSPARENT);
            }
            return;
        }

        String text = editor.buffer.getLine(docLine);
        int[] sel = editor.selectionMgr.selectionBounds();
        int[] fgColors = buildFgColors(text);
        byte[] fontStyles = buildFontStyles(text);

        boolean lineIsCurrent = (docLine == editor.caret.getLine());
        int len = text.length();
        int renderLen = Math.max(len, maxEditorCol);

        boolean isBlinkingOn = (System.currentTimeMillis() % 1000 < 500);
        Editor.HyperlinkRange link = editor.getHoveredHyperlink();

        int strIdx = 0;
        for (int col = 0; col < renderLen; col++) {
            boolean isCaret = (lineIsCurrent && strIdx == editor.caret.getCol() && isBlinkingOn);
            boolean isHyperlinked = (link != null && link.line() == docLine && strIdx >= link.startCol() && strIdx < link.endCol());

            int codePoint;
            int step = 1;
            if (strIdx < len) {
                codePoint = text.codePointAt(strIdx);
                step = Character.charCount(codePoint);
            } else {
                codePoint = ' ';
            }

            int bg = isCaret ? EDITOR_CARET_BG : getBg(sel, docLine, strIdx);
            int fg = isCaret ? EDITOR_CARET_FG : (isHyperlinked ? Theme.EDITOR_HYPERLINK_FG : ((strIdx < len) ? fgColors[strIdx] : Theme.SYNTAX_DEFAULT));

            int style = isHyperlinked ? FastStyle.UNDERLINE : ((strIdx < len && fontStyles != null) ? fontStyles[strIdx] : FastStyle.NONE);

            scene.writeCell(x + col, y, codePoint, fg, bg, style);

            strIdx += step;
        }

        // Caret beyond renderLen (if line is extremely long beyond viewport)
        if (lineIsCurrent && editor.caret.getCol() >= renderLen && isBlinkingOn) {
            scene.writeCell(x + editor.caret.getCol(), y, ' ', EDITOR_CARET_FG, EDITOR_CARET_BG);
        }

        // Selection on empty line
        if (sel != null && docLine >= sel[0] && docLine < sel[2] && len == 0) {
            scene.writeCell(x, y, ' ', Theme.SYNTAX_DEFAULT, EDITOR_SELECTION_BG);
        }
    }

    private int getBg(int[] sel, int docLine, int col) {
        if (sel == null) {
            return (docLine == editor.caret.getLine())
                    ? Theme.EDITOR_CURRENT_LINE_BG
                    : Theme.TRANSPARENT;
        }
        final int sLine = sel[0];
        final int sCol = sel[1];
        final int eLine = sel[2];
        final int eCol = sel[3];
        if (docLine == sLine && docLine == eLine) {
            return (col >= sCol && col < eCol)
                    ? EDITOR_SELECTION_BG
                    : (docLine == editor.caret.getLine()
                       ? Theme.EDITOR_CURRENT_LINE_BG
                       : Theme.TRANSPARENT);
        } else if (docLine == sLine) {
            return col >= sCol
                    ? EDITOR_SELECTION_BG
                    : (docLine == editor.caret.getLine()
                       ? Theme.EDITOR_CURRENT_LINE_BG
                       : Theme.TRANSPARENT);
        } else if (docLine == eLine) {
            return col < eCol ? EDITOR_SELECTION_BG
                    : (docLine == editor.caret.getLine()
                       ? Theme.EDITOR_CURRENT_LINE_BG
                       : Theme.TRANSPARENT);
        } else if (docLine > sLine && docLine < eLine) {
            return EDITOR_SELECTION_BG;
        }
        return (docLine == editor.caret.getLine())
                ? Theme.EDITOR_CURRENT_LINE_BG
                : Theme.TRANSPARENT;
    }

    private int[] buildFgColors(String text) {
        if (editor.getHighlighter() != null) {
            return editor.getHighlighter().highlight(text);
        }
        int[] fg = new int[text.length()];
        Arrays.fill(fg, Theme.SYNTAX_DEFAULT);
        return fg;
    }

    private byte[] buildFontStyles(String text) {
        if (editor.getHighlighter() != null) {
            return editor.getHighlighter().highlightStyles(text);
        }
        return new byte[text.length()];
    }
}
