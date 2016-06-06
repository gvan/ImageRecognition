package com.gvan;

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
                    resImage.matrix[nI][nJ] = image.matrix[i][j];
                }
            }
        }
        return resImage;
    }

    public Image rotate(Image image, double l){
        Utils.log("angle %s", l);
        int side = image.width > image.height ? image.width : image.height;
        Image resImage = new Image(side*2, side*2, image.intensity);
        int offsetI = image.height;
        int offsetJ = image.width;
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                int nJ = offsetJ + (int)(Math.cos(l)*j - Math.sin(l)*i);
                int nI = offsetI + (int)(Math.cos(l)*j + Math.sin(l)*i);
                if(nI > 0 && nI < side*2 && nJ > 0 && nJ < side*2){
                    resImage.matrix[nI][nJ] = image.matrix[i][j];
                }
            }
        }
        return resImage;
    }

}
