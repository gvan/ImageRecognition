package com.gvan.pattern;

import com.gvan.Const;
import com.gvan.QrReader;
import com.gvan.Utils;
import com.gvan.geom.BinaryImage;
import com.gvan.geom.Line;
import com.gvan.geom.Point;
import com.gvan.geom.ThreePoints;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 6/9/16.
 */
public class FindPattern {

    private ThreePoints threePoints;
    private int[] sincos;
    private int[] width;
    private int[] moduleSize;
    private int version;

    public FindPattern(ThreePoints threePoints, int[] sincos, int[] width, int[] moduleSize, int version) {
        this.threePoints = threePoints;
        this.sincos = sincos;
        this.width = width;
        this.moduleSize = moduleSize;
        this.version = version;
    }

    public ThreePoints getThreePoints() {
        return threePoints;
    }

    public int[] getSincos() {
        return sincos;
    }

    public int[] getWidth() {
        return width;
    }

    public int getModuleSize() {
        return moduleSize[Const.UL];
    }

    public int getVersion() {
        return version;
    }

    public static List<FindPattern> recogFindPattern(BinaryImage image){
        List<Line> lineAcross = findLineAcross(image.bitmap);
        List<Line> lineCross = findLinesCross(lineAcross);
        List<ThreePoints> threePointses = getFindPatternCenters(lineCross);
        List<FindPattern> findPatterns = new ArrayList<FindPattern>();
        for(ThreePoints threePoints : threePointses) {
            int[] sincos = getAngle(threePoints.getCenters());
            threePoints.sort(sincos);
            Utils.log("threePoints %s", threePoints.toString());
            int[] width = getWidth(image.bitmap, threePoints.getCenters());
            Utils.log("width %s %s %s", width[0], width[1], width[2]);
            int version = calcRoughVersion(threePoints.getCenters(), width);
            Utils.log("version %s", version);
            int[] moduleSize = {(width[Const.UL] << QrReader.DECIMAL_POINT) / 7,
                    (width[Const.UR] << QrReader.DECIMAL_POINT) / 7,
                    (width[Const.DL] << QrReader.DECIMAL_POINT) / 7};
            findPatterns.add(new FindPattern(threePoints, sincos, width, moduleSize, version));
        }
        return findPatterns;
    }

    private static List<Line> findLineAcross(boolean[][] image){
        Point currentPoint = new Point();
        List<Line> lineAcross = new ArrayList<Line>();

        int[] lengthBuffer = new int[5];
        int bufferPointer = 0;

        boolean horizontalDirection  = true;
        boolean lastElement = Const.FRONT;
        int width = image.length;
        int height = image[0].length;

        while (true){
            boolean currentElement = image[currentPoint.getX()][currentPoint.getY()];
            if(currentElement == lastElement){
                lengthBuffer[bufferPointer]++;
            } else {
                if(currentElement == Const.FRONT){
                    if(checkPattern(lengthBuffer, bufferPointer)){
                        int x1, y1, x2, y2;
                        if(horizontalDirection){
                            x1 = currentPoint.getX();
                            for(int j = 0;j < lengthBuffer.length;j++)
                                x1 -= lengthBuffer[j];
                            x2 = currentPoint.getX() - 1;
                            y1 = y2 = currentPoint.getY();
                        } else {
                            x1 = x2 = currentPoint.getX();
                            y1 = currentPoint.getY();
                            for(int j = 0;j < lengthBuffer.length;j++)
                                y1 -= lengthBuffer[j];
                            y2 = currentPoint.getY() - 1;
                        }
                        lineAcross.add(new Line(x1, y1, x2, y2));
                    }
                }
                bufferPointer = (bufferPointer + 1) % 5;
                lengthBuffer[bufferPointer] = 1;
                lastElement = !lastElement;
            }

            //get next point
            if(horizontalDirection){
                if(currentPoint.getX() < width - 1){
                    currentPoint.translate(1, 0);
                } else
                if(currentPoint.getY() < height - 1){
                    currentPoint.set(0, currentPoint.getY() + 1);
                    lengthBuffer = new int[5];
                } else {
                    currentPoint.set(0, 0);
                    lengthBuffer = new int[5];
                    horizontalDirection = false;
                }
            } else {
                if(currentPoint.getY() < height - 1){
                    currentPoint.translate(0, 1);
                } else
                if(currentPoint.getX() < width - 1){
                    currentPoint.set(currentPoint.getX() + 1, 0);
                    lengthBuffer = new int[5];
                } else {
                    break;
                }
            }
        }

        return lineAcross;
    }

