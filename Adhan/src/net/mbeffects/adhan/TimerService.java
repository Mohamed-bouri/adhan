package net.mbeffects.adhan;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import java.util.*;

public class TimerService extends Thread {

    private static final String[] NAMES =
        { "Fajr","Sunrise","Dhuhr","Asr","Maghrib","Isha" };

    private AdhanMainScreen _screen;
    private boolean         _running = true;
    private int             _lastIdx = -1;
    private boolean         _muted   = false;
    private AdhanPlayer     _player  = null; // lazy — NOT static init

    public TimerService(AdhanMainScreen screen) {
        _screen = screen;
    }

    private AdhanPlayer getPlayer() {
        if (_player == null)
            _player = new AdhanPlayer();
        return _player;
    }

    public void run() {
        while (_running) {
            try {
                checkPrayerTime();
                Application.getApplication().invokeLater(
                    new Runnable() {
                        public void run() {
                            try { _screen.refresh(); }
                            catch (Throwable t) {}
                        }
                    }
                );
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                break;
            } catch (Throwable t) {
                // keep running
            }
        }
    }

    private void checkPrayerTime() {
        try {
            Calendar now   = Calendar.getInstance();
            String[] times = PrayerCalculator.calculate(now.getTime());
            for (int i = 0; i < times.length; i++) {
                if (i == 1) continue;
                if (timeMatches(now, times[i]) && _lastIdx != i) {
                    _lastIdx = i;
                    if (!_muted) getPlayer().play();
                    showAlert(i);
                }
            }
        } catch (Throwable t) {}
    }

    private boolean timeMatches(Calendar now, String t) {
        int h = Integer.parseInt(t.substring(0, 2));
        int m = Integer.parseInt(t.substring(3, 5));
        return now.get(Calendar.HOUR_OF_DAY) == h
            && now.get(Calendar.MINUTE)      == m;
    }

    private void showAlert(final int idx) {
        Application.getApplication().invokeLater(
            new Runnable() {
                public void run() {
                    Dialog.alert("Prayer Time: " + NAMES[idx]);
                }
            }
        );
    }

    public void stopTimer()         { _running = false; }
    public void setMuted(boolean m) { _muted   = m;     }
}
