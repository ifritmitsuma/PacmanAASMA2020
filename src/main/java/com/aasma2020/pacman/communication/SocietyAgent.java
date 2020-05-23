package com.aasma2020.pacman.communication;

public interface SocietyAgent {

    public void sendReport(SocietyAgent agent);

    public void receiveReport(Report report);

}
