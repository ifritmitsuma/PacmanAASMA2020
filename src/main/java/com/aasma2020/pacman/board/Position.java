package com.aasma2020.pacman.board;

import java.awt.*;

public class Position extends Point {

    private long timestamp;

    private boolean valid;

    public Position(Point pacman) {
        setLocation(pacman);
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
