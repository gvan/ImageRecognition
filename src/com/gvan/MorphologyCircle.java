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

}
