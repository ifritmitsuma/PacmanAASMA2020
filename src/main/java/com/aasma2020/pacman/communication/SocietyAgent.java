package com.aasma2020.pacman.communication;

import com.aasma2020.pacman.board.BoardElement;

public abstract class SocietyAgent extends Agent {

    public abstract void receiveReport(SocietyAgent agent, Report report);

}
