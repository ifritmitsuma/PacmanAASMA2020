package com.aasma2020.pacman.ui;

import com.aasma2020.pacman.log.Statistic;
import com.aasma2020.pacman.map.MapEditor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class StartWindow extends JFrame {

    public StartWindow() {
        setSize(600, 400);
        getContentPane().setBackground(Color.black);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ImageIcon logo = new ImageIcon();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            logo = new ImageIcon(ImageIO.read(loader.getResource("images/pacman_logo.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Register Custom fonts
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, loader.getResourceAsStream("fonts/crackman.ttf")));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        getContentPane().add(new JLabel(logo), BorderLayout.NORTH);

        JPanel buttonsC = new JPanel();
        buttonsC.setBackground(Color.black);
        //buttonsC.setLayout(new FlowLayout(FlowLayout.LEADING,20,10));
        buttonsC.setLayout(new BoxLayout(buttonsC, BoxLayout.Y_AXIS));
        FansyButton startButton = new FansyButton("Singleplayer Game");
        FansyButton aiButton = new FansyButton("AI Game");
        FansyButton customButton = new FansyButton("Customize Game");

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        customButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PacWindow pw = new PacWindow(true);
                dispose();
            }
        });

        aiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Statistic.initialize();
                PacWindow pw = new PacWindow(false);
                dispose();
            }
        });

        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MapEditor me = new MapEditor();
                dispose();
            }
        });

        buttonsC.add(startButton);
        buttonsC.add(aiButton);
        buttonsC.add(customButton);

        getContentPane().add(buttonsC);

        setVisible(true);
    }

}
