package cream.cli.view.ui;

import fasttui.composable.ScrollVertical;

/**
 * Manages vertical scrolling state, bounds clamping, visible item range tracking,
 * and automatic synchronization with a {@link ScrollVertical} widget.
 * <p>
 * <b>NOTE / TODO:</b> Candidate to be migrated directly into FastTUI (e.g. {@code fasttui.composable.ScrollController})
 * once finalized across all UI containers (Editor, Files, Popup).
 */
public class ScrollController {

    private int scrollOffset = 0;
    private int visibleCount = 0;
    private int totalCount = 0;

    private ScrollVertical scrollbar;
    private Runnable onScrollCallback;

    public ScrollController() {
        this(null, null);
    }

    public ScrollController(ScrollVertical scrollbar, Runnable onScrollCallback) {
        this.scrollbar = scrollbar;
        this.onScrollCallback = onScrollCallback;
        if (this.scrollbar != null) {
            this.scrollbar.setScrollListener(this::setScrollOffsetFromScrollbar);
        }
    }

    public void setScrollbar(ScrollVertical scrollbar) {
        this.scrollbar = scrollbar;
        if (this.scrollbar != null) {
            this.scrollbar.setScrollListener(this::setScrollOffsetFromScrollbar);
        }
    }

    public void setOnScrollCallback(Runnable onScrollCallback) {
        this.onScrollCallback = onScrollCallback;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public int getVisibleCount() {
        return visibleCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getMaxScrollOffset() {
        return Math.max(0, totalCount - visibleCount);
    }

    public void update(int totalCount, int visibleCount) {
        this.totalCount = totalCount;
        this.visibleCount = visibleCount;
        // Clamp existing offset in case totalCount shrank
        int clamped = clampOffset(scrollOffset);
        if (clamped != scrollOffset) {
            scrollOffset = clamped;
        }
        syncScrollbar();
    }

    public void scrollBy(int delta) {
        setScrollOffset(scrollOffset + delta);
    }

    public void setScrollOffset(int newOffset) {
        int clamped = clampOffset(newOffset);
        if (this.scrollOffset != clamped) {
            this.scrollOffset = clamped;
            syncScrollbar();
            notifyCallback();
        }
    }

    /**
     * Ensures that the specified index is visible within the current viewport range.
     * Scrolls up or down if necessary.
     *
     * @param index the target line or item index
     * @return true if scrolling occurred, false otherwise
     */
    public boolean ensureVisible(int index) {
        if (visibleCount <= 0) return false;
        int oldOffset = scrollOffset;
        if (index < scrollOffset) {
            setScrollOffset(index);
        } else if (index >= scrollOffset + visibleCount) {
            setScrollOffset(index - visibleCount + 1);
        }
        return oldOffset != scrollOffset;
    }

    public void syncScrollbar() {
        if (scrollbar == null) return;
        boolean scrollNeeded = totalCount > visibleCount;
        scrollbar.setVisible(scrollNeeded);
        if (scrollNeeded) {
            scrollbar.update(totalCount, visibleCount, scrollOffset);
        }
    }

    private void setScrollOffsetFromScrollbar(int offset) {
        int clamped = clampOffset(offset);
        if (this.scrollOffset != clamped) {
            this.scrollOffset = clamped;
            syncScrollbar();
            notifyCallback();
        }
    }

    private int clampOffset(int offset) {
        return Math.max(0, Math.min(offset, getMaxScrollOffset()));
    }

    private void notifyCallback() {
        if (onScrollCallback != null) {
            onScrollCallback.run();
        }
    }
}
