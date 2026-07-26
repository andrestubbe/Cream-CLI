package cream.cli.control;

import cream.cli.Client;
import fastkeyboard.FastKeyboard;
import fastkeyboard.FastKeyboardImpl;
import fastkeyboard.Keys;
import fastterminal.FastTerminal;

/**
 * Lean Events dispatcher for CreamCLI.
 * Directs keyboard events straight to FocusManager and updates modifier keys.
 */
public class Events {

    private final Client client;
    private final FocusManager focusManager;
    private MouseHandler mouseHandler;

    public Events(final Client client) {
        this.client = client;
        this.focusManager = client.getViewManager().focusManager;
        this.initMouse();
        this.initKeyboard();
    }

    public void initMouse() {
        this.mouseHandler = new MouseHandler(client, client.getContainer(), client.getNavigator(), client.getEditor(), client.getOmnibox());
        fastterminal.AnsiMouse.open(this.mouseHandler);
    }

    private void initKeyboard() {
        FastKeyboard keyboard = new FastKeyboardImpl();
        keyboard.startListening((h, vKey, mc, pressed, e0, ts, ch) -> {
            // Always update modifier state, even when terminal is not focused,
            // to prevent Ctrl/Shift/Alt from getting stuck.
            boolean isModifier = false;
            if (vKey == Keys.SHIFT || vKey == Keys.LSHIFT || vKey == Keys.RSHIFT) {
                focusManager.setShiftHeld(pressed);
                isModifier = true;
            }
            if (vKey == Keys.ALT || vKey == Keys.LALT || vKey == Keys.RALT || vKey == Keys.MENU) {
                focusManager.setAltHeld(pressed);
                isModifier = true;
            }
            if (vKey == Keys.CONTROL || vKey == Keys.LCONTROL || vKey == Keys.RCONTROL) {
                focusManager.setCtrlHeld(pressed);
                isModifier = true;
                if (mouseHandler != null) {
                    mouseHandler.recheckHover();
                }
            }

            // Block all non-modifier dispatching when terminal is not focused
            if (!FastTerminal.isTerminalFocused()) return;

            if (pressed && vKey == Keys.Q && focusManager.isCtrlHeld()) {
                System.exit(0);
                return;
            }

            if (focusManager.dispatchKeyInput(vKey, ch, pressed)) {
                client.repaint();
            }
        });
    }
}
