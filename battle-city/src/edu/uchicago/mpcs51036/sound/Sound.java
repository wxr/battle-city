package edu.uchicago.mpcs51036.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

    // Play sound (no loop)
    // Reference: http://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
    public static synchronized void playSound(String strPath) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream ais = AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream(strPath));
                clip.open(ais);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Play sound (loop)
    // Reference: http://stackoverflow.com/questions/4875080/music-loop-in-java
    public static Clip clipForLoopFactory(String strPath) {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream(strPath));
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clip;
    }

}


