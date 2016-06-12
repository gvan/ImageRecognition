package com.gvan;

import com.gvan.geom.Axis;
import com.gvan.geom.Line;
import com.gvan.geom.Point;
import com.gvan.pattern.AlignmentPattern;
import com.gvan.pattern.FindPattern;

/**
 * Created by ivan on 6/12/16.
 */
public class SamplingGrid {
    
    private AreaGrid[][] grid;

    public SamplingGrid(int sqrtNumArea) {
        grid = new AreaGrid[sqrtNumArea][sqrtNumArea];
    }

    public void initGrid(int ax, int ay, int width, int height){
        grid[ax][ay] = new AreaGrid(width, height);
    }

    public void setXLine(int ax, int ay, int x, Line line){
        grid[ax][ay].setXLine(x, line);
    }

    public void setYLine(int ax, int ay, int y, Line line){
        grid[ax][ay].setYLine(y, line);
    }

    public Line getXLine(int ax, int ay, int x){
        return grid[ax][ay].getXLine(x);
    }

    public Line getYLine(int ax, int ay, int y){
        return grid[ax][ay].getYLine(y);
    }

    public int getTotalWidth(){
        int total = 0;
        for(int i = 0;i < grid.length;i++){
            total += grid[i][0].getWidth();
            if(i > 0) total -= 1;
        }
        return total;
    }

    public int getHeight(){
        return grid.length;
    }

    public int getWidth(){
        return grid[0].length;
    }

    public int getHeight(int ax, int ay){
        return grid[ax][ay].getHeight();
    }

    public int getWidth(int ax, int ay){
        return grid[ax][ay].getWidth();
    }

    public int getX(int ax, int x){
        int total = x;
        for(int i = 0;i < ax;i++){
            total += grid[i][0].getWidth() - 1;
        }
        return total;
    }

    public int getY(int ay, int y){
        int total = y;
        for(int i = 0;i < ay;i++){
            total += grid[0][i].getHeight() - 1;
        }
        return total;
    }

