package com.aasma2020.pacman.communication;

import com.aasma2020.pacman.Food;
import com.aasma2020.pacman.Ghost;
import com.aasma2020.pacman.Pacman;
import com.aasma2020.pacman.PowerUpFood;
import com.aasma2020.pacman.board.BoardElement;

public abstract class Agent extends BoardElement {

    public abstract void decideAndAct(MapAreaInfo info);

}
