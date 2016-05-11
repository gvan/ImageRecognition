package com.gvan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by betinvest on 4/14/16.
 */
public class ConnectedComponent {

    private Image image;
    private final int[][] mask =
            {{-1,-1},{-1,0},{-1,1},
            {0,-1},{0,0},{0,-1},
            {1,-1},{1,0},{1,1}};
    private int nextLabel = 1;

    public ConnectedComponent(Image image) {
        this.image = image.clone();
    }

    public Image classicalConnect(){
        Image imageRst = new Image(image.width, image.height, 1);
        List<Integer> parent = new ArrayList<Integer>();
        List<Integer> labels = new ArrayList<Integer>();
        int nextRegion = 1;
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.matrix[i][j] == 0) continue;
                int k = 0;
                boolean connected = false;
                if(j > 0 && image.matrix[i][j - 1] == image.matrix[i][j]){
                    k = imageRst.matrix[i][j - 1];
                    connected = true;
                }
                if(i > 0 && image.matrix[i - 1][j] == image.matrix[i][j] &&
                        (!connected || image.matrix[i - 1][j] < k)){
                    k = imageRst.matrix[i - 1][j];
                    connected = true;
                }
                if(!connected){
                    k = nextRegion;
                    nextRegion++;
                }
                imageRst.matrix[i][j] = k;
                if(j > 0 && image.matrix[i][j - 1] == image.matrix[i][j] && imageRst.matrix[i][j - 1] != k)
                    union(k, imageRst.matrix[i][j - 1], parent);
                else
                if(i > 0 && image.matrix[i - 1][j] == image.matrix[i][j] && imageRst.matrix[i - 1][j] != k)
                    union(k, imageRst.matrix[i - 1][j], parent);
                else
                if(parent.size() <= k)
                    for(int l = parent.size();l <= k;l++)
                        parent.add(0);
            }
        }

//        imageRst.printImage();
//        for(int i = 0;i < parent.size();i++)
//            Utils.log("%s - %s", i, parent.get(i));

        nextLabel = 1;
        for(int i = 0;i < imageRst.height;i++){
            for(int j = 0;j < imageRst.width;j++){
                if(imageRst.matrix[i][j] > 0){
                    imageRst.matrix[i][j] = find(imageRst.matrix[i][j], parent, labels);
                }
            }
        }
        imageRst.intensity = nextLabel - 1;

//        imageRst.printImage();
//        for(int i = 0;i < labels.size();i++)
//            Utils.log("%s - %s", i, labels.get(i));

        return imageRst;
    }

    private void union(int k, int j, List<Integer> parent){
        if(parent.size() <= k)
            for(int i = parent.size();i <= k;i++)
                parent.add(0);
        if(parent.size() <= j)
            for(int i = parent.size();i <= j;i++)
                parent.add(0);

        while (parent.get(k) > 0)
            k = parent.get(k);
        while (parent.get(j) > 0)
            j = parent.get(j);
        if(k != j){
            if(k < j)
                parent.set(k,j);
            else
                parent.set(j, k);
        }
    }

    private int find(int k, List<Integer> parent, List<Integer> label){
        while (parent.get(k) > 0)
            k = parent.get(k);
        if(label.size() <= k)
            for(int i = label.size();i <= k;i++)
                label.add(0);
        if(label.get(k) == 0)
            label.set(k, nextLabel++);
        return label.get(k);
    }

    public Image recursionConnect(){
        negate();
        int label = 0;
        for(int i = 0;i < image.height;i++)
            for(int j = 0;j < image.width;j++)
                if(image.matrix[i][j] == -1){
                    search(++label, i, j);
                }
        image.intensity = label;
        return image;
    }

    private void search(int label, int row, int col){
        image.matrix[row][col] = label;
        int[][] neighbors = neighbors(row, col);
        for(int[] neighbor : neighbors)
            if(image.matrix[neighbor[0]][neighbor[1]] == -1)
                search(label, neighbor[0], neighbor[1]);
    }

    private int[][] neighbors(int row, int col){
        int[][] neighbors = new int[mask.length][mask[0].length];
        for(int i = 0;i < mask.length;i++) {
            for (int j = 0; j < mask[i].length; j++) {
                if (j == 0) {
                    neighbors[i][j] = row + mask[i][j];
                    if (neighbors[i][j] >= image.height)
                        neighbors[i][j] = image.height - 1;
                } else {
                    neighbors[i][j] = col + mask[i][j];
                    if (neighbors[i][j] >= image.width)
                        neighbors[i][j] = image.width - 1;
                }
                if (neighbors[i][j] < 0) {
                    neighbors[i][j] = 0;
                }
            }
        }
        return neighbors;
    }

    private void negate(){
        for(int i = 0;i < image.width;i++)
            for(int j = 0;j < image.height;j++)
                if(image.matrix[i][j] == 1)
                    image.matrix[i][j] = -1;
    }

}
