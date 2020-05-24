package com.aasma2020.pacman;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public abstract class WandererGhost extends Ghost {

    public WandererGhost(int x, int y, PacBoard pb, int ghostDelay) {
        super(x, y, pb, ghostDelay);
    }

    @Override
    public moveType getMoveAI(){
        if(isPending){
            if(isStuck){
                if(pendMove == moveType.UP){
                    pendMove = moveType.DOWN;
                }else if(pendMove == moveType.DOWN){
                    pendMove = moveType.UP;
                }
                return pendMove;
            }else{
                return pendMove;
            }
        }
        if(bfs==null)
            bfs = new BFSFinder(parentBoard);
        if(isDead) {
            return baseReturner.getMove(position.x, position.y, parentBoard.ghostBase.x,parentBoard.ghostBase.y);
        }else{
            if(pacmanPosition != null && pacmanPosition.isValid()) {
                moveType move = bfs.getMove(position.x, position.y, pacmanPosition.x, pacmanPosition.y);
                if(moveType.NONE != move) {
                    return move;
                } else {
                    pacmanPosition.setValid(false);
                }
            }
            if (lastCMove == null || isStuck) {
                ArrayList<moveType> pm = getPossibleMoves();
                int i = ThreadLocalRandom.current().nextInt(pm.size());
                lastCMove = pm.get(i);
            }
            return lastCMove;
        }
    }
}
