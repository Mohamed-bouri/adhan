package net.mbeffects.adhan;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class AdhanApp extends UiApplication {

    public static void main(String[] args) {
        AdhanApp app = new AdhanApp();
        app.enterEventDispatcher();
    }

    public AdhanApp() {
        try {
            AdhanMainScreen screen = new AdhanMainScreen();
            pushScreen(screen);
        } catch (Throwable t) {
            Dialog.alert("CRASH: " + t.getClass().getName()
                + "\n" + t.getMessage());
        }
    }
}
