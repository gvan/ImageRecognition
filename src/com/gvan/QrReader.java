package com.gvan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ivan on 5/28/16.
 */
public class QrReader {

    private Point fp1;
    private Point fp2;
    private Point fp3;
    private int fSide = 0;

    public void recognizeFindPatterns(Image image){
        List<Points> pointses = new ArrayList<Points>();
        for(int i = 0;i < image.height;i++){
            int rPrevPixel = image.matrix[i][0];
            List<Integer> rLengths = new ArrayList<Integer>();
            rLengths.add(0);//for first black
            if(rPrevPixel == 0)
                rLengths.set(0, 1);
            else
                rLengths.add(0);
            for(int c = 1;c < image.width;c++){
                int rPixel = image.matrix[i][c];
                if(rPixel != rPrevPixel)
                    rLengths.add(0);
                rLengths.set(rLengths.size() - 1, rLengths.get(rLengths.size() - 1) + 1);
                rPrevPixel = rPixel;
            }
            if(rLengths.size() > 5){
                for(int j = 2;j < rLengths.size() - 2;j++){
                    if(j % 2 == 1) continue;
                    int rL1 = rLengths.get(j - 2);
                    int rL2 = rLengths.get(j - 1);
                    int rL3 = rLengths.get(j);
                    int rL4 = rLengths.get(j + 1);
                    int rL5 = rLengths.get(j + 2);
                    if(Math.round((float)rL1/rL2) == 1 && Math.round((float)rL3/rL2) == 3 &&
                            Math.round((float)rL4/rL5) == 1 && Math.round((float)rL3/rL4) == 3){
                        int cols = 0;
                        for(int l = 0;l < j;l++)
                            cols += rLengths.get(l);
                        cols += rLengths.get(j)/2;
                        int cPrevPixel = image.matrix[0][cols];
                        List<Integer> cLengths = new ArrayList<Integer>();
                        cLengths.add(0);
                        if(cPrevPixel == 0)
                            cLengths.set(0, 1);
                        else
                            cLengths.add(0);
                        for(int r = 1;r < image.height;r++){
                            int cPixel = image.matrix[r][cols];
                            if(cPixel != cPrevPixel)
                                cLengths.add(0);
                            cLengths.set(cLengths.size() - 1, cLengths.get(cLengths.size() - 1) + 1);
                            cPrevPixel = cPixel;
                        }
                        if(cLengths.size() > 5){
                            for(int k = 2;k  < cLengths.size() - 2;k++){
                                if(k % 2 == 1) continue;
                                int cL1 = cLengths.get(k - 2);
                                int cL2 = cLengths.get(k - 1);
                                int cL3 = cLengths.get(k);
                                int cL4 = cLengths.get(k + 1);
                                int cL5 = cLengths.get(k + 2);
                                if(Math.round((float)cL1/cL2) == 1 && Math.round((float)cL3/cL1) == 3 &&
                                        Math.round((float)cL4/cL5) == 1 && Math.round((float)cL3/cL4) == 3){
//                                    Utils.log("find pattern i=%s j=%s", i, cols);
                                    Point point = new Point(cols, i);
                                    if(pointses.size() == 0){
                                        Points points = new Points();
                                        points.addPoint(point);
                                        pointses.add(points);
                                    } else {
                                        boolean pointAdded = false;
                                        for(Points points : pointses){
                                            if(points.isNear(point)) {
                                                points.addPoint(point);
                                                pointAdded = true;
                                            }
                                        }
                                        if(!pointAdded){
                                            Points points = new Points();
                                            points.addPoint(point);
                                            pointses.add(points);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if(pointses.size() > 2){
            pointses.sort(new Comparator<Points>() {
                @Override
                public int compare(Points o1, Points o2) {
                    if(o1.size() < o2.size())
                        return 1;
                    if(o1.size() > o2.size())
                        return -1;
                    return 0;
                }
            });
            fp1 = pointses.get(0).getAvPoint();
            fp2 = pointses.get(1).getAvPoint();
            fp3 = pointses.get(2).getAvPoint();

            fSide = 0;
            fSide += pointses.get(0).getHeight();
            fSide += pointses.get(1).getHeight();
            fSide += pointses.get(2).getHeight();
            fSide /= 3;

            Utils.log("fp1 : %s, %s; fp2: %s, %s; fp3: %s, %s", fp1.c, fp1.r, fp2.c, fp2.r, fp3.c, fp3.r);
            Utils.log("fSide %s", fSide);
        }
    }

    private class Points{

        private List<Point> points;

        public Points() {
            this.points = new ArrayList<Point>();
        }

        public boolean isNear(Point point){
            for(Point pointLocal : points){
                if(Math.abs(pointLocal.c - point.c) < 2 &&
                        Math.abs(pointLocal.r - point.r) < 2){
                    return true;
                }
            }
            return false;
        }

        public void addPoint(Point point){
            this.points.add(point);
        }

        public int size(){
            return points.size();
        }

        public Point getAvPoint(){
            int c = 0, r = 0;
            for(Point point : points){
                c += point.c;
                r += point.r;
            }
            c /= points.size();
            r /= points.size();
            return new Point(c, r);
        }

        public int getHeight(){
            return Math.abs(points.get(points.size() - 1).r - points.get(0).r);
        }

    }

}
