package cream.cli.control;

import cream.cli.Client;
import cream.cli.model.util.SymbolResolver;
import cream.cli.view.editor.Editor;
import cream.cli.view.files.Navigator;
import cream.cli.view.footer.Footer;
import cream.cli.view.omnibox.Omnibox;
import fastmouse.FastMouseListener;
import fastterminal.FastTerminal;
import fasttui.behaviour.Behaviour;
import fasttui.behaviour.EventDispatcher;
import fasttui.component.Component;
import fasttui.component.Container;
import fasttui.component.Interactive;

import java.io.File;

public class MouseHandler implements FastMouseListener {

    private final Client client;
    private final Container container;
    private final Navigator navigator;
    private final Editor editor;
    private final Omnibox omnibox;
    private final Footer footer;

    private final int[] mouseCell = {-1, -1};
    private boolean mouseDown = false;
    private Component draggedComponent = null;

    public MouseHandler(Client client, Container container, Navigator navigator, Editor editor, Omnibox omnibox, Footer footer) {
        this.client = client;
        this.container = container;
        this.navigator = navigator;
        this.editor = editor;
        this.omnibox = omnibox;
        this.footer = footer;
    }

    private void logDebug(String msg) {
        try {
            File logFile = new File(System.getProperty("user.home") + File.separator + ".cream", "click_debug.log");
            java.nio.file.Files.writeString(logFile.toPath(), msg + "\n", java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception ignored) {
        }
    }

    public void recheckHover() {
        if (mouseCell[0] >= 0 && mouseCell[1] >= 0) {
            onMouseMove(0, 0, 0, mouseCell[0], mouseCell[1]);
        }
    }

    @Override
    public void onMouseMove(long h, int dx, int dy, int absX, int absY) {
        if (!FastTerminal.isTerminalFocused()) {
            return;
        }

        mouseCell[0] = absX;
        mouseCell[1] = absY;

        // Switch to MOUSE mode only when there is actual physical mouse displacement (dx != 0 || dy != 0)
        // and no recent keyboard activity (draining async terminal buffer packets)
        if (dx != 0 || dy != 0) {
            FocusManager fm = client != null && client.getViewManager() != null ? client.getViewManager().focusManager : null;
            if (fm != null && !fm.isKeyboardCooldownActive()) {
                fm.setInputMode(FocusManager.InputMode.MOUSE);
            }
        }

        // Check CTRL + Hover hyperlink over Editor
        if (editor != null && editor.isVisible() && client != null && client.getViewManager() != null && client.getViewManager().focusManager != null) {
            FocusManager fm = client.getViewManager().focusManager;
            if (fm.isCtrlHeld() && absX >= editor.getX() && absX < editor.getX() + editor.getWidth() && absY >= editor.getY() && absY < editor.getY() + editor.getHeight()) {
                int[] cellPos = editor.cellToDocPos(absX, absY);
                int docLine = cellPos[0];
                int docCol = cellPos[1];
                int[] bounds = editor.getWordBoundsAt(docLine, docCol);
                if (bounds != null) {
                    String word = editor.getWordAt(docLine, docCol);
                    File targetFile = SymbolResolver.resolveClassFile(word);
                    if (targetFile != null) {
                        editor.setHoveredHyperlink(new Editor.HyperlinkRange(docLine, bounds[0], bounds[1], targetFile));
                    } else {
                        editor.setHoveredHyperlink(null);
                    }
                } else {
                    editor.setHoveredHyperlink(null);
                }
            } else if (editor.getHoveredHyperlink() != null) {
                editor.setHoveredHyperlink(null);
            }
        }

        // Check mouse hover on Dialog
        if (client != null && client.dialog != null && client.dialog.isVisible()) {
            if (client.dialog.handleMouseMove(absX, absY)) {
                client.repaint();
                return;
            }
        }

        // Check mouse hover on active popups
        if (editor != null && editor.isVisible() && editor.autocompleteMgr.getAutocompletePopup() != null && editor.autocompleteMgr.getAutocompletePopup().isFocused()) {
            var popup = editor.autocompleteMgr.getAutocompletePopup();
            int hoveredIdx = popup.getItemIndexAt(absX, absY);
            if (hoveredIdx >= 0 && hoveredIdx != popup.getSelectedIndex()) {
                popup.setHoveredIndex(hoveredIdx);
            }
        } else if (omnibox != null) {
            if (this.footer.popupModel != null && this.footer.popupModel.isVisible()) {
                int idx = this.footer.popupModel.getItemIndexAt(absX, absY);
                if (idx >= 0 && idx != this.footer.popupModel.getSelectedIndex()) {
                    this.footer.popupModel.setHoveredIndex(idx);
                }
            } else if (this.footer.popupService != null && this.footer.popupService.isVisible()) {
                int idx = this.footer.popupService.getItemIndexAt(absX, absY);
                if (idx >= 0 && idx != this.footer.popupService.getSelectedIndex()) {
                    this.footer.popupService.setHoveredIndex(idx);
                }
            } else if (this.footer.popupMode != null && this.footer.popupMode.isVisible()) {
                int idx = this.footer.popupMode.getItemIndexAt(absX, absY);
                if (idx >= 0 && idx != this.footer.popupMode.getSelectedIndex()) {
                    this.footer.popupMode.setHoveredIndex(idx);
                }
            }
        }

        if (mouseDown) {
            if (draggedComponent instanceof Interactive ic) {
                for (Behaviour b : ic.getBehaviors()) {
                    b.onMouseDragged(draggedComponent, absX, absY);
                }
            } else {
                EventDispatcher.dispatchMouseDrag(container, absX, absY);
            }
        } else {
            EventDispatcher.dispatchMouseMove(container, absX, absY);
        }

        client.repaint();
    }

    @Override
    public void onMouseButton(long h, int buttonId, boolean isPressed) {
        if (!FastTerminal.isTerminalFocused()) {
            return;
        }

        if (buttonId != 0) return;

        if (isPressed && mouseDown) {
            return;
        }

        mouseDown = isPressed;

        if (isPressed) {
            FocusManager fm = client != null && client.getViewManager() != null ? client.getViewManager().focusManager : null;

            // Check CTRL + Click Jump-to-Definition over Editor
            if (fm != null && fm.isCtrlHeld() && editor != null && editor.isVisible()) {
                Editor.HyperlinkRange link = editor.getHoveredHyperlink();
                if (link != null && link.targetFile() != null) {
                    editor.fileManager.loadFile(link.targetFile());
                    editor.setHoveredHyperlink(null);
                    client.repaint();
                    return;
                }
            }
            // Dialog Mouse Interception
            if (client != null && client.dialog != null && client.dialog.isVisible()) {
                if (client.dialog.handleMouseClick(mouseCell[0], mouseCell[1], true)) {
                    client.repaint();
                    return;
                }
            }

            // Autocomplete Popup FocusTarget Interception
            if (editor != null && editor.isVisible() && editor.autocompleteMgr.getAutocompletePopup() != null && editor.autocompleteMgr.getAutocompletePopup().isFocused()) {
                var popup = editor.autocompleteMgr.getAutocompletePopup();
                if (popup.containsPoint(mouseCell[0], mouseCell[1])) {
                    if (popup.handleMouseClick(mouseCell[0], mouseCell[1], true)) {
                        editor.autocompleteMgr.acceptAutocomplete();
                        client.repaint();
                        return;
                    }
                } else {
                    popup.hide();
                    client.repaint();
                }
            }

            // Auto-close active omnibox popups when clicking anywhere outside
            if (omnibox != null) {
                if (this.footer.popupMode != null && this.footer.popupMode.isVisible() && !this.footer.popupMode.containsPoint(mouseCell[0], mouseCell[1])) {
                    this.footer.popupMode.setVisible(false);
                }
                if (this.footer.popupService != null && this.footer.popupService.isVisible() && !this.footer.popupService.containsPoint(mouseCell[0], mouseCell[1])) {
                    this.footer.popupService.setVisible(false);
                }
                if (this.footer.popupModel != null && this.footer.popupModel.isVisible() && !this.footer.popupModel.containsPoint(mouseCell[0], mouseCell[1])) {
                    this.footer.popupModel.setVisible(false);
                }
            }

            Component hit = EventDispatcher.findComponentAt(container, mouseCell[0], mouseCell[1]);
            draggedComponent = hit;

            FocusTarget newTarget = null;

            if (navigator != null && navigator.files != null && navigator.files.isVisible() && mouseCell[0] >= navigator.files.getX() && mouseCell[0] < navigator.files.getX() + navigator.files.getWidth() && mouseCell[1] >= navigator.files.getY() && mouseCell[1] < navigator.files.getY() + navigator.files.getHeight()) {
                newTarget = client.getViewManager().filesController;
            } else if (editor != null && editor.isVisible() && mouseCell[0] >= editor.getX() && mouseCell[0] < editor.getX() + editor.getWidth() && mouseCell[1] >= editor.getY() && mouseCell[1] < editor.getY() + editor.getHeight()) {
                newTarget = client.getViewManager().editorController;
            } else if (omnibox != null && this.footer != null && mouseCell[1] == this.footer.getY()) {
                if (mouseCell[0] >= this.footer.getX() + this.footer.mode.getX() && mouseCell[0] < this.footer.getX() + this.footer.mode.getX() + this.footer.mode.getWidth()) {
                    newTarget = client.getViewManager().omniboxModeController;
                } else if (mouseCell[0] >= this.footer.getX() + this.footer.service.getX() && mouseCell[0] < this.footer.getX() + this.footer.service.getX() + this.footer.service.getWidth()) {
                    newTarget = client.getViewManager().omniboxServiceController;
                } else if (mouseCell[0] >= this.footer.getX() + this.footer.model.getX() && mouseCell[0] < this.footer.getX() + this.footer.model.getX() + this.footer.model.getWidth()) {
                    newTarget = client.getViewManager().omniboxModelController;
                }
            } else if (omnibox != null && (hit == omnibox.text || (mouseCell[0] >= omnibox.getX() && mouseCell[0] < omnibox.getX() + omnibox.getWidth() && mouseCell[1] >= omnibox.getY() && mouseCell[1] < omnibox.getY() + omnibox.box.getHeight()))) {
                newTarget = client.getViewManager().omniboxTextController;
            }

            if (newTarget != null && fm.getCurrentComponent() != newTarget) {
                logDebug("[DEBUG-CLICK] Changing focus from " + fm.getCurrentComponent() + " to " + newTarget);
                fm.setCurrentComponent(newTarget);
            }

            if (fm != null) {
                boolean handled = fm.dispatchMouseClick(mouseCell[0], mouseCell[1], true);
                logDebug("[DEBUG-CLICK] fm.dispatchMouseClick returned " + handled + " for currentComponent " + fm.getCurrentComponent());
                if (handled) {
                    client.repaint();
                    return;
                }
            }

            logDebug("[DEBUG-CLICK] Dispatching to container for hit component: " + hit);
            EventDispatcher.dispatchMouseClick(container, mouseCell[0], mouseCell[1], true);
        } else {
            if (draggedComponent instanceof Interactive ic) {
                for (Behaviour b : ic.getBehaviors()) {
                    b.onMouseReleased(draggedComponent, mouseCell[0], mouseCell[1]);
                }
            } else if (draggedComponent != null) {
                EventDispatcher.dispatchMouseClick(draggedComponent, mouseCell[0], mouseCell[1], false);
            }
            draggedComponent = null;
        }

        client.repaint();
    }

    @Override
    public void onMouseWheel(long h, int delta) {
        if (!FastTerminal.isTerminalFocused()) {
            return;
        }

        // Delegate mouse wheel scroll directly to active currentComponent in FocusManager
        if (client != null && client.getViewManager() != null && client.getViewManager().focusManager != null) {
            if (client.getViewManager().focusManager.dispatchMouseScroll(delta)) {
                client.repaint();
                return;
            }
        }

        int x = mouseCell[0];
        int y = mouseCell[1];

        // Autocomplete Popup FocusTarget Scroll Interception
        if (editor != null && editor.isVisible() && editor.autocompleteMgr.getAutocompletePopup() != null && editor.autocompleteMgr.getAutocompletePopup().isFocused()) {
            if (editor.autocompleteMgr.getAutocompletePopup().handleMouseScroll(delta)) {
                client.repaint();
                return;
            }
        }

        if (navigator != null && navigator.files != null && navigator.files.isVisible()) {
            int fx = navigator.files.getX();
            int fy = navigator.files.getY();
            int fw = navigator.files.getWidth();
            int fh = navigator.files.getHeight();
            if (x >= fx && x < fx + fw && y >= fy && y < fy + fh) {
                navigator.files.scroll(-delta);
                client.repaint();
                return;
            }
        }

        if (editor != null && editor.isVisible()) {
            int ex = editor.getX();
            int ey = editor.getY();
            int ew = editor.getWidth();
            int eh = editor.getHeight();
            if (x >= ex && x < ex + ew && y >= ey && y < ey + eh) {
                editor.scroll(-delta);
                client.repaint();
                return;
            }
        }
    }
}
