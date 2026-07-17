package cream.cli;

import fasttui.behaviour.Behaviour;
import fasttui.component.Component;

class FilesBehaviour implements Behaviour {

    private final Files files;

    public FilesBehaviour(Files files) {
        this.files = files;
    }

    @Override
    public void onMouseMoved(Component target, int cellX, int cellY) {
        if (!files.contains(cellX, cellY)) return;
        int row = cellY - (files.getY() + 1);
        int visibleHeight = files.getHeight() - 2;
        if (row >= 0 && row < visibleHeight) {
            int index = files.getScrollOffset() + row;
            if (index < files.getFileCount()) {
                files.select(index);
            }
        }
    }


    @Override
    public void onMouseReleased(Component target, int cellX, int cellY) {
        onMouseMoved(target, cellX, cellY);
        files.activateSelected();
    }
}
