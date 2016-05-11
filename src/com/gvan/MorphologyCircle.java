package com.gvan;

import java.util.ArrayList;

/**
 * Created by ivan on 5/11/16.
 */
public class MorphologyCircle {

    private ArrayList<Integer> squares;
    private ArrayList<float[]> centroids;

    public ArrayList<Integer> square(Image image){
        squares = new ArrayList<Integer>();
        for(int i = 0;i < image.height;i++) {
            for (int j = 0; j < image.width; j++) {
                if(image.matrix[i][j] != 0){
                    int p = image.matrix[i][j];
                    while (p >= squares.size())
                        squares.add(0);
                    squares.set(p, squares.get(p) + 1);
                }
            }
        }
        return squares;
    }

    public ArrayList<float[]> centroid(Image image){
        square(image);
        centroids = new ArrayList<float[]>();
        for(int i = 0;i < squares.size();i++) {
            float[] pair = {0f, 0f};
            centroids.add(pair);
        }

        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.matrix[i][j] != 0){
                    int p = image.matrix[i][j];
                    float[] pair = centroids.get(p);
                    pair[0] += i;
                    pair[1] += j;
                }
            }
        }

        for(int i = 0;i < centroids.size();i++){
            float[] pair = centroids.get(i);
            pair[0] /= squares.get(i);
            pair[1] /= squares.get(i);
        }

        return centroids;
    }

    public float[] radialDistance(Image image){
        centroid(image);
        int marketsCount = centroids.size();
        int[] Ks = new int[marketsCount];
        float[] muR = new float[marketsCount];
        float[] sigR = new float[marketsCount];
        float[] res = new float[marketsCount];
        for(int i = 0;i < muR.length;i++) {
            Ks[i] = 0;
            muR[i] = 0;
            sigR[i] = 0;
        }
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.matrix[i][j] != 0){
                    int p = image.matrix[i][j];
                    float[] centroid = centroids.get(p);
                    if(i == 0 || j == 0 ||
                            i == image.height - 1 || j == image.width - 1 ||
                            hasNeighbor(i, j, image.matrix, p)){
                        muR[p] += Math.sqrt(Math.pow((centroid[0] - i), 2) + Math.pow((centroid[1] - j), 2));
                        Ks[p]++;
                    }
                }
            }
        }
        for(int i = 0;i < marketsCount;i++) {
            muR[i] = muR[i] / Ks[i];
        }
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.matrix[i][j] != 0){
                    int p = image.matrix[i][j];
                    float[] centroid = centroids.get(p);
                    if(i == 0 || j == 0 ||
                            i == image.height - 1 || j == image.width - 1 ||
                            hasNeighbor(i, j, image.matrix, p)){
                        float dist = (float) Math.sqrt(Math.pow((centroid[0] - i), 2) + Math.pow((centroid[1] - j), 2));
                        sigR[p] += Math.pow((dist - muR[p]), 2);
                    }
                }
            }
        }
        for(int i = 0;i < marketsCount;i++){
            sigR[i] = (float) Math.sqrt(sigR[i] / Ks[i]);
            res[i] = muR[i] / sigR[i];
        }
        return res;
    }

    private boolean hasNeighbor(int r, int c, int[][] matrix, int p){
        for(int i = -1;i <= 1;i++)
            for(int j = -1;j <= 1;j++)
                if(matrix[r+i][c+j] != p)
                    return true;
        return false;
    }

}
