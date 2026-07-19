package cream.cli;

import fasttui.component.ColorSet;

import java.util.Map;

public class Theme {

    // --- Hauptpalette (Retro‑Synthwave / Accent Colors) ---

    public static final int COLOR_1 = 0x4acdb8; // Mint Teal / Aqua Mint
    public static final int COLOR_2 = 0x465373; // Slate Indigo / Steel Blue
    public static final int COLOR_3 = 0xb93a50; // Crimson Berry / Raspberry Red
    public static final int COLOR_4 = 0xfbb97f; // Soft Peach / Apricot Glow
    public static final int COLOR_5 = 0xf0be75; // Warm Honey / Golden Sand
    public static final int COLOR_6 = 0xbb3b53; // Deep Rose / Wine Raspberry
    public static final int COLOR_7 = 0xb1d6a0; // Pastel Sage / Soft Green Tea
    public static final int COLOR_8 = 0xa186d1; // Lavender Mist / Soft Purple Glow
    public static final int COLOR_9 = 0xe40373; // Neon Magenta — greller Synthwave-Punch
    public static final int COLOR_10 = 0x00e0ff; // Electric Cyan — greller CREAM-Glow

// --- Dark Blue Night Mode Palette (Background / Depth Layers) ---

    public static final int COLOR_BLUE_1 = 0x0c0e10; // Blackened Navy / Deep Space Blue
    public static final int COLOR_BLUE_2 = 0x13141f; // Obsidian Blue / Midnight Indigo
    public static final int COLOR_BLUE_3 = 0x363d54; // Graphite Blue / Slate Night
    public static final int COLOR_BLUE_4 = 0x111727; // Dark Royal Blue / Abyss Blue
    public static final int COLOR_BLUE_5 = 0x171a29; // Ink Blue / Shadow Navy
    public static final int COLOR_BLUE_6 = 0x1e283a; // Storm Blue / Twilight Navy
    public static final int COLOR_BLUE_7 = 0x22293e; // Iron Blue / Dusk Indigo
    public static final int COLOR_BLUE_8 = 0x373e59; // Steel Indigo / Lunar Blue

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

    //    public static final int FILE_NORMAL_BACKGROUND = TRANSPARENT;
//    public static final int FILE_NORMAL_FOREGROUND = 0xCCCCCC;
//    public static final int FILE_SELECTION_BACKGROUND = 0x222222;
//    public static final int FILE_SELECTION_FOREGROUND = 0xFFFFFF;
//    public static final int FILE_FOOTER = 0x71717A;
//    public static final int FILE_INFORMATION = 0x71717A;
    //
    //    public static final int PATH_BACKGROUND = -1;
    //    public static final int PATH_FOREGROUND = 0xCCCCCC;
    //    public static final int SCROLL_BACKGROUND = TRANSPARENT;
    //    public static final int SCROLL_FOREGROUND = 0x666666;
    //    public static final int INPUT_BORDER = 0x222222;

    public static final int TRANSPARENT = -2;
    public static final int BACKGROUND = COLOR_BLUE_2;
    public static final int FOREGROUND = 0x95a8f1;
    public static final int BORDER = COLOR_BLUE_8;

    public static final int FILE_NORMAL_BACKGROUND = TRANSPARENT;
    public static final int FILE_NORMAL_FOREGROUND = 0xCCCCCC;
    public static final int FILE_SELECTION_BACKGROUND = 0x222222;
    public static final int FILE_SELECTION_FOREGROUND = 0xFFFFFF;
    public static final int FILE_FOOTER = 0x71717A;
    public static final int FILE_INFORMATION = 0x71717A;

    public static final int PATH_BACKGROUND = Theme.BACKGROUND;
    public static final int PATH_FOREGROUND = 0xCCCCCC;
    public static final int SCROLL_BACKGROUND = TRANSPARENT;
    public static final int SCROLL_FOREGROUND = 0x666666;
    public static final int EDITOR_NUMBERS = 0x444444;

    public static final int INPUT_COMMAND_BORDER_NORMAL_FOREGROUND = COLOR_BLUE_8;
    public static final int INPUT_COMMAND_BORDER_NORMAL_BACKGROUND = BACKGROUND;

    public static final int DROPDOWN_NORMAL_BACKGROUND = TRANSPARENT;
    public static final int DROPDOWN_NORMAL_FOREGROUND = 0x777777;
    public static final int DROPDOWN_HOVER_BACKGROUND = TRANSPARENT;
    public static final int DROPDOWN_HOVER_FOREGROUND = 0x999999;
    public static final int DROPDOWN_PRESSED_BACKGROUND = 0x222222;
    public static final int DROPDOWN_PRESSED_FOREGROUND = 0xFFFFFF;

    public static final ColorSet inputBackground = new ColorSet(-2, 0x24293e, 0x24293e, 0x24293e);
    public static final ColorSet inputIndicator = new ColorSet(-2, 0x95a8f1, 0xFFFFFF, 0x95a8f1);
    public static final ColorSet inputForeground = new ColorSet(0x95a8f1, 0xc3cdf7, 0xc3cdf7, 0x95a8f1);
    public static final ColorSet inputInformation = new ColorSet(0x596491, 0x596491, 0x596491, 0x596491);

    public static final ColorSet legendTypeBackground = new ColorSet(COLOR_7, COLOR_7, 0x95a8f1, 0x95a8f1);
    public static final ColorSet legendTypeForeground = new ColorSet(COLOR_BLUE_2, COLOR_BLUE_2, COLOR_BLUE_2, COLOR_BLUE_2);
    public static final ColorSet legendDropdownBackground = new ColorSet(-2, -2, -2, -2);
    public static final ColorSet legendDropdownForeground = new ColorSet(0x596491, 0x95a8f1, 0x596491, 0x95a8f1);
    public static final ColorSet legendInformationBackground = new ColorSet(-2, -2, 0x24293e, 0x24293e);
    public static final ColorSet legendInformationForeground = new ColorSet(COLOR_BLUE_8, 0xc3cdf7, 0xc3cdf7, 0x95a8f1);

}
