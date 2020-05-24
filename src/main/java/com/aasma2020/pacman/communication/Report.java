package com.aasma2020.pacman.communication;

import com.aasma2020.pacman.board.Position;
import com.aasma2020.pacman.moveType;

public class Report {

    private Position pacmanPosition;

    private moveType moveIntention;

    public Position getPacmanPosition() {
        return pacmanPosition;
    }

    public void setPacmanPosition(Position pacmanPosition) {
        this.pacmanPosition = pacmanPosition;
    }

    public moveType getMoveIntention() {
        return moveIntention;
    }

    public void setMoveIntention(moveType moveIntention) {
        this.moveIntention = moveIntention;
    }

}
