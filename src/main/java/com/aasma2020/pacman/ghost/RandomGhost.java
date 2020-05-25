package com.aasma2020.pacman.ghost;

import com.aasma2020.pacman.bfs.BFSFinder;
import com.aasma2020.pacman.board.PacBoard;
import com.aasma2020.pacman.board.moveType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RandomGhost extends Ghost {

    public RandomGhost(int x, int y, PacBoard pb, int ghostDelay) {
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
        }else {
            if(pacmanPosition != null && pacmanPosition.isValid()) {
                moveType move = moveType.NONE;
                // Let's make it just a bit random (40% random)
                double randFactor = ThreadLocalRandom.current().nextDouble(1.0);
                if(randFactor < 0.4) {
                    List<moveType> possibleMoves = getPossibleMoves();
                    if(((isWeak() && position.x <= pacmanPosition.x) || (!isWeak() && position.x >= pacmanPosition.x))
                            && possibleMoves.contains(moveType.LEFT)) {
                        move = moveType.LEFT;
                    }
                    if(((isWeak() && position.x >= pacmanPosition.x) || (!isWeak() && position.x <= pacmanPosition.x))
                            && possibleMoves.contains(moveType.RIGHT)) {
                        move = moveType.RIGHT;
                    }
                    if(((isWeak() && position.y >= pacmanPosition.y) || (!isWeak() && position.y <= pacmanPosition.y))
                            && possibleMoves.contains(moveType.DOWN)) {
                        move = moveType.DOWN;
                    }
                    if(((isWeak() && position.y <= pacmanPosition.y) || (!isWeak() && position.y >= pacmanPosition.y))
                            && possibleMoves.contains(moveType.UP)) {
                        move = moveType.UP;
                    }
                    return move;
                } else {
                    move = bfs.getMove(position.x, position.y, pacmanPosition.x, pacmanPosition.y);
                    if (moveType.NONE != move) {
                        return move;
                    } else {
                        pacmanPosition.setValid(false);
                    }
                }
            }
            ArrayList<moveType> pm = getPossibleMoves();
            int i = ThreadLocalRandom.current().nextInt(pm.size());
            lastCMove = pm.get(i);
            return lastCMove;
        }
    }

}
