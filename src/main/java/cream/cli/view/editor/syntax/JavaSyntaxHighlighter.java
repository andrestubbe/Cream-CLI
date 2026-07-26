package cream.cli.view.editor.syntax;

import cream.cli.Theme;
import fastterminal.FastStyle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaSyntaxHighlighter implements SyntaxHighlighter {

    private boolean inBlockComment = false;
    private final Set<String> activeParams = new HashSet<>();
    private final Set<String> activeVars = new HashSet<>();
    private final Set<String> activeFields = new HashSet<>();
    private int braceDepth = 0;
    private byte[] lastStyles;

    @Override
    public byte[] highlightStyles(String text) {
        return lastStyles != null && lastStyles.length == text.length() ? lastStyles : new byte[text.length()];
    }

    @Override
    public int[] highlight(String text) {
        int len = text.length();
        int[] fg = new int[len];
        byte[] styles = new byte[len];
        this.lastStyles = styles;
        Arrays.fill(fg, Theme.SYNTAX_DEFAULT);

        String trimmed = text.trim();

        // 1. Javadoc / Multiline comment continuation lines (starting with /*, /**, *, */)
        if (trimmed.startsWith("/*") || trimmed.startsWith("/**") || trimmed.startsWith("*") || trimmed.startsWith("*/")) {
            Arrays.fill(fg, 0, len, Theme.SYNTAX_COMMENT);
            return fg;
        }

        boolean wasType = false;
        boolean wasVisibility = false;
        int parenDepth = 0;

        int i = 0;
        while (i < len) {
            char c = text.charAt(i);

            // Block comment continuation state
            if (inBlockComment) {
                int start = i;
                while (i < len) {
                    if (text.charAt(i) == '*' && i + 1 < len && text.charAt(i + 1) == '/') {
                        i += 2;
                        inBlockComment = false;
                        break;
                    }
                    i++;
                }
                Arrays.fill(fg, start, Math.min(i, len), Theme.SYNTAX_COMMENT);
                continue;
            }

            // Single line comment //
            if (c == '/' && i + 1 < len && text.charAt(i + 1) == '/') {
                Arrays.fill(fg, i, len, Theme.SYNTAX_COMMENT);
                break;
            }

            // Start of block comment /* or /**
            if (c == '/' && i + 1 < len && text.charAt(i + 1) == '*') {
                int start = i;
                i += 2;
                boolean closedOnSameLine = false;
                while (i < len) {
                    if (text.charAt(i) == '*' && i + 1 < len && text.charAt(i + 1) == '/') {
                        i += 2;
                        closedOnSameLine = true;
                        break;
                    }
                    i++;
                }
                if (!closedOnSameLine) {
                    inBlockComment = true;
                }
                Arrays.fill(fg, start, Math.min(i, len), Theme.SYNTAX_COMMENT);
                continue;
            }

            // Java Annotations (@Override, @Deprecated, @Test, @link, etc.)
            if (c == '@' && i + 1 < len && Character.isJavaIdentifierStart(text.charAt(i + 1))) {
                int start = i;
                i++;
                while (i < len && Character.isJavaIdentifierPart(text.charAt(i))) {
                    i++;
                }
                Arrays.fill(fg, start, i, Theme.SYNTAX_KEYWORD);
                wasType = false;
                continue;
            }

            // String literal "..."
            if (c == '"') {
                int start = i++;
                while (i < len && text.charAt(i) != '"') {
                    if (text.charAt(i) == '\\' && i + 1 < len) i += 2;
                    else i++;
                }
                if (i < len) i++;
                Arrays.fill(fg, start, Math.min(i, len), Theme.SYNTAX_STRING);
                wasType = false;
                continue;
            }

            // Character literal '...'
            if (c == '\'') {
                int start = i++;
                while (i < len && text.charAt(i) != '\'') {
                    if (text.charAt(i) == '\\' && i + 1 < len) i += 2;
                    else i++;
                }
                if (i < len) i++;
                Arrays.fill(fg, start, Math.min(i, len), Theme.SYNTAX_STRING);
                wasType = false;
                continue;
            }

            // Number literal
            if (Character.isDigit(c)) {
                int start = i;
                while (i < len) {
                    char ch = text.charAt(i);
                    if (Character.isDigit(ch) || ch == 'x' || ch == 'X'
                            || ch == 'L' || ch == 'l' || ch == 'f' || ch == 'F'
                            || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) {
                        i++;
                    } else break;
                }
                Arrays.fill(fg, start, i, Theme.SYNTAX_NUMBER);
                wasType = false;
                continue;
            }

            // Identifier / keyword / type / parameter / variable / method call
            if (Character.isJavaIdentifierStart(c)) {
                int start = i;
                while (i < len && Character.isJavaIdentifierPart(text.charAt(i))) i++;
                String word = text.substring(start, i);

                int nextIdx = i;
                while (nextIdx < len && Character.isWhitespace(text.charAt(nextIdx))) nextIdx++;
                boolean isMethodCall = (nextIdx < len && text.charAt(nextIdx) == '(') || (start >= 2 && text.substring(start - 2, start).equals("::"));
                boolean isThisAccess = (start >= 5 && text.substring(start - 5, start).equals("this.")) || (start >= 6 && text.substring(start - 6, start).equals("super."));

                int color = Theme.SYNTAX_IDENTIFIER;
                byte style = FastStyle.NONE;

                if (isKeyword(word)) {
                    color = Theme.SYNTAX_KEYWORD;
                    wasType = isPrimitiveTypeKeyword(word);
                    if (isVisibilityKeyword(word)) wasVisibility = true;
                } else if (isThisAccess) {
                    color = Theme.SYNTAX_FIELD;
                    activeFields.add(word);
                    wasType = false;
                } else if (isConstantName(word)) {
                    color = Theme.SYNTAX_CONSTANT;
                    style = FastStyle.ITALIC;
                    wasType = false;
                } else if (isMethodCall && !isControlFlowKeyword(word)) {
                    color = Theme.SYNTAX_METHOD;
                    wasType = false;
                } else if (isType(word)) {
                    color = Theme.SYNTAX_TYPE;
                    wasType = true;
                } else if (wasType) {
                    if (parenDepth > 0) {
                        color = Theme.SYNTAX_PARAMETER;
                        activeParams.add(word);
                    } else if (braceDepth <= 1 || wasVisibility) {
                        color = Theme.SYNTAX_FIELD;
                        activeFields.add(word);
                    } else {
                        color = Theme.SYNTAX_LOCAL_VARIABLE;
                        activeVars.add(word);
                    }
                    wasType = false;
                } else if (activeParams.contains(word)) {
                    color = Theme.SYNTAX_PARAMETER;
                    wasType = false;
                } else if (activeVars.contains(word)) {
                    color = Theme.SYNTAX_LOCAL_VARIABLE;
                    wasType = false;
                } else if (activeFields.contains(word)) {
                    color = Theme.SYNTAX_FIELD;
                    wasType = false;
                } else {
                    wasType = false;
                }

                Arrays.fill(fg, start, i, color);
                if (style != FastStyle.NONE) {
                    Arrays.fill(styles, start, i, style);
                }
                continue;
            }

            // Operators / punctuation / braces / parens
            switch (c) {
                case '{' -> {
                    fg[i] = Theme.SYNTAX_BRACE;
                    braceDepth++;
                    wasType = false;
                }
                case '}' -> {
                    fg[i] = Theme.SYNTAX_BRACE;
                    if (braceDepth > 0) braceDepth--;
                    if (braceDepth <= 1) {
                        // Scope closed: clear method parameters & local variables
                        activeParams.clear();
                        activeVars.clear();
                    }
                    wasType = false;
                }
                case '(' -> {
                    fg[i] = Theme.SYNTAX_PAREN;
                    parenDepth++;
                }
                case ')' -> {
                    fg[i] = Theme.SYNTAX_PAREN;
                    if (parenDepth > 0) parenDepth--;
                    wasType = false;
                }
                case '[', ']' -> {
                    fg[i] = Theme.SYNTAX_PAREN;
                    // Intentionally keep wasType active for array declarations like String[] a
                }
                case ';', ',', '.' -> {
                    fg[i] = Theme.SYNTAX_PUNCTUATION;
                    if (c == ';') wasType = false;
                }
                case '+', '-', '*', '/', '%', '=', '!', '<', '>', '&', '|', '^', '?', ':', '~' -> {
                    fg[i] = Theme.SYNTAX_OPERATOR;
                    if (c == '=') wasType = false;
                }
                default -> {
                    if (!Character.isWhitespace(c)) {
                        fg[i] = Theme.SYNTAX_PUNCTUATION;
                        wasType = false;
                    }
                }
            }
            i++;
        }
        return fg;
    }

    private static boolean isType(String w) {
        if (w.isEmpty()) return false;
        return Character.isUpperCase(w.charAt(0)) || isPrimitiveTypeKeyword(w);
    }

    private static boolean isConstantName(String w) {
        if (w.isEmpty() || isKeyword(w)) return false;
        boolean hasUpper = false;
        for (int i = 0; i < w.length(); i++) {
            char ch = w.charAt(i);
            if (Character.isLowerCase(ch)) return false;
            if (Character.isUpperCase(ch)) hasUpper = true;
        }
        return hasUpper;
    }

    private static boolean isPrimitiveTypeKeyword(String w) {
        return switch (w) {
            case "int", "long", "double", "float", "boolean", "char", "byte", "short", "void", "var" -> true;
            default -> false;
        };
    }

    private static boolean isVisibilityKeyword(String w) {
        return switch (w) {
            case "private", "public", "protected" -> true;
            default -> false;
        };
    }

    private static boolean isControlFlowKeyword(String w) {
        return switch (w) {
            case "if", "while", "for", "switch", "catch", "synchronized", "new" -> true;
            default -> false;
        };
    }

    private static boolean isKeyword(String w) {
        return switch (w) {
            case "package", "import", "public", "private", "protected", "final", "static",
                    "class", "interface", "enum", "new", "for", "while", "if", "else",
                    "return", "switch", "case", "default", "this", "super", "extends",
                    "implements", "int", "long", "double", "float", "boolean", "char",
                    "byte", "short", "void", "null", "true", "false", "var" -> true;
            default -> false;
        };
    }
}