    private static boolean checkPattern(int[] buffer, int pointer){
        final int[] modelRatio = {1, 1, 3, 1, 1};

        int baseLength = 0;
        for(int i = 0;i < 5;i++){
            baseLength += buffer[i];
        }
        if (baseLength < 10) return false;

        baseLength <<= QrReader.DECIMAL_POINT;
        baseLength /= 7;
        for(int i = 0;i < modelRatio.length;i++){
            int leastLength = baseLength*modelRatio[i] - baseLength/2;
            int mostLength = baseLength*modelRatio[i] + baseLength/2;

            int targetLength = buffer[(pointer + i + 1) % 5] << QrReader.DECIMAL_POINT;
            if(targetLength < leastLength || targetLength > mostLength) {
                return false;
            }
        }
//        Utils.log("%s %s %s %s %s", buffer[0], buffer[1], buffer[2], buffer[3], buffer[4]);
        return true;
    }

    private static List<Line> findLinesCross(List<Line> lineAcross){
        List<Line> lineCross = new ArrayList<Line>();
        List<Line> lineNeighbor = new ArrayList<Line>();
        List<Line> lineCandidate = new ArrayList<Line>();
        Line compareLine;

        lineCandidate.addAll(lineAcross);
        for(int i = 0;i < lineCandidate.size() - 1;i++){
            lineNeighbor.clear();
            lineNeighbor.add(lineCandidate.get(i));
            for(int j = i + 1;j < lineCandidate.size();j++){
                if(Line.isNeighbor(lineNeighbor.get(lineNeighbor.size() - 1), lineCandidate.get(j))){
                    lineNeighbor.add(lineCandidate.get(j));
                    compareLine = lineNeighbor.get(lineNeighbor.size() - 1);
                    if(lineNeighbor.size() * 8 > compareLine.getLength() || (j == lineCandidate.size() - 1)){
                        lineCross.add(lineNeighbor.get(lineNeighbor.size() / 2));
                        lineCandidate.removeAll(lineNeighbor);
                    }
                } else
                if(canNeighbor(lineNeighbor.get(lineNeighbor.size() - 1), lineCandidate.get(j)) ||
                        j == lineCandidate.size() - 1){
                    compareLine = lineNeighbor.get(lineNeighbor.size() - 1);
                    if(lineNeighbor.size() * 6 > compareLine.getLength()){
                        lineCross.add(lineNeighbor.get(lineNeighbor.size() / 2));
                        lineCandidate.removeAll(lineNeighbor);
                    }
                    break;
                }
            }
        }
        return lineCross;
    }

    private static boolean canNeighbor(Line line1, Line line2){
        if(Line.isCross(line1, line2))
            return true;
        return line1.isHorizontal() ?
                Math.abs(line1.getP1().getY() - line2.getP1().getY()) > 1 :
                Math.abs(line1.getP1().getX() - line2.getP1().getX()) > 1;
    }

    private static List<ThreePoints> getFindPatternCenters(List<Line> crossLines){
        List<ThreePoints> threePointses = new ArrayList<ThreePoints>();
        List<Point> points = new ArrayList<Point>();
        for(int i = 0;i < crossLines.size() - 1;i++){
            Line compareLine = crossLines.get(i);
            for(int j = i + 1;j < crossLines.size();j++){
                Line comparedLine = crossLines.get(j);
                if(Line.isCross(compareLine, comparedLine)){
                    int x, y;
                    if(compareLine.isHorizontal()){
                        x = compareLine.getCenter().getX();
                        y = comparedLine.getCenter().getY();
                    } else {
                        x = comparedLine.getCenter().getX();
                        y = compareLine.getCenter().getY();
                    }
                    points.add(new Point(x, y));
                }
            }
        }
        if(points.size() >= 3){
            for(int i = 0;i < points.size() - 1;i++){
                for(int j = i + 1;j < points.size() - 1;j++){
                    for(int k = j + 1;k < points.size();k++){
                        Point p1 = points.get(i);
                        Point p2 = points.get(j);
                        Point p3 = points.get(k);
                        int d1 = new Line(p1, p2).getLength();
                        int d2 = new Line(p1, p3).getLength();
                        int d3 = new Line(p2, p3).getLength();
                        if((d3 > d1 && d3 > d2 && isRightTriangle(d3, d1, d2)) ||
                                (d2 > d1 && d2 > d3 && isRightTriangle(d2, d1, d3)) ||
                                (d1 > d2 && d1 > d3 && isRightTriangle(d1, d2, d3))){
                            threePointses.add(new ThreePoints(p1, p2, p3));
                            points.remove(p1);
                            points.remove(p2);
                            points.remove(p3);
                            i = j = k = 0;
                        }
                    }
                }
            }
        }
        return threePointses;
    }

