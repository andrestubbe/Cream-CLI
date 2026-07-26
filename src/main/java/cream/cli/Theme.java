package cream.cli;

import fasttui.component.BorderStyle;
import fasttui.component.ColorSet;

import java.util.Map;

public class Theme {

    public static final String ICON_OPEN_FOLDER = "📂 ";
    public static final String ICON_FOLDER = "📁 ";
    public static final String ICON_DOCUMENT = "📄 ";
    public static final Map<String, String> ICONS = Map.ofEntries(
            // Documents
            Map.entry("txt", "📝 "),
            Map.entry("md", "📝 "),
            Map.entry("pdf", "📕 "),
            Map.entry("doc", "📘 "),
            Map.entry("docx", "📘 "),
            Map.entry("ppt", "📙 "),
            Map.entry("pptx", "📙 "),
            Map.entry("xls", "📗 "),
            Map.entry("xlsx", "📗 "),

            // Images
            Map.entry("png", "🖼  "),
            Map.entry("jpg", "🖼  "),
            Map.entry("jpeg", "🖼  "),
            Map.entry("gif", "🖼  "),
            Map.entry("bmp", "🖼  "),
            Map.entry("svg", "🖼  "),

            // Audio & Video
            Map.entry("mp3", "🎵 "),
            Map.entry("wav", "🎵 "),
            Map.entry("flac", "🎵 "),
            Map.entry("ogg", "🎵 "),
            Map.entry("mp4", "🎥 "),
            Map.entry("mkv", "🎥 "),
            Map.entry("avi", "🎥 "),
            Map.entry("mov", "🎥 "),

            // Archives
            Map.entry("zip", "🗜 "),
            Map.entry("rar", "🗜 "),
            Map.entry("7z", "🗜 "),
            Map.entry("gz", "🗜 "),
            Map.entry("tar", "🗜 "),

            // Code & Development
            Map.entry("java", "☕ "),
            Map.entry("class", "☕ "),
            Map.entry("jar", "☕ "),
            Map.entry("kt", "🎯 "),
            Map.entry("py", "🐍 "),
            Map.entry("go", "🐹 "),
            Map.entry("rs", "🦀 "),
            Map.entry("c", "👾 "),
            Map.entry("cpp", "👾 "),
            Map.entry("h", "👾 "),
            Map.entry("js", "🟨 "),
            Map.entry("ts", "🟦 "),
            Map.entry("html", "🌐 "),
            Map.entry("css", "🎨 "),
            Map.entry("sh", "🐚 "),
            Map.entry("bat", "💻 "),
            Map.entry("cmd", "💻 "),
            Map.entry("ps1", "💻 "),
            Map.entry("exe", "⚙️  "),
            Map.entry("dll", "⚙️  "),

            // Data & Configs
            Map.entry("json", "{} "),
            Map.entry("xml", "<> "),
            Map.entry("yaml", "🔧 "),
            Map.entry("yml", "🔧 "),
            Map.entry("toml", "🔧 "),
            Map.entry("ini", "🔧 "),
            Map.entry("properties", "🔧 "),
            Map.entry("sql", "🗄️ "),
            Map.entry("db", "🗄️ "),
            Map.entry("csv", "📊 "),
            Map.entry("tsv", "📊 ")
    );

    public static final int TRANSPARENT = -2;
    public static final int BACKGROUND = 0x222436;
    public static final int FOREGROUND = 0x95a8f1;
    public static final int BORDER = 0x373e59;

    public static final int COLOR_PATH_FOREGROUND = 0xa1d5ff;
    public static final int COLOR_PATH_BACKGROUND = TRANSPARENT;
    public static final int COLOR_PATH_SEPARATOR = 0x596491;
    public static final int COLOR_PATH_FILE = 0x00e0ff;
    public static final int COLOR_PATH_FILE_HOVER = 0x80f0ff;
    public static final int COLOR_PATH_DIR_HOVER = 0xcce6ff;

    public static final ColorSet PATH_HEADER_BACKGROUND = new ColorSet(
            TRANSPARENT,
            TRANSPARENT,
            TRANSPARENT,
            TRANSPARENT
    );

    public static final ColorSet PATH_HEADER_DIRECTORY_SET = new ColorSet(
            COLOR_PATH_FOREGROUND,
            COLOR_PATH_DIR_HOVER,
            0xffffff,
            COLOR_PATH_FOREGROUND
    );

    public static final ColorSet PATH_HEADER_SEPARATOR_SET = new ColorSet(
            COLOR_PATH_SEPARATOR,
            COLOR_PATH_SEPARATOR,
            COLOR_PATH_SEPARATOR,
            COLOR_PATH_SEPARATOR
    );

