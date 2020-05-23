package com.aasma2020.pacman;

import com.aasma2020.pacman.board.BoardElement;

import java.awt.*;

public class PowerUpFood extends BoardElement {

    public PowerUpFood(int x,int y,int type){
        position = new Point(x,y);
        this.type = type;
    }

    public int type; //0-4

}
