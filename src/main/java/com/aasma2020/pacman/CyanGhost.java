package com.aasma2020.pacman;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class CyanGhost extends RandomGhost {

    public CyanGhost(int x, int y,PacBoard pb){
        super(x,y,pb,9);
    }

    @Override
    public void loadImages(){
        ghostR = new Image[2];
        ghostL = new Image[2];
        ghostU = new Image[2];
        ghostD = new Image[2];
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ghostR[0] = ImageIO.read(loader.getResource("images/ghost/cyan/1.png"));
            ghostR[1] = ImageIO.read(loader.getResource("images/ghost/cyan/3.png"));
            ghostL[0] = ImageHelper.flipHor(ImageIO.read(loader.getResource("images/ghost/cyan/1.png")));
            ghostL[1] = ImageHelper.flipHor(ImageIO.read(loader.getResource("images/ghost/cyan/3.png")));
            ghostU[0] = ImageIO.read(loader.getResource("images/ghost/cyan/4.png"));
            ghostU[1] = ImageIO.read(loader.getResource("images/ghost/cyan/5.png"));
            ghostD[0] = ImageIO.read(loader.getResource("images/ghost/cyan/6.png"));
            ghostD[1] = ImageIO.read(loader.getResource("images/ghost/cyan/7.png"));
        }catch(IOException e){
            System.err.println("Cannot Read Images !");
        }
    }

}
