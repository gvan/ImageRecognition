package com.gvan;

/**
 * Created by ivan on 5/24/16.
 */
public class Binarization {

    float [] P;//histogram of probability of intensity

    public void buildHistogram(Image image){
        P =  new float[image.intensity+1];
        int [] I = new int[image.intensity+1];
        int A = 0;
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                A++;
                I[image.matrix[i][j]]++;
            }
        }
        float maxP = 0;
        for(int i = 0;i < image.intensity + 1;i++){
            P[i] = (float) I[i] / A;
            if(maxP < P[i])
                maxP = P[i];
        }

//        Print the histogram
        float k = 50/maxP;
        for(int i = 0;i < image.intensity + 1;i++){
            int n = (int) (k*P[i]);
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = 0;j < n;j++)
                stringBuilder.append("# ");
            Utils.log(String.format("%s: %s", i, stringBuilder.toString()));
        }
    }

    public int getThreshold(Image image){
        buildHistogram(image);
        int bestT = 0;
        double maxSigma = 0;
        float mu = 0;
        float q1_t = P[0];
        float mu1_t = 0;

        for(int i = 0;i < P.length;i++)
            mu += i*P[i];
        Utils.log("mu %s", mu);
        for(int t = 1;t < image.intensity;t++){
            float q1_t_plus_1 = q1_t + P[t];
            float mu1_t_plus_1 = q1_t_plus_1 == 0 ? 0 :(q1_t*mu1_t + t*P[t]) / q1_t_plus_1;
            float mu2_t_plus_1 = (mu - q1_t_plus_1*mu1_t_plus_1) / (1 - q1_t_plus_1);
            double sigma = q1_t_plus_1*(1 - q1_t_plus_1)*Math.pow(mu1_t_plus_1 - mu2_t_plus_1, 2);
            Utils.log("%s: sigma %s", t, sigma);
            if(sigma >= maxSigma){
                maxSigma = sigma;
                bestT = t;
            }
            q1_t = q1_t_plus_1;
            mu1_t = mu1_t_plus_1;
        }

        return bestT;
    }

    public Image binarize(Image image){
        int threshold = getThreshold(image);
        Utils.log("threshold %s", threshold);
        Image resImage = new Image(image.width, image.height, 1);
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                resImage.matrix[i][j] = image.matrix[i][j] < threshold ? 0 : 1;
            }
        }
        return resImage;
    }

}
