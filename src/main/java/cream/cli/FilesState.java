package cream.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FilesState {

    public File currentDirectory = new File(".").getAbsoluteFile().getParentFile();
    public final List<File> files = new ArrayList<>();
    public int selectedIndex = -1;
    public int scrollOffset = 0;

}
