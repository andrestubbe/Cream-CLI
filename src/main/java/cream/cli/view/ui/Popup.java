package cream.cli.view.ui;

import cream.cli.control.focus.FocusManager;
import cream.cli.control.focus.FocusTarget;
import cream.cli.view.theme.ThemeService;
import fastkeyboard.Keys;
import fastterminal.FastTerminalScene;
import fasttui.behaviour.ButtonState;
import fasttui.component.BorderStyle;
import fasttui.component.Box;
import fasttui.component.ColorSet;
import fasttui.component.Container;
import fasttui.composable.ScrollVertical;
import fasttui.util.TerminalGraphics;

import java.util.ArrayList;
import java.util.List;

public abstract class Popup extends Container implements FocusTarget, FocusManager.FallbackFocusTarget {

    private static final BorderStyle POPUP_BORDER_STYLE = BorderStyle.ROUNDED;

    protected final Box box;
    protected final ScrollVertical scrollbar;
    protected final List<Row> rows = new ArrayList<>();

    protected int selectedIndex = 0;
    protected int scrollOffset = 0;
    protected int visibleRows = 8;
    protected Runnable onSelectAction;
    protected FocusTarget parentTarget;
    protected FocusManager focusManager;

    public Popup(int x, int y, int width, int height) {
        super(x, y, width, height);

        this.box = new Box(0, 0, width, height);
        this.box.setBackgroundColor(ThemeService.get().getPopupBackgroundNormal());
        this.box.setBorderColor(ThemeService.get().getPopupForegroundBorder());
        this.box.setBorderStyle(POPUP_BORDER_STYLE);
        this.add(this.box);

        final int scrollbarX = width - 1;
        final int scrollbarY = 1;
        final int scrollbarHeight = Math.max(1, height - 2);
        int transparent = -2;
        ColorSet scrollFg = new ColorSet(ThemeService.get().getScrollbarForegroundNormal(), ThemeService.get().getScrollbarForegroundHover(), ThemeService.get().getScrollbarForegroundNormal(), ThemeService.get().getScrollbarForegroundNormal());
        ColorSet scrollBg = new ColorSet(transparent, ThemeService.get().getScrollbarBackgroundHover(), transparent, transparent);
        this.scrollbar = new ScrollVertical(scrollbarX, scrollbarY, scrollbarHeight, scrollFg, scrollBg);
    }

    @Override
    public void render(FastTerminalScene scene) {
        if (!isVisible()) return;

        box.setX(0);
        box.setY(0);
        box.setWidth(this.width);
        box.setHeight(this.height);

        TerminalGraphics.applyStenciledBlurredShadow(scene, getAbsoluteX(), getAbsoluteY(), this.width, this.height, 0, 1, 0x000000, 0.33, 2.5, 1.0);
//        TerminalGraphics.applyBackgroundBlur(scene, this.x, this.y, this.width, this.height, 1.0, 2.0, 0.8);
//        TerminalGraphics.applyOverlay(scene, this.x, this.y, this.width, this.height, POPUP_BACKGROUND_NORMAL, 0.90);

        super.render(scene);

        int innerH = Math.max(1, this.height - 2);
        if (this.rows.size() > innerH) {
            this.scrollbar.setX(this.width - 2);
            this.scrollbar.setY(1);
            this.scrollbar.setHeight(innerH);
            this.scrollbar.update(this.rows.size(), innerH, scrollOffset);
            this.scrollbar.render(scene);
        }
    }

    public void moveUp() {
        if (rows.isEmpty()) return;
        if (selectedIndex > 0) {
            selectedIndex--;
        } else {
            selectedIndex = rows.size() - 1;
        }
        updateScrollAndSelection();
    }

    public void moveDown() {
        if (rows.isEmpty()) return;
        if (selectedIndex < rows.size() - 1) {
            selectedIndex++;
        } else {
            selectedIndex = 0;
        }
        updateScrollAndSelection();
    }

    public void scrollUp(int count) {
        for (int i = 0; i < count; i++) moveUp();
    }

    public void scrollDown(int count) {
        for (int i = 0; i < count; i++) moveDown();
    }

