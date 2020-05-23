package com.aasma2020.pacman.board;

import java.awt.*;

public abstract class BoardElement {

    protected Point position;

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

}
