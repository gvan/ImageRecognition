package com.gvan.geom;

import com.gvan.Const;

/**
 * Created by ivan on 6/11/16.
 */
public class ThreePoints{

    private Point[] centers = new Point[3];

    public ThreePoints(Point p1, Point p2, Point p3) {
        centers[0] = p1;
        centers[1] = p2;
        centers[2] = p3;
    }

    public Point[] getCenters() {
        return centers;
    }

    public Point getCenter(int i){
        return centers[i];
    }

    @Override
    public String toString() {
        return String.format("ul %s, ur %s, dl %s", centers[0].toString(), centers[1].toString(), centers[2].toString());
    }

    //sort to ul, ur, dl
    public void sort(int[] angle){
        Point[] sortedCenters = new Point[3];

        int n1 = 1;
        int n2 = 2;
        switch (getQuadrant(angle)){
            case 1:{
                sortedCenters[n1] = getPointAtSide(centers, Const.RIGHT, Const.BOTTOM);
                sortedCenters[n2] = getPointAtSide(centers, Const.BOTTOM, Const.LEFT);
                break;
            }
            case 2:{
                sortedCenters[n1] = getPointAtSide(centers, Const.BOTTOM, Const.LEFT);
                sortedCenters[n2] = getPointAtSide(centers, Const.TOP, Const.LEFT);
                break;
            }
            case 3:{
                sortedCenters[n1] = getPointAtSide(centers, Const.LEFT, Const.TOP);
                sortedCenters[n2] = getPointAtSide(centers, Const.RIGHT, Const.TOP);
                break;
            }
            case 4:{
                sortedCenters[n1] = getPointAtSide(centers, Const.TOP, Const.RIGHT);
                sortedCenters[n2] = getPointAtSide(centers, Const.BOTTOM, Const.RIGHT);
                break;
            }
        }

        for(Point center : centers){
            if(!center.equeals(sortedCenters[1]) && !center.equeals(sortedCenters[2]))
                sortedCenters[0] = center;
        }

        centers = sortedCenters;
    }
    
    private int getQuadrant(int[] angle){
        int sin = angle[0];
        int cos = angle[1];
        if(sin >= 0 && cos > 0)
            return 1;
        else
        if(sin > 0 && cos <= 0)
            return 2;
        else
        if(sin <= 0 && cos < 0)
            return 3;
        else
        if(sin < 0 && cos >= 0)
            return 4;
        return 0;
    }

    static Point getPointAtSide(Point[] points, int side1, int side2) {
        Point sidePoint;
        int x = ((side1 == Const.RIGHT || side2 == Const.RIGHT) ? 0 : Integer.MAX_VALUE);
        int y = ((side1 == Const.BOTTOM || side2 == Const.BOTTOM) ? 0 : Integer.MAX_VALUE);
        sidePoint = new Point(x, y);

        for (int i = 0; i < points.length; i++) {
            switch (side1) {
                case Const.RIGHT:
                    if (sidePoint.getX() < points[i].getX()) {
                        sidePoint = points[i];
                    }
                    else if (sidePoint.getX() == points[i].getX()) {
                        if (side2 == Const.BOTTOM) {
                            if (sidePoint.getY() < points[i].getY()) {
                                sidePoint = points[i];
                            }
                        }
                        else {
                            if (sidePoint.getY() > points[i].getY()) {
                                sidePoint = points[i];
                            }
                        }
                    }
                    break;
                case Const.BOTTOM:
                    if (sidePoint.getY() < points[i].getY()) {
                        sidePoint = points[i];
                    }
                    else if (sidePoint.getY() == points[i].getY()) {
                        if (side2 == Const.RIGHT) {
                            if (sidePoint.getX() < points[i].getX()) {
                                sidePoint = points[i];
                            }
                        }
                        else {
                            if (sidePoint.getX() > points[i].getX()) {
                                sidePoint = points[i];
                            }
                        }
                    }
                    break;
                case Const.LEFT:
                    if (sidePoint.getX() > points[i].getX()) {
                        sidePoint = points[i];
                    }
                    else if (sidePoint.getX() == points[i].getX()) {
                        if (side2 == Const.BOTTOM) {
                            if (sidePoint.getY() < points[i].getY()) {
                                sidePoint = points[i];
                            }
                        }
                        else {
                            if (sidePoint.getY() > points[i].getY()) {
                                sidePoint = points[i];
                            }
                        }
                    }
                    break;
                case Const.TOP:
                    if (sidePoint.getY() > points[i].getY()) {
                        sidePoint = points[i];
                    }
                    else if (sidePoint.getY() == points[i].getY()) {
                        if (side2 == Const.RIGHT) {
                            if (sidePoint.getX() < points[i].getX()) {
                                sidePoint = points[i];
                            }
                        }
                        else {
                            if (sidePoint.getX() > points[i].getX()) {
                                sidePoint = points[i];
                            }
                        }
                    }
                    break;
            }
        }
        return sidePoint;
    }
    
}
