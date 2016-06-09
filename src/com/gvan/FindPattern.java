package com.gvan;

import com.gvan.geom.BinaryImage;
import com.gvan.geom.Line;
import com.gvan.geom.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by ivan on 6/9/16.
 */
public class FindPattern {

    private Point[] centers;

    public FindPattern(BinaryImage image) {
        recogFindPattern(image);
    }

    public void recogFindPattern(BinaryImage image){
        List<Line> lineAcross = findLineAcross(image);
        for(Line line : lineAcross){
            Utils.log(line.toString());
        }
    }

    private List<Line> findLineAcross(BinaryImage image){
        Point currentPoint = new Point();
        List<Line> lineAcross = new ArrayList<Line>();

        int[] lengthBuffer = new int[5];
        int bufferPointer = 0;

        boolean horizontalDirection  = true;
        boolean lastElement = Const.FRONT;

        while (true){
            boolean currentElement = image.bitmap[currentPoint.r][currentPoint.c];
            if(currentElement == lastElement){
                lengthBuffer[bufferPointer]++;
            } else {
                if(currentElement == Const.FRONT){
                    if(checkPattern(lengthBuffer, bufferPointer)){
                        int c1, r1, c2, r2;
                        if(horizontalDirection){
                            c1 = currentPoint.c;
                            for(int j = 0;j < lengthBuffer.length;j++)
                                c1 -= lengthBuffer[j];
                            c2 = currentPoint.c - 1;
                            r1 = r2 = currentPoint.r;
                        } else {
                            c1 = c2 = currentPoint.c;
                            r1 = currentPoint.r;
                            for(int j = 0;j < lengthBuffer.length;j++)
                                r1 -= lengthBuffer[j];
                            r2 = currentPoint.r - 1;
                        }
                        lineAcross.add(new Line(c1, r1, c2, r2));
                    }
                }
                bufferPointer = (bufferPointer + 1) % 5;
                lengthBuffer[bufferPointer] = 1;
                lastElement = !lastElement;
            }

            if(horizontalDirection){
                if(currentPoint.c < image.width - 1){
                    currentPoint.translate(1, 0);
                } else
                if(currentPoint.r < image.height - 1){
                    currentPoint.set(0, currentPoint.r + 1);
                    lengthBuffer = new int[5];
                } else {
                    currentPoint.set(0, 0);
                    lengthBuffer = new int[5];
                    horizontalDirection = false;
                }
            } else {
                if(currentPoint.r < image.height - 1){
                    currentPoint.translate(0, 1);
                } else
                if(currentPoint.c < image.width - 1){
                    currentPoint.set(currentPoint.c + 1, 0);
                    lengthBuffer = new int[5];
                } else {
                    break;
                }
            }
        }

        return lineAcross;
    }

    private boolean checkPattern(int[] buffer, int pointer){
        final int[] modelRatio = {1, 1, 3, 1, 1};

        int baseLength = 0;
        for(int i = 0;i < 5;i++){
            baseLength += buffer[i];
        }

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
        return true;
    }

}
