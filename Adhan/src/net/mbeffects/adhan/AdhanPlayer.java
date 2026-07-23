package net.mbeffects.adhan;

import javax.microedition.media.*;
import java.io.*;

public class AdhanPlayer {

    private Player _player;

    public void play() {
        try {
            stop();
            InputStream is = getClass().getResourceAsStream("/adhan.mp3");
            if (is == null) return;
            _player = Manager.createPlayer(is, "audio/mpeg");
            _player.realize();
            _player.prefetch();
            _player.start();
        } catch (Throwable t) {
            // silent — never crash app for audio
        }
    }

    public void stop() {
        try {
            if (_player != null) {
                _player.stop();
                _player.close();
                _player = null;
            }
        } catch (Throwable t) {}
    }
}
