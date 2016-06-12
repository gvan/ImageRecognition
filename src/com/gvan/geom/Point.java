package com.gvan.geom;

/**
 * Created by ivan on 5/30/16.
 */
public class Point {

    private int x;//its row, first parameter in point
    private int y;//its column, second parameter in point

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int dx, int dy){
        this.x += dx;
        this.y += dy;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equeals(Point point){
        return x == point.x && y == point.y;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }

}
