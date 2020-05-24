package com.aasma2020.pacman;

import com.aasma2020.pacman.communication.Agent;
import com.aasma2020.pacman.communication.MapAreaInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PacBoard extends JPanel{


    Timer redrawTimer;
    ActionListener redrawAL;

    int[][] map;
    Image[] mapSegments;

    Image foodImage;
    Image[] pfoodImage;

    Image goImage;
    Image vicImage;

    Pacman pacman;
    ArrayList<Food> foods;
    ArrayList<PowerUpFood> pufoods;
    ArrayList<Ghost> ghosts;
    ArrayList<TeleportTunnel> teleports;

    boolean singleplayerGame;
    boolean isCustom = false;
    boolean isGameOver = false;
    boolean isWin = false;
    boolean drawScore = false;
    boolean clearScore = false;
    int scoreToAdd = 0;

    int score;
    JLabel scoreboard;

    LoopPlayer siren;
    boolean mustReactivateSiren = false;
    LoopPlayer pac6;

    public Point ghostBase;

    public int m_x;
    public int m_y;

    MapData md_backup;
    PacWindow windowParent;

    private Timer winTimer;
    protected int winSeconds = 3;

    int areaVisionRadius = 3;

    public PacBoard(JLabel scoreboard,MapData md,PacWindow pw, boolean singleplayerGame){
    	this.singleplayerGame = singleplayerGame;
        this.scoreboard = scoreboard;
        this.setDoubleBuffered(true);
        md_backup = md;
        windowParent = pw;

        m_x = md.getX();
        m_y = md.getY();
        this.map = md.getMap();

        this.isCustom = md.isCustom();
        this.ghostBase = md.getGhostBasePosition();

        //loadMap();

        pacman = new Pacman(md.getPacmanPosition().x,md.getPacmanPosition().y,this,singleplayerGame);
        addKeyListener(pacman);

        foods = new ArrayList<>();
        pufoods = new ArrayList<>();
        ghosts = new ArrayList<>();
        teleports = new ArrayList<>();

        //TODO : read food from mapData (Map 1)

        if(!isCustom) {
            for (int i = 0; i < m_x; i++) {
                for (int j = 0; j < m_y; j++) {
                    if (map[i][j] == 0)
                        foods.add(new Food(i, j));
                }
            }
        }else{
            foods = md.getFoodPositions();
        }



        pufoods = md.getPufoodPositions();

        ghosts = new ArrayList<>();
        for(GhostData gd : md.getGhostsData()){
            switch(gd.getType()) {
                case RED:
                    ghosts.add(new RedGhost(gd.getX(), gd.getY(), this));
                    break;
                case PINK:
                    ghosts.add(new PinkGhost(gd.getX(), gd.getY(), this));
                    break;
                case CYAN:
                    ghosts.add(new CyanGhost(gd.getX(), gd.getY(), this));
                    break;
                case ORANGE:
                    ghosts.add(new OrangeGhost(gd.getX(), gd.getY(), this));
                    break;
            }
        }

        teleports = md.getTeleports();

        setLayout(null);
        setSize(20*m_x,20*m_y);
        setBackground(Color.black);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        mapSegments = new Image[28];
        mapSegments[0] = null;
        for(int ms=1;ms<28;ms++){
            try {
                mapSegments[ms] = ImageIO.read(loader.getResource("images/map segments/"+ms+".png"));
            }catch(Exception e){}
        }

        pfoodImage = new Image[5];
        for(int ms=0 ;ms<5;ms++){
            try {
                pfoodImage[ms] = ImageIO.read(loader.getResource("images/food/"+ms+".png"));
            }catch(Exception e){}
        }
        try{
            foodImage = ImageIO.read(loader.getResource("images/food.png"));
            goImage = ImageIO.read(loader.getResource("images/gameover.png"));
            vicImage = ImageIO.read(loader.getResource("images/victory.png"));
            //pfoodImage = ImageIO.read(loader.getResource("/images/pfood.png"));
        }catch(Exception e){}


        redrawAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //Draw Board
                repaint();
            }
        };
        redrawTimer = new Timer(16,redrawAL);
        redrawTimer. start();

        //com.aasma2020.pacman.SoundPlayer.play("pacman_start.wav");
        siren = new LoopPlayer("siren.wav");
        pac6 = new LoopPlayer("pac6.wav");
        siren.start();
    }

    private void collisionTest(){
        Rectangle pr = new Rectangle(pacman.pixelPosition.x+13,pacman.pixelPosition.y+13,2,2);
        Ghost ghostToRemove = null;
        for(Ghost g : ghosts){
            Rectangle gr = new Rectangle(g.pixelPosition.x,g.pixelPosition.y,28,28);

            if(pr.intersects(gr)){
                if(!g.isDead()) {
                    if (!g.isWeak()) {
                        g.setLastPacmanPosition(g.getPosition());
                        //Game Over
                        /*siren.stop();
                        com.aasma2020.pacman.SoundPlayer.play("pacman_lose.wav");
                        pacman.moveTimer.stop();
                        pacman.animTimer.stop();
                        g.moveTimer.stop();
                        isGameOver = true;
                        scoreboard.setText("    Press R to try again !");*/
                        //scoreboard.setForeground(Color.red);
                        break;
                    } else {
                        //Eat com.aasma2020.pacman.Ghost
                        SoundPlayer.play("pacman_eatghost.wav");
                        //getGraphics().setFont(new Font("Arial",Font.BOLD,20));
                        drawScore = true;
                        scoreToAdd++;
                        if(ghostBase!=null)
                            g.die();
                        else
                            ghostToRemove = g;
                    }
                }
            }
        }

        if(ghostToRemove!= null){
            ghosts.remove(ghostToRemove);
        }
    }

    private void update(){

        Food foodToEat = null;
        //Check food eat
        for(Food f : foods){
            if(pacman.getPosition().distance(f.getPosition()) == 0)
                foodToEat = f;
        }
        if(foodToEat!=null) {
            SoundPlayer.play("pacman_eat.wav");
            foods.remove(foodToEat);
            score ++;
            scoreboard.setText("    Score : "+score);

            if(foods.size() == 0){
                siren.stop();
                pac6.stop();
                SoundPlayer.play("pacman_intermission.wav");
                isWin = true;
                pacman.moveTimer.stop();
                for(Ghost g : ghosts){
                    g.moveTimer.stop();
                }
            }
        }

        PowerUpFood puFoodToEat = null;
        //Check pu food eat
        for(PowerUpFood puf : pufoods){
            if(pacman.getPosition().distance(puf.getPosition()) == 0)
                puFoodToEat = puf;
        }
        if(puFoodToEat!=null) {
            //com.aasma2020.pacman.SoundPlayer.play("pacman_eat.wav");
            switch(puFoodToEat.type) {
                case 0:
                    //PACMAN 6
                    pufoods.remove(puFoodToEat);
                    siren.stop();
                    mustReactivateSiren = true;
                    pac6.start();
                    for (Ghost g : ghosts) {
                        g.weaken();
                    }
                    scoreToAdd = 0;
                    break;
                default:
                    SoundPlayer.play("pacman_eatfruit.wav");
                    pufoods.remove(puFoodToEat);
                    scoreToAdd = 1;
                    drawScore = true;
            }
            //score ++;
            //scoreboard.setText("    Score : "+score);
        }

        //Check com.aasma2020.pacman.Ghost Undie
        for(Ghost g:ghosts){
            if(g.isDead() && g.getPosition().x == ghostBase.x && g.getPosition().y == ghostBase.y){
                g.undie();
            }
        }

        //Check Teleport
        for(TeleportTunnel tp : teleports) {
            if (pacman.getPosition().x == tp.getFrom().x && pacman.getPosition().y == tp.getFrom().y && pacman.activeMove == tp.getReqMove()) {
                //System.out.println("TELE !");
                pacman.setPosition(tp.getTo());
                pacman.pixelPosition.x = pacman.getPosition().x * 28;
                pacman.pixelPosition.y = pacman.getPosition().y * 28;
            }
        }

        //Check isSiren
        boolean isSiren = true;
        for(Ghost g:ghosts){
            if(g.isWeak()){
                isSiren = false;
            }
        }
        if(isSiren){
            pac6.stop();
            if(mustReactivateSiren){
                mustReactivateSiren = false;
                siren.start();
            }

        }



    }


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //DEBUG ONLY !
        /*for(int ii=0;ii<=m_x;ii++){
            g.drawLine(ii*28+10,10,ii*28+10,m_y*28+10);
        }
        for(int ii=0;ii<=m_y;ii++){
            g.drawLine(10,ii*28+10,m_x*28+10,ii*28+10);
        }*/

        //Draw Walls
        g.setColor(Color.blue);
        for(int i=0;i<m_x;i++){
            for(int j=0;j<m_y;j++){
                if(map[i][j]>0){
                    //g.drawImage(10+i*28,10+j*28,28,28);
                    g.drawImage(mapSegments[map[i][j]],10+i*28,10+j*28,null);
                }
            }
        }

        //Draw com.aasma2020.pacman.Food
        g.setColor(new Color(204, 122, 122));
        for(Food f : foods){
            //g.fillOval(f.position.x*28+22,f.position.y*28+22,4,4);
            g.drawImage(foodImage,10+f.getPosition().x*28,10+f.getPosition().y*28,null);
        }

        //Draw PowerUpFoods
        g.setColor(new Color(204, 174, 168));
        for(PowerUpFood f : pufoods){
            //g.fillOval(f.position.x*28+20,f.position.y*28+20,8,8);
            g.drawImage(pfoodImage[f.type],10+f.getPosition().x*28,10+f.getPosition().y*28,null);
        }

        //Draw com.aasma2020.pacman.Pacman
        switch(pacman.activeMove){
            case NONE:
            case RIGHT:
                g.drawImage(pacman.getPacmanImage(),10+pacman.pixelPosition.x,10+pacman.pixelPosition.y,null);
                break;
            case LEFT:
                g.drawImage(ImageHelper.flipHor(pacman.getPacmanImage()),10+pacman.pixelPosition.x,10+pacman.pixelPosition.y,null);
                break;
            case DOWN:
                g.drawImage(ImageHelper.rotate90(pacman.getPacmanImage()),10+pacman.pixelPosition.x,10+pacman.pixelPosition.y,null);
                break;
            case UP:
                g.drawImage(ImageHelper.flipVer(ImageHelper.rotate90(pacman.getPacmanImage())),10+pacman.pixelPosition.x,10+pacman.pixelPosition.y,null);
                break;
        }

        //Draw Ghosts
        for(Ghost gh : ghosts){
            g.drawImage(gh.getGhostImage(),10+gh.pixelPosition.x,10+gh.pixelPosition.y,null);

            // Draw visibility area circle
            int imageSize = gh.getGhostImage().getWidth(null) * 2;
            g.setColor(new Color(255,255,255,32));
            g.fillOval(gh.pixelPosition.x - imageSize, gh.pixelPosition.y - imageSize, areaVisionRadius * imageSize, areaVisionRadius * imageSize);
        }

        if(clearScore){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            drawScore = false;
            clearScore =false;
        }

        if(drawScore) {
            //System.out.println("must draw score !");
            g.setFont(new Font("Arial",Font.BOLD,15));
            g.setColor(Color.yellow);
            Integer s = scoreToAdd*100;
            g.drawString(s.toString(), pacman.pixelPosition.x + 13, pacman.pixelPosition.y + 50);
            //drawScore = false;
            score += s;
            scoreboard.setText("    Score : "+score);
            clearScore = true;

        }

        if(isGameOver){
            g.drawImage(goImage,this.getSize().width/2-315,this.getSize().height/2-75,null);
        }

        if(isWin){
            g.drawImage(vicImage,this.getSize().width/2-315,this.getSize().height/2-75,null);
            if(winSeconds > 0) {
                if(winTimer == null) {
                    winTimer = new Timer(1000, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            --winSeconds;
                            if(winSeconds == 0) {
                                winTimer.stop();
                            }
                        }
                    });
                    winTimer.start();
                }
                g.setColor(Color.red);
                g.setFont(new Font("Arial",Font.BOLD,20));
                g.drawString("Starting new level in " + winSeconds + " seconds", this.getSize().width / 2 - 135, this.getSize().height / 2 + 100);
            } else {
                windowParent.newLevel();
            }
        }


    }

    @Override
    public void processEvent(AWTEvent ae){

        switch(ae.getID()) {
            case Messeges.UPDATE:
                update();
                break;
            case Messeges.COLTEST:
                if (!isGameOver) {
                    collisionTest();
                }
                break;
            case Messeges.AREATEST:
                checkArea((Agent) ae.getSource());
                break;
            case Messeges.RESET:
                if(isGameOver)
                    restart();
                break;
            default:
                super.processEvent(ae);
                break;
        }
    }

    private void checkArea(Agent agent) {

        MapAreaInfo info = new MapAreaInfo(agent.getPosition(), areaVisionRadius);

        if(!(agent instanceof Pacman)) {
            info.setPacman(pacman.getPosition());
        }

        for(Ghost ghost : ghosts) {
            if(agent instanceof Ghost && ghost.equals(agent)) {
                continue;
            }
            info.addGhost(ghost.getPosition(), ghost);
        }

        for(PowerUpFood puf : pufoods) {
            info.addPowerUpFood(puf.getPosition(), puf);
        }

        for(Food f : foods) {
            info.addFood(f.getPosition(), f);
        }

        agent.decideAndAct(info);

    }

    public void restart(){

        siren.stop();

        new PacWindow(singleplayerGame);
        windowParent.dispose();

        /*
        removeKeyListener(pacman);

        isGameOver = false;

        pacman = new com.aasma2020.pacman.Pacman(md_backup.getPacmanPosition().x,md_backup.getPacmanPosition().y,this);
        addKeyListener(pacman);

        foods = new ArrayList<>();
        pufoods = new ArrayList<>();
        ghosts = new ArrayList<>();
        teleports = new ArrayList<>();

        //TODO : read food from mapData (Map 1)

        if(!isCustom) {
            for (int i = 0; i < m_x; i++) {
                for (int j = 0; j < m_y; j++) {
                    if (map[i][j] == 0)
                        foods.add(new com.aasma2020.pacman.Food(i, j));
                }
            }
        }else{
            foods = md_backup.getFoodPositions();
        }



        pufoods = md_backup.getPufoodPositions();

        ghosts = new ArrayList<>();
        for(com.aasma2020.pacman.GhostData gd : md_backup.getGhostsData()){
            switch(gd.getType()) {
                case RED:
                    ghosts.add(new com.aasma2020.pacman.RedGhost(gd.getX(), gd.getY(), this));
                    break;
                case PINK:
                    ghosts.add(new com.aasma2020.pacman.PinkGhost(gd.getX(), gd.getY(), this));
                    break;
                case CYAN:
                    ghosts.add(new com.aasma2020.pacman.CyanGhost(gd.getX(), gd.getY(), this));
                    break;
            }
        }

        teleports = md_backup.getTeleports();
        */
    }




}
