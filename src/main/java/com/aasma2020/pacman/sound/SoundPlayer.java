package com.aasma2020.pacman.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {

    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();

    private static boolean mute = false;

    public static synchronized void playAsync(final String name) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                if(mute) {
                    return;
                }
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            loader.getResourceAsStream("sounds/" + name));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    public static void play(final String name) {
        if(mute) {
            return;
        }
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    loader.getResourceAsStream("sounds/" + name));
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void muteToggle() {
        mute = !mute;
    }

    public static void muteToggle(Boolean state) {
        mute = state;
    }
}