    public static SamplingGrid getSamplingGrid(FindPattern findPattern, AlignmentPattern alignmentPattern){
        Point[][] centers = alignmentPattern.getCenters();
        int version = findPattern.getVersion();
        int sqrtCenters = (version / 7) + 2;
        
        centers[0][0] = findPattern.getThreePoints().getCenter(Const.UL);
        centers[sqrtCenters - 1][0] = findPattern.getThreePoints().getCenter(Const.UR);
        centers[0][sqrtCenters - 1] = findPattern.getThreePoints().getCenter(Const.DL);
        
        int sqrtNumArea = sqrtCenters - 1;
        SamplingGrid samplingGrid = new SamplingGrid(sqrtNumArea);
        Line baseLineC, baseLineR, gridLineC, gridLineR;
        
        Axis axis = new Axis(findPattern.getSincos(), findPattern.getModuleSize());
        ModulePitch modulePitch;

        for (int ay = 0; ay < sqrtNumArea; ay++) {
            for (int ax = 0; ax < sqrtNumArea; ax++) {
                modulePitch = new ModulePitch();  /// Housing to order
                baseLineC = new Line();
                baseLineR = new Line();
                axis.setModulePitch(findPattern.getModuleSize());

                Point logicalCenters[][]= AlignmentPattern.getLogicalCenter(findPattern.getVersion());

                Point upperLeftPoint	=	centers[ax][ay];
                Point upperRightPoint	=	centers[ax+1][ay];
                Point lowerLeftPoint	=	centers[ax][ay+1];
                Point lowerRightPoint	=	centers[ax+1][ay+1];

                Point logicalUpperLeftPoint	=	logicalCenters[ax][ay];
                Point logicalUpperRightPoint	=	logicalCenters[ax+1][ay];
                Point logicalLowerLeftPoint	=	logicalCenters[ax][ay+1];
                Point logicalLowerRightPoint	=	logicalCenters[ax+1][ay+1];

                // left upper corner
                if(ax==0 && ay==0) {
                    if (sqrtNumArea == 1) {
                        upperLeftPoint = axis.translate(upperLeftPoint,-3,-3);
                        upperRightPoint = axis.translate(upperRightPoint,3,-3);
                        lowerLeftPoint = axis.translate(lowerLeftPoint,-3,3);
                        lowerRightPoint = axis.translate(lowerRightPoint,6,6);

                        logicalUpperLeftPoint.translate(-6,-6);
                        logicalUpperRightPoint.translate(3,-3);
                        logicalLowerLeftPoint.translate(-3,3);
                        logicalLowerRightPoint.translate(6,6);
                    }
                    else {
                        upperLeftPoint = axis.translate(upperLeftPoint,-3,-3);
                        upperRightPoint = axis.translate(upperRightPoint,0,-6);
                        lowerLeftPoint = axis.translate(lowerLeftPoint,-6,0);

                        logicalUpperLeftPoint.translate(-6,-6);
                        logicalUpperRightPoint.translate(0,-6);
                        logicalLowerLeftPoint.translate(-6,0);
                    }
                } else
                // left bottom corner
                if(ax==0 && ay==sqrtNumArea-1) {
                    upperLeftPoint = axis.translate(upperLeftPoint,-6,0);
                    lowerLeftPoint = axis.translate(lowerLeftPoint,-3,3);
                    lowerRightPoint = axis.translate(lowerRightPoint, 0, 6);


                    logicalUpperLeftPoint.translate(-6,0);
                    logicalLowerLeftPoint.translate(-6,6);
                    logicalLowerRightPoint.translate(0,6);
                } else
                // right upper corner
                if(ax==sqrtNumArea-1 && ay==0) {
                    upperLeftPoint = axis.translate(upperLeftPoint,0,-6);
                    upperRightPoint = axis.translate(upperRightPoint,3,-3);
                    lowerRightPoint = axis.translate(lowerRightPoint,6,0);

                    logicalUpperLeftPoint.translate(0,-6);
                    logicalUpperRightPoint.translate(6,-6);
                    logicalLowerRightPoint.translate(6,0);
                } else
                // right bottom corner
                if(ax==sqrtNumArea-1 && ay==sqrtNumArea-1) {
                    lowerLeftPoint = axis.translate(lowerLeftPoint,0,6);
                    upperRightPoint = axis.translate(upperRightPoint,6,0);
                    lowerRightPoint = axis.translate(lowerRightPoint,6,6);

                    logicalLowerLeftPoint.translate(0,6);
                    logicalUpperRightPoint.translate(6,0);
                    logicalLowerRightPoint.translate(6,6);
                } else
                // left side
                if(ax==0) {
                    upperLeftPoint = axis.translate(upperLeftPoint,-6,0);
                    lowerLeftPoint = axis.translate(lowerLeftPoint,-6,0);

                    logicalUpperLeftPoint.translate(-6,0);
                    logicalLowerLeftPoint.translate(-6,0);

                } else
                // right
                if(ax==sqrtNumArea-1) {
                    upperRightPoint = axis.translate(upperRightPoint,6,0);
                    lowerRightPoint = axis.translate(lowerRightPoint,6,0);

                    logicalUpperRightPoint.translate(6,0);
                    logicalLowerRightPoint.translate(6,0);
                } else
                // top
                if(ay==0) {
                    upperLeftPoint = axis.translate(upperLeftPoint,0,-6);
                    upperRightPoint = axis.translate(upperRightPoint,0,-6);

                    logicalUpperLeftPoint.translate(0,-6);
                    logicalUpperRightPoint.translate(0,-6);

                } else
                // bottom
                if(ay==sqrtNumArea-1) {
                    lowerLeftPoint = axis.translate(lowerLeftPoint,0,6);
                    lowerRightPoint = axis.translate(lowerRightPoint,0,6);

                    logicalLowerLeftPoint.translate(0,6);
                    logicalLowerRightPoint.translate(0,6);
                }

                if(ax==0) {
                    logicalUpperRightPoint.translate(1,0);
                    logicalLowerRightPoint.translate(1,0);
                } else {
                    logicalUpperLeftPoint.translate(-1,0);
                    logicalLowerLeftPoint.translate(-1,0);
                }

                if(ay==0) {
                    logicalLowerLeftPoint.translate(0,1);
                    logicalLowerRightPoint.translate(0,1);
                } else {
                    logicalUpperLeftPoint.translate(0,-1);
                    logicalUpperRightPoint.translate(0,-1);
                }

                int logicalWidth=logicalUpperRightPoint.getX()-logicalUpperLeftPoint.getX();
                int logicalHeight=logicalLowerLeftPoint.getY()-logicalUpperLeftPoint.getY();

                if (version < 7) {
                    logicalWidth += 3;
                    logicalHeight += 3;

                }
                modulePitch.top = getAreaModulePitch(upperLeftPoint, upperRightPoint, logicalWidth-1);
                modulePitch.left = getAreaModulePitch(upperLeftPoint, lowerLeftPoint, logicalHeight-1);
                modulePitch.bottom = getAreaModulePitch(lowerLeftPoint, lowerRightPoint, logicalWidth-1);
                modulePitch.right = getAreaModulePitch(upperRightPoint, lowerRightPoint, logicalHeight-1);

                baseLineC.setP1(upperLeftPoint);
                baseLineR.setP1(upperLeftPoint);
                baseLineC.setP2(lowerLeftPoint);
                baseLineR.setP2(upperRightPoint);

                samplingGrid.initGrid(ax,ay,logicalWidth,logicalHeight);

                for (int i = 0; i < logicalWidth; i++) {
                    gridLineC = new Line(baseLineC.getP1(), baseLineC.getP2());

                    axis.setOrigin(gridLineC.getP1());
                    axis.setModulePitch(modulePitch.top);
                    gridLineC.setP1(axis.translate(i,0));

                    axis.setOrigin(gridLineC.getP2());
                    axis.setModulePitch(modulePitch.bottom);
                    gridLineC.setP2(axis.translate(i,0));

                    samplingGrid.setXLine(ax,ay,i,gridLineC);
                }

                for (int i = 0; i < logicalHeight; i++) {

                    gridLineR = new Line(baseLineR.getP1(), baseLineR.getP2());

                    axis.setOrigin(gridLineR.getP1());
                    axis.setModulePitch(modulePitch.left);
                    gridLineR.setP1(axis.translate(0,i));

                    axis.setOrigin(gridLineR.getP2());
                    axis.setModulePitch(modulePitch.right);
                    gridLineR.setP2(axis.translate(0,i));

                    samplingGrid.setYLine(ax,ay,i,gridLineR);

                }
            }
        }
        return samplingGrid;
    }

    private static int getAreaModulePitch(Point start, Point end, int logicalDistance){
        Line tempLine = new Line(start, end);
        int realDistance = tempLine.getLength();
        int modulePitch = (realDistance << QrReader.DECIMAL_POINT) / logicalDistance;
        return modulePitch;
    }
    
    private class AreaGrid{
        protected Line[] xLine;
        protected Line[] yLine;

        public AreaGrid(int width, int height) {
            xLine = new Line[width];
            yLine = new Line[height];
        }

        public void setXLine(int x, Line line){
            xLine[x] = line;
        }

        public void setYLine(int y, Line line){
            yLine[y] = line;
        }

        public Line getXLine(int x){
            return xLine[x];
        }

        public Line getYLine(int y){
            return yLine[y];
        }

        public int getWidth(){
            return xLine.length;
        }

        public int getHeight(){
            return yLine.length;
        }

    }
    
    private static class ModulePitch{
        public int top;
        public int bottom;
        public int left;
        public int right;
    }
    
}
