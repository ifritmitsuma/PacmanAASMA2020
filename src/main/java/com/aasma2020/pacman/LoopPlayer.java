package com.aasma2020.pacman;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Created by Armin on 4/23/2016.
 */
public class LoopPlayer {

    Clip clip;
    AudioInputStream inputStream;

    private ClassLoader loader = Thread.currentThread().getContextClassLoader();

    private boolean mute = false;
    private boolean playing = false;

    public LoopPlayer(String soundname){
        try {
            clip = AudioSystem.getClip();
            inputStream = AudioSystem.getAudioInputStream(
                    loader.getResourceAsStream("sounds/" + soundname));
            clip.open(inputStream);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void start(){
        playing = true;
        if(!mute) {
            unmuteSound();
        }
    }

    public void stop(){
        playing = false;
        if(mute) {
            muteSound();
        }
    }

    public void unmuteSound() {
        try {
            if(clip.isRunning()) {
                clip.stop();
            }
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void muteSound() {
        try {
            clip.stop();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void muteToggle() {
        mute = !mute;
        if(playing && !this.mute) {
            unmuteSound();
        } else {
            muteSound();
        }
    }

    public void muteToggle(Boolean state) {
        mute = state;
        if(playing && !this.mute) {
            unmuteSound();
        } else {
            muteSound();
        }
    }
}
