package cream.cli.control;

import cream.cli.view.ui.Popup;
import fastkeyboard.Keys;
import fasttui.behaviour.ButtonState;
import fasttui.composable.Button;

/**
 * Controller for individual Omnibox Button elements (Mode, Service, Model) in CreamCLI.
 * When activated via ENTER/SPACE, opens the dropdown popup and transfers focus directly into the popup.
 */
public class OmniboxButtonController implements FocusTarget {

    private final Button button;
    private final Popup popup;
    private final FocusManager focusManager;

    public OmniboxButtonController(Button button, Popup popup, FocusManager focusManager) {
        this.button = button;
        this.popup = popup;
        this.focusManager = focusManager;
        if (this.popup != null) {
            this.popup.setParentTarget(this);
            this.popup.setFocusManager(focusManager);
        }
    }

    @Override
    public boolean isFocused() {
        return focusManager != null && focusManager.getCurrentComponent() == this;
    }

    @Override
    public void onFocusGained() {
        if (button != null) button.setButtonState(ButtonState.FOCUSSED);
    }

    @Override
    public void onFocusLost() {
        if (button != null) button.setButtonState(ButtonState.NORMAL);
    }

    @Override
    public boolean handleKeyInput(int vKey, String ch, boolean pressed) {
        if (!isFocused() || !pressed) return false;
        if (vKey == Keys.ENTER || vKey == Keys.SPACE) {
            togglePopup();
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMouseClick(int mouseX, int mouseY, boolean isPressed) {
        if (!isFocused() || !isPressed) return false;
        togglePopup();
        return true;
    }

    private void togglePopup() {
        if (popup != null) {
            boolean nextVisible = !popup.isVisible();
            popup.setVisible(nextVisible);
            if (nextVisible) {
                focusManager.setCurrentComponent(popup);
            }
        }
    }
}
