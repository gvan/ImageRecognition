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

    public Point translate(Point origin, int moveX, int moveY){
        setOrigin(origin);
        return translate(moveX, moveY);
    }

    public Point translate(int moveX, int moveY){
        Point point = new Point();
        int dx = moveX == 0 ? 0 : (modulePitch * moveX) >> QrReader.DECIMAL_POINT;
        int dy = moveY == 0 ? 0 : (modulePitch * moveY) >> QrReader.DECIMAL_POINT;
        point.translate((dx*cos - dy*sin) >> QrReader.DECIMAL_POINT, (dx*sin + dy*cos) >> QrReader.DECIMAL_POINT);
        point.translate(origin.getX(), origin.getY());
        return point;
    }

}
