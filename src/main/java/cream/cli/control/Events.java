package cream.cli.control;

import cream.cli.Client;
import cream.cli.control.focus.FocusManager;
import cream.cli.control.handler.MouseHandler;
import fastkeyboard.FastKeyboard;
import fastkeyboard.FastKeyboardImpl;
import fastkeyboard.Keys;
import fastterminal.AnsiMouse;
import fastterminal.FastTerminal;

public final class Events {

    private final Client client;
    private final FocusManager focusManager;
    private final MouseHandler mouseHandler;

    public Events(final Client client, final String[] args) {
        this.client = client;
        this.focusManager = client.getViewManager().focusManager;
        this.mouseHandler = new MouseHandler(this.client);
        AnsiMouse.open(this.mouseHandler);
        final FastKeyboard keyboard = new FastKeyboardImpl();
        keyboard.startListening(this::onKeyEvent);
        IOManager.setupRecent(client, args);
    }

    private void onKeyEvent(final long h, final int vKey, final int mc, final boolean pressed, final boolean e0, final long ts, final String ch) {
        switch (vKey) {
            case Keys.SHIFT, Keys.LSHIFT, Keys.RSHIFT -> this.focusManager.setShiftHeld(pressed);
            case Keys.ALT, Keys.LALT, Keys.RALT -> this.focusManager.setAltHeld(pressed);
            case Keys.CONTROL, Keys.LCONTROL, Keys.RCONTROL -> {
                this.focusManager.setCtrlHeld(pressed);
                this.mouseHandler.recheckHover();
            }
        }
        if (!FastTerminal.isTerminalFocused()) return;
        if (pressed && vKey == Keys.Q && focusManager.isCtrlHeld()) {
            System.exit(0);
            return;
        }
        if (this.focusManager.dispatchKeyInput(vKey, ch, pressed)) {
            this.client.repaint();
        }
    }
}
