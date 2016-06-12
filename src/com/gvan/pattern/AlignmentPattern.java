package com.gvan.pattern;

import com.gvan.Const;
import com.gvan.Utils;
import com.gvan.geom.Axis;
import com.gvan.geom.Point;

/**
 * Created by ivan on 6/11/16.
 */
public class AlignmentPattern {

    private Point[][] centers;
    int pattersDistance;

    public AlignmentPattern(Point[][] centers, int pattersDistance) {
        this.centers = centers;
        this.pattersDistance = pattersDistance;
    }

    public Point[][] getCenters() {
        return centers;
    }

    public int getPattersDistance() {
        return pattersDistance;
    }

    public static AlignmentPattern findAlignmentPattern(boolean[][] image, FindPattern findPattern){
        Point[][] logicalCenters = getLogicalCenter(findPattern.getVersion());
        int logicalDistance = logicalCenters[1][0].getX() - logicalCenters[0][0].getX();
        Point[][] centers = getCenter(image, findPattern, logicalCenters);

        for (Point[] points : centers)
            for (Point point : points)
                Utils.log(point.toString());
        return new AlignmentPattern(centers, logicalDistance);
    }

    public static Point[][] getLogicalCenter(int version){
        int[] logicalSeeds = LogicalSeed.getSeed(version);
        Point[][] logicalCenters = new Point[logicalSeeds.length][logicalSeeds.length];

        for(int c = 0;c < logicalCenters.length;c++){
            for(int r = 0;r < logicalCenters.length;r++){
                logicalCenters[r][c] = new Point(logicalSeeds[r], logicalSeeds[c]);
            }
        }
        return logicalCenters;
    }

    public static Point[][] getCenter(boolean[][] image, FindPattern findPattern, Point[][] logicalCenters){
        Axis axis = new Axis(findPattern.getSincos(), findPattern.getModuleSize());
        int sqrtCenters = logicalCenters.length;
        Point[][] centers = new Point[sqrtCenters][sqrtCenters];

        axis.setOrigin(findPattern.getThreePoints().getCenter(Const.UL));
        centers[0][0] = axis.translate(3, 3);

        axis.setOrigin(findPattern.getThreePoints().getCenter(Const.UR));
        centers[sqrtCenters - 1][0] = axis.translate(-3, 3);

        axis.setOrigin(findPattern.getThreePoints().getCenter(Const.DL));
        centers[0][sqrtCenters - 1] = axis.translate(3, -3);

        for(int y = 0;y < sqrtCenters;y++){
            for(int x = 0;x < sqrtCenters;x++){
                if((x == 0 && y == 0) || (x == 0 && y == sqrtCenters-1) || (x == sqrtCenters-1 && y == 0)){
                    continue;
                }
                Point target;
                if(y == 0){
                    if(x > 0 && x < sqrtCenters - 1){
                        target = axis.translate(centers[x-1][y], logicalCenters[x][y].getX() - logicalCenters[x - 1][y].getX(),0);
                        centers[x][y] = new Point(target.getX(), target.getY());
                    }
                } else
                if(x == 0){
                    if(y > 0 && y < sqrtCenters - 1){
                        target = axis.translate(centers[x][y - 1], 0, logicalCenters[x][y].getY() - logicalCenters[x][y - 1].getY());
                        centers[x][y] = new Point(target.getX(), target.getY());
                    }
                } else {
                    Point t1 = axis.translate(centers[x - 1][y], logicalCenters[x][y].getX() - logicalCenters[x - 1][y].getX(), 0);
                    Point t2 = axis.translate(centers[x][y - 1], 0, logicalCenters[x][y].getY() - logicalCenters[x][y - 1].getY());
                    centers[x][y] = new Point((t1.getX() + t2.getX())/2, (t1.getY() + t2.getY()) / 2 + 1);
                }

                if(findPattern.getVersion() > 1){
                    Point precisionCenter = getPrecisionCenter(image, centers[x][y]);
                    if(precisionCenter != null)
                        centers[x][y] = precisionCenter;
                }
            }
        }

        return centers;
    }

    private static Point getPrecisionCenter(boolean[][] image, Point targetPoint){
        if(image[targetPoint.getX()][targetPoint.getY()] == Const.FRONT){
            int scope = 0;
            boolean found = false;
            while (!found){
                scope++;
                for(int dy = scope;dy > -scope;dy--){
                    for(int dx = scope;dx > -scope;dx--){
                        int x = targetPoint.getX() + dx;
                        int y = targetPoint.getY() + dy;
                        if((x < 0 || y < 0) || (x > image.length - 1 || y > image[0].length - 1))
                            return null;
                        if(image[x][y] == Const.BACK){
                            targetPoint = new Point(targetPoint.getX() + dx, targetPoint.getY() + dy);
                            found = true;
                        }
                    }
                }
            }
        }
        int x, lx, rx, y, uy, dy;
        x = lx = rx = targetPoint.getX();
        y = uy = dy = targetPoint.getY();

        while (lx >= 1                  && targetPointOnTheCorner(image, lx, y, lx -1, y)) lx--;
        while (rx < image.length - 1    && targetPointOnTheCorner(image, rx, y, rx + 1, y)) rx++;
        while (uy >= 1                  && targetPointOnTheCorner(image, x, uy, x, uy - 1)) uy--;
        while (dy < image[0].length - 1 && targetPointOnTheCorner(image, x, dy, x, dy + 1)) dy++;

        return new Point((lx + rx + 1) / 2,(uy + dy + 1) / 2);
    }

    private static boolean targetPointOnTheCorner(boolean[][] image, int x, int y, int nx, int ny){
        return image[x][y] == Const.FRONT && image[nx][ny] == Const.BACK;
    }

}