    public static final ColorSet PATH_HEADER_FILE_SET = new ColorSet(
            COLOR_PATH_FILE,
            COLOR_PATH_FILE_HOVER,
            0xffffff,
            COLOR_PATH_FILE
    );

    public static final int FILES_FILE_SELECTION_BACKGROUND = 0x333a5b;
    public static final int FILES_FILE_SELECTION_FOREGROUND = 0xc0d4ff;
    public static final int FILES_FILE_HOVER_BACKGROUND = 0x333a5b;
    public static final int FILES_FILE_HOVER_FOREGROUND = 0xc0d4ff;
    public static final int FILES_INFO_NORMAL_BACKGROUND = 0x4a517c;
    public static final int FILES_INFO_SELECTED_BACKGROUND = 0x7ba3fb;
    public static final int FILES_FOOTER = 0x4a517c;

    public static final int FILES_COLUM_HEADER_BACKGROUND = 0x1e2030;
    public static final ColorSet FILES_COLUM_HEADER_BACKGROUND_SET = new ColorSet(
            TRANSPARENT,
            TRANSPARENT,
            TRANSPARENT,
            TRANSPARENT
    );
    public static final ColorSet FILES_COLUM_HEADER_FOREGROUND_SET = new ColorSet(
            0x4a517c,
            0x4a517c,
            0x4a517c,
            0x4a517c
    );

    // --- Heatmap Colors ---
    public static final int HEATMAP_YEAR_2005 = 0x5edacc;
    public static final int HEATMAP_YEAR_2019 = 0x6d75bb;

    // --- Settings Menu Colors ---
    public static final int SETTINGS_BORDER = 0x777777;
    public static final int SETTINGS_TEXT = 0x999999;

    // --- Editor & Syntax Colors ---
    public static final int EDITOR_SELECTION_BG = 0x3b4261;
    public static final int EDITOR_CARET_BG = 0xAEAFAD;
    public static final int EDITOR_CARET_FG = 0x1A1A2E;
    public static final int EDITOR_CURRENT_LINE_BG = 0x333a5b;
    public static final int EDITOR_NUMBERS = 0x3a4160;
    public static final int EDITOR_HYPERLINK_FG = 0x4EC9B0;

    public static final int SYNTAX_DEFAULT = 0xCCCCCC;
    public static final int SYNTAX_COMMENT = 0x565F89;
    public static final int SYNTAX_STRING = 0x9ECE6A;
    public static final int SYNTAX_NUMBER = 0xFF9E64;
    public static final int SYNTAX_IDENTIFIER = 0xC0CAF5;
    public static final int SYNTAX_KEYWORD = 0xBB9AF7;
    public static final int SYNTAX_TYPE = 0x2AC3DE;
    public static final int SYNTAX_PUNCTUATION = 0x7982A9;
    public static final int SYNTAX_BRACE = 0x8992A7;
    public static final int SYNTAX_PAREN = 0xA9B1D6;
    public static final int SYNTAX_OPERATOR = 0x89DDFF;
    public static final int SYNTAX_PARAMETER = 0xE0AF68;
    public static final int SYNTAX_LOCAL_VARIABLE = 0xC0CAF5;

    // XML Specific Syntax Colors
    public static final int SYNTAX_XML_TAG = 0x7AA2F7;
    public static final int SYNTAX_XML_ATTRIBUTE = 0xBB9AF7;
    public static final int SYNTAX_XML_VALUE = 0x9ECE6A;
    public static final int SYNTAX_XML_BRACKET = 0x89DDFF;
    public static final int SYNTAX_XML_COMMENT = 0x565F89;

    // Markdown Specific Syntax Colors
    public static final int SYNTAX_MD_HEADER = 0x89DDFF;
    public static final int SYNTAX_MD_BOLD = 0xFF9E64;
    public static final int SYNTAX_MD_ITALIC = 0xE0AF68;
    public static final int SYNTAX_MD_CODE = 0x9ECE6A;
    public static final int SYNTAX_MD_LINK = 0x7AA2F7;
    public static final int SYNTAX_MD_LIST = 0xF7768E;
    public static final int SYNTAX_MD_QUOTE = 0x565F89;

    // JSON Specific Syntax Colors
    public static final int SYNTAX_JSON_KEY = 0x7AA2F7;
    public static final int SYNTAX_JSON_STRING = 0x9ECE6A;
    public static final int SYNTAX_JSON_NUMBER = 0xFF9E64;
    public static final int SYNTAX_JSON_KEYWORD = 0xBB9AF7;
    public static final int SYNTAX_JSON_BRACKET = 0x89DDFF;

    // --- Input Component Colors ---
    public static final int OMNIBOX_CARET_BG = 0xAEAFAD;
    public static final int OMNIBOX_CARET_FG = 0x1A1A2E;

