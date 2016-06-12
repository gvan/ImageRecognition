package com.gvan.geom;

import com.gvan.QrReader;

/**
 * Created by ivan on 6/11/16.
 */
public class Axis {

    private int sin, cos;
    private int modulePitch;
    private Point origin;

    public Axis(int[] angle, int modulePitch){
        this.sin = angle[0];
        this.cos = angle[1];
        this.modulePitch = modulePitch;
        this.origin = new Point();
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public void setModulePitch(int modulePitch) {
        this.modulePitch = modulePitch;
    }

    public Point translate(Point origin, int moveC, int moveR){
        setOrigin(origin);
        return translate(moveC, moveR);
    }

    public Point translate(int moveC, int moveR){
        Point point = new Point();
        int dc = moveC == 0 ? 0 : (modulePitch * moveC) >> QrReader.DECIMAL_POINT;
        int dr = moveR == 0 ? 0 : (modulePitch * moveR) >> QrReader.DECIMAL_POINT;
        point.translate((dc*cos - dr*sin) >> QrReader.DECIMAL_POINT, (dc*sin + dr*cos) >> QrReader.DECIMAL_POINT);
        point.translate(origin.getX(), origin.getY());
        return point;
    }

}
