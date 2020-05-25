package com.aasma2020.pacman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {

    private static BufferedWriter writer;

    public static void initialize() {
        try {
            if (writer != null) {
                writer.close();
            }
            writer = new BufferedWriter(new FileWriter(new File("log_" + System.currentTimeMillis() + ".txt")));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String s) {
        if(writer == null) {
            initialize();
        }
        try {
            writer.write(s);
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