    public static final int OMNIBOX_NORMAL_BACKGROUND = TRANSPARENT;
    public static final int OMNIBOX_NORMAL_BORDER = 0x3b4261;
    public static final int OMNIBOX_HOVERED_BACKGROUND = TRANSPARENT;
    public static final int OMNIBOX_HOVERED_BORDER = 0x616ba3;
    public static final int OMNIBOX_FOCUSSED_BACKGROUND = TRANSPARENT;
    public static final int OMNIBOX_FOCUSSED_BORDER = 0x616ba3;

    public static final int OMNIBOX_INFORMATION_BACKGROUND = TRANSPARENT;
    public static final int OMNIBOX_INFORMATION_FOREGROUND = 0x373e59;

    public static final ColorSet OMNIBOX_TEXT_BACKGROUND_SET = new ColorSet(
            TRANSPARENT, // normal
            TRANSPARENT, // hover
            TRANSPARENT, // focus
            TRANSPARENT  // press
    );

    public static final ColorSet OMNIBOX_TEXT_FOREGROUND_SET = new ColorSet(
            0x95a8f1, // normal (0x95a8f1)
            0xa1d5ff,   // hover
            0xa1d5ff,   // focus
            0xa1d5ff  // press
    );

    public static final ColorSet OMNIBOX_PLACEHOLDER_FOREGROUND_SET = new ColorSet(
            0x3b4261, // normal (dimmed teal-blue)
            0x616ba3, // hover (brighter soft blue)
            0x616ba3, // focus (electric cyan-blue)
            0x616ba3  // press
    );

    public static final ColorSet OMNIBOX_MODE_BACKGROUND_SET = new ColorSet(
            0xb1d6a0,
            0xb1d6a0,
            0x95a8f1,
            0x95a8f1
    );

    public static final ColorSet OMNIBOX_MODE_FOREGROUND_SET = new ColorSet(
            0x13141f,
            0x13141f,
            0x13141f,
            0x13141f
    );

    public static final ColorSet OMNIBOX_BUTTON_BACKGROUND_SET = new ColorSet(
            TRANSPARENT,
            TRANSPARENT,
            TRANSPARENT,
            TRANSPARENT
    );

    public static final ColorSet OMNIBOX_BUTTON_FOREGROUND_SET = new ColorSet(
            0x596491,
            0x95a8f1,
            0x596491,
            0x95a8f1
    );

    public static final ColorSet SCROLLBAR_BACKGROUND_SET = new ColorSet(
            TRANSPARENT,
            0x2d3048,
            0x2d3048,
            0x2d3048
    );

    public static final ColorSet SCROLLBAR_FOREGROUND_SET = new ColorSet(
            0x3b4261,
            0xa1d5ff,
            0xa1d5ff,
            0xa1d5ff
    );

    public static final BorderStyle POPUP_BORDER_STYLE = BorderStyle.ROUNDED;
    public static final int POPUP_BORDER = 0x606a9c;
    public static final int POPUP_BACKGROUND_NORMAL = 0x26283d;
    public static final int POPUP_FOREGROUND_NORMAL = 0xABB2BF;
    public static final int POPUP_BACKGROUND_SELECTION = 0x2C313A;
    public static final int POPUP_FOREGROUND_SELECTION = 0x61AFEF;
    public static final int POPUP_MUTED = 0x5C6370;

    public static final ColorSet POPUP_BACKGROUND_ROW_SET = new ColorSet(
            -2,
            0x30334f,
            0x30334f,
            0x30334f
    );

    public static final ColorSet POPUP_FOREGROUND_INDICATOR_SET = new ColorSet(
            -2,
            0x95a8f1,
            0xFFFFFF,
            0x95a8f1
    );

    public static final ColorSet POPUP_FOREGROUND_NORMAL_SET = new ColorSet(
            0x95a8f1,
            0xc3cdf7,
            0xc3cdf7,
            0x95a8f1
    );

    public static final ColorSet POPUP_FOREGROUND_INFORMATION_SET = new ColorSet(
            0x596491,
            0x596491,
            0x596491,
            0x596491
    );

    public static final int POPUP_FOREGROUND_SOURCE_JDK = 0xE5C07B;
    public static final int POPUP_FOREGROUND_SOURCE_SRC = 0x98C379;
    public static final int POPUP_FOREGROUND_SOURCE_MAVEN = 0x61AFEF;
    public static final int POPUP_FOREGROUND_SOURCE_JAR = 0xC678DD;
    public static final int POPUP_FOREGROUND_SOURCE_KEYWORD = 0xE06C75;
    public static final int POPUP_FOREGROUND_SOURCE_SNIPPET = 0x56B6C2;
}
