package com.gvan.other;

import com.gvan.util.Utils;
import com.gvan.geom.Image;

/**
 * Created by betinvest on 5/10/16.
 */
public class Morphology {

    private int[][] struct;
    private int sCols;
    private int sRows;
    private int sX;
    private int sY;

    private final int[][] S_3_SQR = {{1,1,1},{1,1,1},{1,1,1}};

    protected void initStruct(int cols, int rows){
        this.sCols = cols;
        this.sRows = rows;
        this.sX = cols / 2;
        this.sY = rows / 2;
        Utils.log("structure sX %s sY %s", sX, sY);
        struct = new int[sRows][sCols];
        for(int i = 0;i < struct.length;i++)
            for(int j = 0;j < struct[i].length;j++)
                struct[i][j] = 1;
    }

    public Image increase(Image image){
        return increase(image, struct);
    }

    public Image increase(Image image, int[][] struct){
        int sCols = struct[0].length;
        int sRows = struct.length;
        int sX = sCols / 2;
        int sY = sRows / 2;
        Image resImage = new Image(image.width, image.height, 1);
        for(int i = sY;i < image.height - sY;i++){
            for(int j = sX;j < image.width - sX;j++){
                if(image.bitmap[i][j] == 1){
                    for(int k = i - sY;k <= i + sY;k++){
                        for(int l = j - sX;l <= j + sX;l++){
                            if(struct[k % sRows][l % sCols] == 1){
                                resImage.bitmap[k][l] = resImage.intensity;
                            }
                        }
                    }
                }
            }
        }
        return resImage;
    }

    public Image erosion(Image image){
        Image resImage = new Image(image.width, image.height, 1);
        for(int i = sY;i < image.height - sY;i++){
            for(int j = sX;j < image.width - sX;j++){
                boolean adjust = true;
                for(int k = i - sY;k <= i + sY;k++){
                    for(int l = j - sX;l <= j + sX;l++){
                        if(image.bitmap[k][l] == 0){
                            adjust = false;
                            break;
                        }
                    }
                    if(!adjust)
                        break;
                }
                if(adjust)
                    resImage.bitmap[i][j] = 1;
            }
        }
        return resImage;
    }

    public Image closing(Image image){
        return erosion(increase(image));
    }

    public Image breaking(Image image){
        return increase(erosion(image));
    }

    public Image conjuction(Image image1, Image image2){
        Image resImage = new Image(image1.width, image1.height, 1);
        for(int i = 0;i < image1.height;i++){
            for(int j = 0;j < image1.width;j++){
                resImage.bitmap[i][j] = image1.bitmap[i][j] | image2.bitmap[i][j];
            }
        }
        return resImage;
    }

    public Image disjunction(Image image1, Image image2){
        Image resImage = new Image(image1.width, image1.height, 1);
        for(int i = 0;i < image1.width;i++){
            for(int j = 0;j < image1.height;j++){
                resImage.bitmap[i][j] = image1.bitmap[i][j] & image2.bitmap[i][j];
            }
        }
        return resImage;
    }

    public Image conditionalIncrease(Image image){
        Image resImage = erosion(image);
        Image localImage;
        do {
            localImage = resImage.clone();
            resImage = disjunction(increase(resImage, S_3_SQR), image);
        } while (!resImage.equals(localImage));
        return resImage;
    }

}
