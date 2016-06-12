package com.gvan.geom;

import com.gvan.Utils;

/**
 * Created by ivan on 6/9/16.
 */
public class Line {

    private int x1, y1, x2, y2;
    private Point p1, p2;

    public Line(){
        x1 = y1 = x2 = y2 = 0;
        p1 = new Point(x1, y1);
        p2 = new Point(x2, y2);
    }

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }

    public Line(Point p1, Point p2){
        this.x1 = p1.getX();
        this.y1 = p1.getY();
        this.x2 = p2.getX();
        this.y2 = p2.getY();
        this.p1 = p1;
        this.p2 = p2;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s) - (%s, %s)", x1, y1, x2, y2);
    }

    public static boolean isNeighbor(Line line1, Line line2){
        return (Math.abs(line1.getP1().getX() - line2.getP1().getX())) < 3 &&
                (Math.abs(line1.getP1().getY() - line2.getP1().getY())) < 3 &&
                (Math.abs(line1.getP2().getX() - line2.getP2().getX())) < 3 &&
                (Math.abs(line1.getP2().getY() - line2.getP2().getY())) < 3;
    }

    public int getLength(){
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        return Utils.sqrt(dx*dx + dy*dy);
    }

    public Point getCenter(){
        int c = (x1 + x2) / 2;
        int r = (y1 + y2) / 2;
        return new Point(c, r);
    }

    public boolean isHorizontal(){
        return y1 == y2;
    }

    public boolean isVertical(){
        return x1 == x2;
    }

    public static boolean isCross(Line line1, Line line2){
        if(line1.isHorizontal() && line2.isVertical()){
            if(line1.getP1().getY() > line2.getP1().getY() &&
                    line1.getP1().getY() < line2.getP2().getY() &&
                    line2.getP1().getX() > line1.getP1().getX() &&
                    line2.getP1().getX() < line1.getP2().getX())
                return true;
        } else
        if(line1.isVertical() && line2.isHorizontal()){
            if(line1.getP1().getX() > line2.getP1().getX() &&
                    line1.getP1().getX() < line2.getP2().getX() &&
                    line2.getP1().getY() > line1.getP1().getY() &&
                    line2.getP1().getY() < line1.getP2().getY())
                return true;
        }
        return false;
    }

    public static Line getLongest(Line[] lines){
        Line longestLine = new Line();
        for(int i = 0;i < lines.length;i++){
            if(lines[i].getLength() > longestLine.getLength())
                longestLine = lines[i];
        }
        return longestLine;
    }

}
