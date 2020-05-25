package com.aasma2020.pacman.ghost;

import com.aasma2020.pacman.helper.ImageHelper;
import com.aasma2020.pacman.board.PacBoard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;


public class OrangeGhost extends RandomGhost {

    public OrangeGhost(int x, int y, PacBoard pb){
        super(x,y,pb,10);
    }

    @Override
    public void loadImages(){
        ghostR = new Image[2];
        ghostL = new Image[2];
        ghostU = new Image[2];
        ghostD = new Image[2];
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ghostR[0] = ImageIO.read(loader.getResource("images/ghost/orange/1.png"));
            ghostR[1] = ImageIO.read(loader.getResource("images/ghost/orange/3.png"));
            ghostL[0] = ImageHelper.flipHor(ImageIO.read(loader.getResource("images/ghost/orange/1.png")));
            ghostL[1] = ImageHelper.flipHor(ImageIO.read(loader.getResource("images/ghost/orange/3.png")));
            ghostU[0] = ImageIO.read(loader.getResource("images/ghost/orange/4.png"));
            ghostU[1] = ImageIO.read(loader.getResource("images/ghost/orange/5.png"));
            ghostD[0] = ImageIO.read(loader.getResource("images/ghost/orange/6.png"));
            ghostD[1] = ImageIO.read(loader.getResource("images/ghost/orange/7.png"));
        }catch(IOException e){
            System.err.println("Cannot Read Images !");
        }
    }

}
