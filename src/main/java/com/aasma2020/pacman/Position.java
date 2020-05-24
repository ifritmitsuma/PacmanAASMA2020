package com.aasma2020.pacman;

import java.awt.*;

public class Position extends Point {

    private long timestamp;

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

}