    private static boolean isRightTriangle(int dMax, int d1, int d2){
        long hypotenuse = Math.round(Math.sqrt(d1 * d1 + d2 * d2));
        return dMax <= hypotenuse + 4 && dMax >= hypotenuse - 4;
//        return dMax == hypotenuse;
    }

    private static int[] getAngle(Point[] centers){
        Line[] additionalLine = new Line[3];
        for (int i = 0;i < centers.length;i++)
            additionalLine[i] = new Line(centers[i], centers[(i + 1) % centers.length]);

        Line remoteLine = Line.getLongest(additionalLine);
        Point originPoint = new Point();
        for(Point center : centers){
            if(!remoteLine.getP1().equals(center) &&
                    !remoteLine.getP2().equals(center)){
                originPoint = center;
                break;
            }
        }

        Point remotePoint;
        if(originPoint.getY() <= remoteLine.getP1().getY() &
                originPoint.getY() <= remoteLine.getP2().getY()){
            if(remoteLine.getP1().getX() < remoteLine.getP2().getX())
                remotePoint = remoteLine.getP2();
            else
                remotePoint = remoteLine.getP1();
        } else
        if(originPoint.getX() >= remoteLine.getP1().getX() &
                originPoint.getX() >= remoteLine.getP2().getX()){
            if (remoteLine.getP1().getY() < remoteLine.getP2().getY())
                remotePoint = remoteLine.getP2();
            else
                remotePoint = remoteLine.getP1();
        } else
        if(originPoint.getY() >= remoteLine.getP1().getY() &
                originPoint.getY() >= remoteLine.getP2().getY()){
            if(remoteLine.getP1().getX() < remoteLine.getP2().getX())
                remotePoint = remoteLine.getP1();
            else
                remotePoint = remoteLine.getP2();
        } else {
            if(remoteLine.getP1().getY() < remoteLine.getP2().getY())
                remotePoint = remoteLine.getP1();
            else
                remotePoint = remoteLine.getP2();
        }

        int dist = new Line(originPoint, remotePoint).getLength();
        int[] angle = new int[2];
        //sin
        angle[0] = ((remotePoint.getY() - originPoint.getY()) << QrReader.DECIMAL_POINT) / dist;
        //cos
        angle[1] = ((remotePoint.getX() - originPoint.getX()) << QrReader.DECIMAL_POINT) / dist;

        return angle;
    }

    private static int[] getWidth(boolean [][] image, Point[] centers){
        int[] width = new int[3];

        for(int i = 0;i < 3;i++){
            boolean flag = false;
            int lx, rx;
            int y = centers[i].getY();
            for(lx = centers[i].getX();lx >= 0;lx--){
                if(image[lx][y] == Const.BACK && image[lx - 1][y] == Const.FRONT){
                    if(!flag) flag = true;
                    else break;
                }
            }
            flag = false;
            for(rx = centers[i].getX();rx < image.length;rx++){
                if (image[rx][y] == Const.BACK && image[rx + 1][y] == Const.FRONT){
                    if(!flag) flag = true;
                    else break;
                }
            }
            width[i] = rx - lx + 1;
        }
        return width;
    }

    private static int calcRoughVersion(Point[] centers, int[] width){
        int lengthAdditionalLine = (new Line(centers[Const.UL], centers[Const.UR]).getLength()) << QrReader.DECIMAL_POINT;
        int averageWidth = ((width[Const.UL] + width[Const.UR]) << QrReader.DECIMAL_POINT) / 14;
        int roughVersion = ((lengthAdditionalLine  / averageWidth) - 10) / 4;
        if(((lengthAdditionalLine / averageWidth) - 10 % 4) >= 2){
            roughVersion++;
        }
        return roughVersion;
    }


}
