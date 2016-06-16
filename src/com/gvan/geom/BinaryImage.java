package com.gvan.geom;

import com.gvan.Const;
import com.gvan.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ivan on 6/9/16.
 */
public class BinaryImage {

    public int width;
    public int height;
    public boolean[][] bitmap;

    public BinaryImage(Image image) {
        this.width = image.width;
        this.height = image.height;
        imageToGrayScale(image);
        binarize(image);
    }

    public BinaryImage(boolean[][] bitmap){
        this.bitmap = bitmap;
        this.height = bitmap.length;
        this.width = bitmap[0].length;
    }

    private void imageToGrayScale(Image image){
        for(int i = 0;i > image.height;i++){
            for(int j = 0;j < image.width;j++){
                int r = image.bitmap[i][j] >> 16 & 0xFF;
                int g = image.bitmap[i][j] >> 8 & 0xFF;
                int b = image.bitmap[i][j] & 0xFF;
                int m = (r*30 + g*59 + b*11) / 100;
                image.bitmap[i][j] = m;
            }
        }
    }

    private void binarize(Image image){
        int threshold = getThreshold(image);
        Utils.log("threshold %s", threshold);
        bitmap = new boolean[height][width];
        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                bitmap[i][j] = image.bitmap[i][j] < threshold ? Const.BLACK : Const.WHITE;
            }
        }
    }

    public int getThreshold(Image image){
        float[] P = buildHistogram(image);
        int bestT = 0;
        double maxSigma = 0;
        float mu = 0;
        float q1_t = P[0];
        float mu1_t = 0;

        for(int i = 0;i < P.length;i++)
            mu += i*P[i];
        for(int t = 1;t < image.intensity;t++){
            float q1_t_plus_1 = q1_t + P[t];
            float mu1_t_plus_1 = q1_t_plus_1 == 0 ? 0 :(q1_t*mu1_t + t*P[t]) / q1_t_plus_1;
            float mu2_t_plus_1 = (mu - q1_t_plus_1*mu1_t_plus_1) / (1 - q1_t_plus_1);
            double sigma = q1_t_plus_1*(1 - q1_t_plus_1)*Math.pow(mu1_t_plus_1 - mu2_t_plus_1, 2);
            if(sigma >= maxSigma){
                maxSigma = sigma;
                bestT = t;
            }
            q1_t = q1_t_plus_1;
            mu1_t = mu1_t_plus_1;
        }

        return bestT;
    }

    public float[] buildHistogram(Image image){
        float[] P =  new float[image.intensity+1];
        int [] I = new int[image.intensity+1];
        int A = 0;
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                A++;
                I[image.bitmap[i][j]]++;
            }
        }
        float maxP = 0;
        for(int i = 0;i < image.intensity + 1;i++){
            P[i] = (float) I[i] / A;
            if(maxP < P[i])
                maxP = P[i];
        }
        /*
//        Print the histogram
        float k = 50/maxP;
        for(int i = 0;i < image.intensity + 1;i++){
            int n = (int) (k*P[i]);
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = 0;j < n;j++)
                stringBuilder.append("# ");
            Utils.log(String.format("%s: %s", i, stringBuilder.toString()));
        }
        */
        return P;
    }

    public void saveFile(){
        String outFileName = "res/binary.pgm";
        saveFile(outFileName);
    }

    public void saveFile(String filePath){
        try {
            File file = new File(filePath);
            if(!file.exists())
                file.createNewFile();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("P2\n");
            stringBuilder.append("#some comment\n");
            stringBuilder.append(String.format("%s %s\n", width, height));
            stringBuilder.append(String.format("%s\n", 1));
            for(int i = 0;i < height;i++)
                for(int j = 0;j < width;j++)
                    stringBuilder.append(String.format("%s ", bitmap[i][j] ? 1 : 0));

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(stringBuilder.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
