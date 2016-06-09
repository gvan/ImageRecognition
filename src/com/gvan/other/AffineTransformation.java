package com.gvan.other;

import com.gvan.Utils;
import com.gvan.geom.Image;

/**
 * Created by betinvest on 6/6/16.
 */
public class AffineTransformation {

    public Image translate(Image image, int dC, int dR){
        Image resImage = new Image(image.width, image.height, image.intensity);
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                int nI = i + dC;
                int nJ = j + dR;
                if(nI > 0 && nI < image.height && nJ > 0 && nJ < image.width){
                    resImage.bitmap[nI][nJ] = image.bitmap[i][j];
                }
            }
        }
        return resImage;
    }

    public Image rotate(Image image, double a){
        Utils.log("angle %s", a);

        int[] xExtremes = new int[]{rotX(a, 0, 0), rotX(a, 0, image.height - 1),
                rotX(a, image.width - 1, 0), rotX(a, image.width - 1, image.height - 1)};
        int[] yExtremes = new int[]{rotY(a, 0, 0), rotY(a, 0, image.height - 1),
                rotY(a, image.width - 1, 0), rotY(a, image.width - 1, image.height - 1)};
        int minX = min(xExtremes);
        int maxX = max(xExtremes);
        int minY = min(yExtremes);
        int maxY = max(yExtremes);
        Utils.log("minX %s maxX %s minY %s maxY %s", minX, maxX, minY, maxY);

        int nWidth = maxX - minX;
        int nHeight = maxY - minY;
        Image resImage = new Image(nWidth, nHeight, image.intensity);
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                int nJ = (int)(Math.cos(a)*j - Math.sin(a)*i) + Math.abs(minX);
                int nI = (int)(Math.cos(a)*j + Math.sin(a)*i) + Math.abs(minY);
                if(nI > 0 && nI < nHeight && nJ > 0 && nJ < nWidth){
                    resImage.bitmap[nI][nJ] = image.bitmap[i][j];
                }
            }
        }
        return resImage;
    }

    private int rotY(double a, int x, int y){
        return (int)(Math.cos(a)*x + Math.sin(a)*y);
    }

    private int rotX(double a, int x, int y){
        return (int)(-Math.sin(a)*x + Math.cos(a)*y);
    }

    private int max(int[] arr){
        int max = 0;
        for(int i = 0;i < arr.length;i++)
            if(arr[i] > max) max = arr[i];
        return max;
    }

    private int min(int[] arr){
        int min = Integer.MAX_VALUE;
        for(int j = 0;j < arr.length;j++)
            if(arr[j] < min) min = arr[j];
        return min;
    }

}
