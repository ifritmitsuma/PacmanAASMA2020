package com.aasma2020.pacman.communication;

import com.aasma2020.pacman.board.BoardElement;

public abstract class Agent extends BoardElement {

    public abstract void decideAndAct(MapAreaInfo info);

}
