package com.gvan.pattern;

import com.gvan.Const;
import com.gvan.QrReader;
import com.gvan.exception.InvalidVersionException;
import com.gvan.exception.InvalidVersionInfoException;
import com.gvan.exception.VersionInformationException;
import com.gvan.geom.*;
import com.gvan.geom.Point;
import com.gvan.util.DebugCanvas;
import com.gvan.util.Utils;

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

    // this constant used for VersionInformation's error correction (BCC
    static final int[] VersionInfoBit = {
            0x07C94,0x085BC,0x09A99,0x0A4D3,0x0BBF6,0x0C762,0x0D847,
            0x0E60D,0x0F928,0x10B78,0x1145D,0x12A17,0x13532,0x149A6,
            0x15683,0x168C9,0x177EC,0x18EC4,0x191E1,0x1AFAB,0x1B08E,
            0x1CC1A,0x1D33F,0x1ED75,0x1F250,0x209D5,0x216F0,0x228BA,
            0x2379F,0x24B0B,0x2542E,0x26A64,0x27541,0x28C69
    };

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

    public static List<FindPattern> recogFindPattern(BinaryImage image, DebugCanvas debugCanvas){
        List<Line> lineAcross = findLineAcross(image.bitmap);
        List<Line> lineCross = findLinesCross(lineAcross);
        List<ThreePoints> threePointses = getFindPatternCenters(lineCross);
        List<FindPattern> findPatterns = new ArrayList<FindPattern>();
        for(ThreePoints threePoints : threePointses) {
            debugCanvas.drawPoint(threePoints.getCenter(0));
            debugCanvas.drawPoint(threePoints.getCenter(1));
            debugCanvas.drawPoint(threePoints.getCenter(2));
            Utils.log("threePoints %s", threePoints.toString());
            int[] sincos = getAngle(threePoints.getCenters());
            threePoints.sort(sincos);
            int[] width = getWidth(image.bitmap, threePoints.getCenters());
            Utils.log("width %s %s %s", width[0], width[1], width[2]);
            int[] moduleSize = {
                    (width[Const.UL] << QrReader.DECIMAL_POINT) / 7,
                    (width[Const.UR] << QrReader.DECIMAL_POINT) / 7,
                    (width[Const.DL] << QrReader.DECIMAL_POINT) / 7};
            int version = calcRoughVersion(threePoints.getCenters(), width);
            if(version > 6){
                version = calcExactVersion(threePoints.getCenters(), sincos, moduleSize, image.bitmap);
            }
            Utils.log("version %s", version);
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
        boolean lastElement = Const.WHITE;
        int width = image.length;
        int height = image[0].length;

        while (true){
            boolean currentElement = image[currentPoint.getX()][currentPoint.getY()];
            if(currentElement == lastElement){
                lengthBuffer[bufferPointer]++;
            } else {
                if(currentElement == Const.WHITE){
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
        if(buffer[0] == 0 || buffer[1] == 0 || buffer[2] == 0 || buffer[3] == 0  || buffer[4] == 0)
            return false;
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
                    if(lineNeighbor.size() * 8 > compareLine.getLength()){
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
                        if(d1 < 10 || d2 < 10 || d3 < 10) continue;
                        if((d3 > d1 && d3 > d2 && isRightTriangle(d3, d1, d2)) ||
                                (d2 > d1 && d2 > d3 && isRightTriangle(d2, d1, d3)) ||
                                (d1 > d2 && d1 > d3 && isRightTriangle(d1, d2, d3))){
                            threePointses.add(new ThreePoints(p1, p2, p3));
                            removePoint(points, p1);
                            removePoint(points, p2);
                            removePoint(points, p3);
                            i = j = k = 0;
                        }
                    }
                }
            }
        }
        return threePointses;
    }

    private static void removePoint(List<Point> points, Point point){
        for(int i = points.size() - 1;i >= 0;i--){
            Point pointLocal = points.get(i);
            if(pointLocal.getX() > point.getX() - 4 && pointLocal.getX() < point.getX() + 4 &&
                    pointLocal.getY() > point.getY() - 4 && pointLocal.getY() < point.getY() + 4){
                points.remove(i);
            }
        }

    }

    private static boolean isRightTriangle(int dMax, int d1, int d2){
        long hypotenuse = Math.round(Math.sqrt(d1 * d1 + d2 * d2));
        return dMax <= hypotenuse +1 && dMax >= hypotenuse - 1;
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

        Utils.log("remote point %s %s", remotePoint.getX(), remotePoint.getY());
        Utils.log("origin point %s %s", originPoint.getX(), originPoint.getY());
        int dist = new Line(originPoint, remotePoint).getLength();
        int dy = remotePoint.getY() - originPoint.getY();
        int dx = remotePoint.getX() - originPoint.getX();
        int[] angle = new int[2];
        //sin
        angle[0] = (dy << QrReader.DECIMAL_POINT) / dist;
        //cos
        angle[1] = (dx << QrReader.DECIMAL_POINT) / dist;

        Utils.log("sin %s cos %s", ((double)dy / dist), ((double)dx / dist));

        return angle;
    }

    private static int[] getWidth(boolean [][] image, Point[] centers){
        int[] width = new int[3];

        for(int i = 0;i < 3;i++){
            boolean flag = false;
            int lx, rx;
            int y = centers[i].getY();
            for(lx = centers[i].getX();lx >= 0;lx--){
                if(image[lx][y] == Const.BLACK && image[lx - 1][y] == Const.WHITE){
                    if(!flag) flag = true;
                    else break;
                }
            }
            flag = false;
            for(rx = centers[i].getX();rx < image.length;rx++){
                if (image[rx][y] == Const.BLACK && image[rx + 1][y] == Const.WHITE){
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
        int averageWidth = ((width[Const.UL] + width[Const.UR] + width[Const.DL]) << QrReader.DECIMAL_POINT) / 21;
        Utils.log("length %s avWidth %s", lengthAdditionalLine, averageWidth);
        int roughVersion = ((lengthAdditionalLine  / averageWidth) - 10) / 4;
        if(((lengthAdditionalLine / averageWidth) - 10 % 4) >= 2){
            roughVersion++;
        }
        if(roughVersion == 0) roughVersion = 1;

        roughVersion = 2;
        return roughVersion;
    }

    static int calcExactVersion(Point[] centers, int[] angle, int[] moduleSize, boolean[][] image)
            throws InvalidVersionInfoException, InvalidVersionException {
        boolean[] versionInformation = new boolean[18];
        Point[] points = new Point[18];
        Point target;
        Axis axis = new Axis(angle, moduleSize[Const.UR]); //UR
        axis.setOrigin(centers[Const.UR]);

        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 3; x++) {
                target = axis.translate(x - 7, y - 3);
                versionInformation[x + y * 3] = image[target.getX()][target.getY()];
                points[x + y * 3] = target;
            }
        }

        int exactVersion = 0;
        try {
            exactVersion = checkVersionInfo(versionInformation);
        } catch (InvalidVersionInfoException e) {
            axis.setOrigin(centers[Const.DL]);
            axis.setModulePitch(moduleSize[Const.DL]); //DL

            for (int x = 0; x < 6; x++) {
                for (int y = 0; y < 3; y++) {
                    target = axis.translate(x - 3, y - 7);
                    versionInformation[y + x * 3] = image[target.getX()][target.getY()];
                    points[x + y * 3] = target;
                }
            }

            try {
                exactVersion = checkVersionInfo(versionInformation);
            } catch (VersionInformationException e2) {
                throw e2;
            }
        }
        return exactVersion;
    }

    static int checkVersionInfo(boolean[] target)
            throws InvalidVersionInfoException {
        // note that this method includes BCH 18-6 Error Correction
        // see page 67 on JIS-X-0510(2004)
        int errorCount = 0, versionBase;
        for (versionBase = 0; versionBase < VersionInfoBit.length; versionBase++) {
            errorCount = 0;
            for (int j = 0; j < 18; j++) {
                if (target[j] ^ (VersionInfoBit[versionBase] >> j) % 2 == 1)
                    errorCount++;
            }
            if (errorCount <= 3) break;
        }
        if (errorCount <= 3)
            return 7 + versionBase;
        else
            throw new InvalidVersionInfoException("Too many errors in version information");
    }


}
