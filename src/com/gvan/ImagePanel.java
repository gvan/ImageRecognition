package com.gvan;

import com.gvan.geom.*;
import com.gvan.geom.Point;
import com.gvan.util.DebugCanvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ivan on 6/13/16.
 */
public class ImagePanel extends JPanel implements DebugCanvas{

    BufferedImage image;
    ArrayList<Line> lines = new ArrayList<Line>();
    ArrayList<Point> points = new ArrayList<Point>();

    public ImagePanel() {
        try {
            image = ImageIO.read(new File("res/qrcode1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(image, 0, 0, null);

        g2d.setColor(new Color(255, 0, 0, 255));
        g2d.setStroke(new BasicStroke(1));
        for(Line line : lines) {
            g2d.drawLine(line.getP1().getY(), line.getP1().getX(), line.getP2().getY(), line.getP2().getX());
        }
        for(Point point : points){
            g2d.fillOval(point.getY() - 4, point.getX() - 4, 8, 8);
        }
    }

    @Override
    public void drawLine(Line line) {
        lines.add(line);
        repaint();
    }

    @Override
    public void drawPoint(Point point) {
        points.add(point);
        repaint();
    }
}
