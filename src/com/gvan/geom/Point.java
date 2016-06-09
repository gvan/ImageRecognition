package com.gvan.geom;

/**
 * Created by ivan on 5/30/16.
 */
public class Point {

    public int c;//its column
    public int r;//its row

    public Point() {
        this.c = 0;
        this.r = 0;
    }

    public Point(int c, int r) {
        this.c = c;
        this.r = r;
    }

    public void translate(int dc, int dr){
        this.c += dc;
        this.r += dr;
    }

    public void set(int c, int r) {
        this.c = c;
        this.r = r;
    }
}