    protected void updateScrollAndSelection() {
        int innerH = Math.max(1, this.height - 2);
        this.visibleRows = innerH;

        if (selectedIndex < scrollOffset) {
            scrollOffset = selectedIndex;
        } else if (selectedIndex >= scrollOffset + innerH) {
            scrollOffset = selectedIndex - innerH + 1;
        }

        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            boolean isSel = (i == selectedIndex);
            boolean isVisibleRow = (i >= scrollOffset && i < scrollOffset + innerH);
            r.setVisible(isVisibleRow);
            if (isVisibleRow) {
                r.setX(1);
                r.setY(1 + (i - scrollOffset));
            }
            r.onStateChanged(isSel ? ButtonState.HOVERED : ButtonState.NORMAL);
        }
    }

    public void addRow(Row row) {
        this.rows.add(row);
        if (!this.children.contains(row)) {
            row.setParent(this);
            this.children.add(row);
        }
        updateScrollAndSelection();
    }

    public void clearRows() {
        for (Row r : rows) {
            this.children.remove(r);
        }
        this.rows.clear();
        this.selectedIndex = 0;
        this.scrollOffset = 0;
    }

    // --- FocusTarget Implementation ---
    @Override
    public boolean handleKeyInput(int vKey, String ch, boolean pressed) {
        if (!isVisible() || !pressed) return false;

        if (vKey == Keys.UP) {
            moveUp();
            return true;
        } else if (vKey == Keys.DOWN) {
            moveDown();
            return true;
        } else if (vKey == Keys.PAGE_UP) {
            scrollUp(visibleRows);
            return true;
        } else if (vKey == Keys.PAGE_DOWN) {
            scrollDown(visibleRows);
            return true;
        } else if (vKey == Keys.ESC) {
            setVisible(false);
            if (focusManager != null && parentTarget != null) {
                focusManager.setCurrentComponent(parentTarget);
            }
            return true;
        } else if (vKey == Keys.ENTER || vKey == Keys.TAB) {
            setVisible(false);
            if (onSelectAction != null) {
                onSelectAction.run();
            }
            if (focusManager != null && parentTarget != null) {
                focusManager.setCurrentComponent(parentTarget);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMouseScroll(int delta) {
        if (!isVisible()) return false;
        if (delta > 0) scrollUp(3);
        else if (delta < 0) scrollDown(3);
        return true;
    }

    @Override
    public boolean handleMouseClick(int mouseX, int mouseY, boolean isPressed) {
        if (!isVisible() || !isPressed) return false;
        if (containsPoint(mouseX, mouseY)) {
            int absY = getAbsoluteY();
            int relY = mouseY - (absY + 1);
            int clickedIdx = scrollOffset + relY;
            if (clickedIdx >= 0 && clickedIdx < rows.size()) {
                setSelectedIndex(clickedIdx);
                setVisible(false);
                if (onSelectAction != null) {
                    onSelectAction.run();
                }
                return true;
            }
        } else {
            setVisible(false);
            if (focusManager != null && parentTarget != null) {
                focusManager.setCurrentComponent(parentTarget);
            }
            return true;
        }
        return false;
    }

    public boolean containsPoint(int mouseX, int mouseY) {
        int absX = getAbsoluteX();
        int absY = getAbsoluteY();
        return isVisible() && mouseX >= absX && mouseX < absX + width && mouseY >= absY && mouseY < absY + height;
    }

    public int getItemIndexAt(int mouseX, int mouseY) {
        if (!containsPoint(mouseX, mouseY)) return -1;
        int absY = getAbsoluteY();
        int relY = mouseY - (absY + 1);
        int itemIdx = scrollOffset + relY;
        if (itemIdx >= 0 && itemIdx < rows.size()) {
            return itemIdx;
        }
        return -1;
    }

    @Override
    public boolean isFocused() {
        return isVisible();
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public String getLabelAt(int index) {
        if (index >= 0 && index < rows.size()) {
            Row r = rows.get(index);
            if (r != null && r.getCells() != null && !r.getCells().isEmpty()) {
                if (r.getCells().size() > 1) {
                    return r.getCells().get(1).text;
                }
                return r.getCells().get(0).text;
            }
        }
        return null;
    }

    @Override
    public FocusTarget getParentTarget() {
        return this.parentTarget;
    }

    public void setFocusManager(final FocusManager focusManager) {
        this.focusManager = focusManager;
    }

    public void setParentTarget(final FocusTarget parentTarget) {
        this.parentTarget = parentTarget;
    }

    public void setOnSelectAction(final Runnable action) {
        this.onSelectAction = action;
    }

    @Override
    public void setX(final int newX) {
        super.setX(newX);
        this.updateScrollAndSelection();
    }

    @Override
    public void setY(final int newY) {
        super.setY(newY);
        this.updateScrollAndSelection();
    }

    public void setSelectedIndex(final int index) {
        if (index >= 0 && index < rows.size()) {
            this.selectedIndex = index;
            this.updateScrollAndSelection();
        }
    }

    public void setHoveredIndex(final int index) {
        if (index >= 0 && index < rows.size()) {
            this.selectedIndex = index;
            int innerH = Math.max(1, this.height - 2);
            for (int i = 0; i < rows.size(); i++) {
                Row r = rows.get(i);
                boolean isSel = (i == selectedIndex);
                boolean isVisibleRow = (i >= scrollOffset && i < scrollOffset + innerH);
                r.setVisible(isVisibleRow);
                r.onStateChanged(isSel ? ButtonState.HOVERED : ButtonState.NORMAL);
            }
        }
    }
}