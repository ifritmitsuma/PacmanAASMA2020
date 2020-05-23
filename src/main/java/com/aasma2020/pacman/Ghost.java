package com.aasma2020.pacman;

import com.aasma2020.pacman.board.BoardElement;
import com.aasma2020.pacman.communication.Agent;
import com.aasma2020.pacman.communication.MapAreaInfo;
import com.aasma2020.pacman.communication.Report;
import com.aasma2020.pacman.communication.SocietyAgent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public abstract class Ghost extends Agent implements SocietyAgent {
    //Anim Vars
    Timer animTimer;
    ActionListener animAL;

    //Pending Vars
    Timer pendingTimer;
    ActionListener pendingAL;

    //Move Vars
    Timer moveTimer;
    ActionListener moveAL;
    public moveType activeMove;
    protected boolean isStuck = true;
    boolean isPending = false;

    Timer unWeakenTimer1;
    Timer unWeakenTimer2;
    ActionListener unweak1;
    ActionListener unweak2;
    int unweakBlinks;
    boolean isWhite = false;

    protected boolean isWeak = false;
    protected boolean isDead = false;

    protected Boolean toWeaken;

    public boolean isWeak() {
        return isWeak;
    }

    public boolean isDead() {
        return isDead;
    }

    //Image[] pac;
    Image ghostImg;
    int activeImage = 0;
    int addFactor = 1;

    public Point pixelPosition;

    Image[] ghostR;
    Image[] ghostL;
    Image[] ghostU;
    Image[] ghostD;

    Image[] ghostW;
    Image[] ghostWW;
    Image ghostEye;

    int ghostNormalDelay;
    int ghostWeakDelay = 30;
    int ghostDeadDelay = 5;

    BFSFinder baseReturner;

    protected PacBoard parentBoard;

    private Point pacmanPosition;

    public Ghost (int x, int y,PacBoard pb,int ghostDelay) {

        position = new Point(x,y);
        pixelPosition = new Point(28*x,28*y);

        parentBoard = pb;

        activeMove = moveType.RIGHT;

        ghostNormalDelay = ghostDelay;

        loadImages();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        //load weak Image
        ghostW = new Image[2];
        try {
            ghostW[0] = ImageIO.read(loader.getResource("images/ghost/blue/1.png"));
            ghostW[1] = ImageIO.read(loader.getResource("images/ghost/blue/3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ghostWW = new Image[2];
        try {
            ghostWW[0] = ImageIO.read(loader.getResource("images/ghost/white/1.png"));
            ghostWW[1] = ImageIO.read(loader.getResource("images/ghost/white/3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ghostEye = ImageIO.read(loader.getResource("images/eye.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //animation timer
        animAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                activeImage = (activeImage + 1) % 2;
            }
        };
        animTimer = new Timer(100,animAL);
        animTimer.start();

        moveAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                if((pixelPosition.x % 28 == 0) && (pixelPosition.y % 28 == 0)){
                    if(!isStuck) {
                        switch (activeMove) {
                            case RIGHT:
                                position.x++;
                                break;
                            case LEFT:
                                position.x--;
                                break;
                            case UP:
                                position.y--;
                                break;
                            case DOWN:
                                position.y++;
                                break;
                        }
                        dispatchEvent(Messeges.AREATEST);
                    }


                    activeMove = getMoveAI();
                    isStuck = true;

                    //animTimer.stop();
                    //System.out.println("LOGICAL POS :" + logicalPosition.x + " , " + logicalPosition.y);
                    //if(todoMove != com.aasma2020.pacman.moveType.NONE) {
                    //    activeMove = todoMove;
                    //    todoMove = com.aasma2020.pacman.moveType.NONE;
                    //}
                }else{
                    isStuck = false;
                    //animTimer.start();
                }
                // }
                //TODO : fix ghost movements
                switch(activeMove){
                    case RIGHT:
                        if(pixelPosition.x >= (parentBoard.m_x-1) * 28){
                            return;
                        }
                        if((position.x+1 < parentBoard.m_x) && (parentBoard.map[position.x+1][position.y]>0) && ((parentBoard.map[position.x+1][position.y]<26)||isPending)){
                            return;
                        }
                        pixelPosition.x ++;
                        break;
                    case LEFT:
                        if(pixelPosition.x <= 0){
                            return;
                        }
                        if((position.x-1 >= 0) && (parentBoard.map[position.x-1][position.y]>0) && ((parentBoard.map[position.x-1][position.y]<26)||isPending)){
                            return;
                        }
                        pixelPosition.x --;
                        break;
                    case UP:
                        if(pixelPosition.y <= 0){
                            return;
                        }
                        if((position.y-1 >= 0) && (parentBoard.map[position.x][position.y-1]>0) && ((parentBoard.map[position.x][position.y-1]<26)||isPending)){
                            return;
                        }
                        pixelPosition.y--;
                        break;
                    case DOWN:
                        if(pixelPosition.y >= (parentBoard.m_y-1) * 28){
                            return;
                        }
                        if((position.y+1 < parentBoard.m_y) && (parentBoard.map[position.x][position.y+1]>0) && ((parentBoard.map[position.x][position.y+1]<26)||isPending)){
                            return;
                        }
                        pixelPosition.y ++;
                        break;
                }

                dispatchEvent(Messeges.AREATEST);
                dispatchEvent(Messeges.COLTEST);
            }
        };
        moveTimer = new Timer(ghostDelay,moveAL);
        moveTimer.start();

        unweak1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unWeakenTimer2.start();
                unWeakenTimer1.stop();
            }
        };
        unWeakenTimer1 = new Timer(7000,unweak1);

        unweak2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(toWeaken != null && toWeaken) {
                    unweakBlinks = 0;
                    unWeakenTimer2.stop();
                    unWeakenTimer1.start();
                    isWhite = false;
                    toWeaken = false;
                    return;
                }

                if(unweakBlinks == 10){
                    unweaken();
                    toWeaken = null;
                    unWeakenTimer2.stop();
                }
                if(unweakBlinks % 2 == 0){
                    isWhite = true;
                }else{
                    isWhite = false;
                }
                unweakBlinks++;
            }
        };
        unWeakenTimer2 = new Timer(250,unweak2);


        pendingAL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPending = false;
                pendingTimer.stop();
            }
        };
        pendingTimer = new Timer(7000,pendingAL);

        baseReturner = new BFSFinder(pb);
        //start AI
        activeMove = getMoveAI();

    }

    protected abstract void dispatchEvent(int message);

    //load Images from Resource
    public abstract void loadImages();

    //get Move Based on AI
    public abstract moveType getMoveAI();

    //get possible Moves
    public ArrayList<moveType> getPossibleMoves(){
        ArrayList<moveType> possibleMoves = new ArrayList<>();

        if(position.x >= 0 && position.x < parentBoard.m_x-1 && position.y >= 0 && position.y < parentBoard.m_y-1 ) {
            //System.out.println(this.toString());
            if (!(parentBoard.map[position.x + 1][position.y] > 0)) {
                possibleMoves.add(moveType.RIGHT);
            }

            if (!(parentBoard.map[position.x - 1][position.y] > 0)) {
                possibleMoves.add(moveType.LEFT);
            }

            if(!(parentBoard.map[position.x][position.y-1]>0)){
                possibleMoves.add(moveType.UP);
            }

            if(!(parentBoard.map[position.x][position.y+1]>0)){
                possibleMoves.add(moveType.DOWN);
            }
        }

        return possibleMoves;
    }

    public Image getGhostImage(){
        if(!isDead) {
            if (!isWeak) {
                switch (activeMove) {
                    case RIGHT:
                        return ghostR[activeImage];
                    case LEFT:
                        return ghostL[activeImage];
                    case UP:
                        return ghostU[activeImage];
                    case DOWN:
                        return ghostD[activeImage];
                }
                return ghostR[activeImage];
            } else {
                if (isWhite) {
                    return ghostWW[activeImage];
                } else {
                    return ghostW[activeImage];
                }
            }
        }else{
            return ghostEye;
        }
    }


    public void weaken(){
        if(toWeaken != null) {
            toWeaken = true;
        } else {
            toWeaken = false;
        }
        isWeak = true;
        moveTimer.setDelay(ghostWeakDelay);
        unweakBlinks = 0;
        isWhite = false;
        unWeakenTimer1.restart();
    }

    public void unweaken(){
        isWeak = false;
        moveTimer.setDelay(ghostNormalDelay);
    }

    public void die(){
        isDead = true;
        moveTimer.setDelay(ghostDeadDelay);
    }

    public void undie(){
        //Shift Left Or Right
        int r = ThreadLocalRandom.current().nextInt(3);
        if (r == 0) {
            //Do nothing
        }
        if(r==1){
            position.x += 1;
            pixelPosition.x += 28;
        }
        if(r==2){
            position.x -= 1;
            pixelPosition.x -= 28;
        }
        isPending = true;
        pendingTimer.start();

        isDead = false;
        isWeak = false;
        moveTimer.setDelay(ghostNormalDelay);
    }

    public void setLastPacmanPosition(Point pacmanPosition) {
        this.pacmanPosition = pacmanPosition;
    }

    @Override
    public void sendReport(SocietyAgent agent) {
        Report report = new Report();
        report.setPacmanPosition(this.pacmanPosition);
        agent.receiveReport(report);
    }

    @Override
    public void receiveReport(Report report) {
        this.pacmanPosition = report.getPacmanPosition();
    }

    @Override
    public void decideAndAct(MapAreaInfo info) {

        if(info.getPacman() != null) {
            System.out.println("---- " + this.getClass().getSimpleName() + " ----");
            System.out.println("Found Pacman!");
        }
      /* for(Ghost ghost : info.getGhosts().values()) {

            System.out.println("Found a " + ghost.getClass().getSimpleName() + " ghost...");
        }
*/
    }
}