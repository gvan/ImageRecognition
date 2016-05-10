package com.gvan;

/**
 * Created by betinvest on 5/10/16.
 */
public class Morphologic {

    private int[][] struct;
    private int sCols;
    private int sRows;
    private int sX;
    private int sY;

    protected void initStruct(int cols, int rows){
        this.sCols = cols;
        this.sRows = rows;
        this.sX = rows / 2;
        this.sY = cols / 2;
        Utils.log("structure sX %s sY %s", sX, sY);
        struct = new int[sRows][sCols];
        for(int i = 0;i < struct.length;i++)
            for(int j = 0;j < struct[i].length;j++)
                struct[i][j] = 1;
    }

    public Image increase(Image image){
        Image resImage = new Image(image.width, image.height, 1);
        for(int i = sY;i < image.height - sY;i++){
            for(int j = sX;j < image.width - sX;j++){
                if(image.matrix[i][j] == 1){
                    for(int k = i - sY;k <= i + sY;k++){
                        for(int l = j - sX;l <= j + sX;l++){
                            resImage.matrix[k][l] = resImage.intensity;
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
                        if(image.matrix[k][l] == 0){
                            adjust = false;
                            break;
                        }
                    }
                    if(!adjust)
                        break;
                }
                if(adjust)
                    resImage.matrix[i][j] = 1;
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

    public Image conjuction(Image image){
        Image resImage = new Image(image.width, image.height, 1);
        return resImage;
    }

}
