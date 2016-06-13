package com.gvan;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        String fileName = "res/qr.pgm";
        String outFileName = "res/output.pgm";

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(420,420);
        frame.setVisible(true);
        ImagePanel imagePanel = new ImagePanel();
        frame.add(imagePanel);

        QrReader.read(fileName, imagePanel);

    }

}
