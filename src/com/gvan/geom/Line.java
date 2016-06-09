package com.gvan.geom;

/**
 * Created by ivan on 6/9/16.
 */
public class Line {

    int c1, r1, c2, r2;

    public Line(int c1, int r1, int c2, int r2) {
        this.c1 = c1;
        this.r1 = r1;
        this.c2 = c2;
        this.r2 = r2;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s) - (%s, %s)", c1, r1, c2, r2);
    }
}
