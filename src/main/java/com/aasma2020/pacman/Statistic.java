package com.aasma2020.pacman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Statistic {


    private static BufferedWriter writer;

    public static void initialize() {
        try {
            if (writer != null) {
                writer.close();
            }
            writer = new BufferedWriter(new FileWriter(new File("log_" + System.currentTimeMillis() + ".csv")));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(int level, String winner, long seconds) {
        if(writer == null) {
            initialize();
        }
        try {
            writer.write("" + level + ";" + winner + ";" + seconds);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if(writer == null) {
            return;
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
