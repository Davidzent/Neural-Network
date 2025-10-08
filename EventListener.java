import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class EventListener {
    private static volatile boolean AddPressed = false;
    private static volatile boolean MinusPressed = false;
    private static volatile boolean NPressed = false;
    private static volatile boolean MPressed = false;

    public static boolean isAddPressed() {
        synchronized (App.class) {
            return AddPressed;
        }
    }

    public static boolean isMinusPressed() {
        synchronized (App.class) {
            return MinusPressed;
        }
    }

    public static boolean isNPressed() {
        synchronized (App.class) {
            return NPressed;
        }
    }

    public static boolean isMPressed() {
        synchronized (App.class) {
            return MPressed;
        }
    }

    EventListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (App.class) {
                    switch (ke.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            switch (ke.getKeyCode()) {
                                case KeyEvent.VK_ADD:
                                    AddPressed = true;
                                    break;
                                case KeyEvent.VK_SUBTRACT:
                                    MinusPressed = true;
                                    break;
                                case KeyEvent.VK_N:
                                    NPressed = true;
                                    break;
                                case KeyEvent.VK_M:
                                    MPressed = true;
                                    break;
                            }
                            break;
                        case KeyEvent.KEY_RELEASED:
                            switch (ke.getKeyCode()) {
                                case KeyEvent.VK_ADD:
                                    AddPressed = false;
                                    break;
                                case KeyEvent.VK_SUBTRACT:
                                    MinusPressed = false;
                                    break;
                                case KeyEvent.VK_N:
                                    NPressed = false;
                                    break;
                                case KeyEvent.VK_M:
                                    MPressed = false;
                                    break;
                            }
                            break;
                    }
                    return false;
                }
            }
        });
    }
}
