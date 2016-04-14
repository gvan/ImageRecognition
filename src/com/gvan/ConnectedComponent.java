package com.gvan;

/**
 * Created by betinvest on 4/14/16.
 */
public class ConnectedComponent {

    private Image image;
    private final int[][] mask =
            {{-1,-1},{-1,0},{-1,1},
            {0,-1},{0,0},{0,-1},
            {1,-1},{1,0},{1,1}};
    private final int MAX_LABELS = 8000;
    private int nextLabel = 1;

    public ConnectedComponent(Image image) {
        this.image = image.clone();
    }

    public Image classicalConnect(){
        Image imageRst = new Image(image.width, image.height, 1);
        int[] parent = new int[MAX_LABELS];
        int[] labels = new int[MAX_LABELS];
        for(int i = 0;i < MAX_LABELS;i++){
            parent[i] = 0;
            labels[i] = 0;
        }
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
                if(k >= MAX_LABELS) System.exit(1);
                imageRst.matrix[i][j] = k;
                if(j > 0 && image.matrix[i][j - 1] == image.matrix[i][j] && imageRst.matrix[i][j - 1] != k)
                    union(k, imageRst.matrix[i][j - 1], parent);
                if(i > 0 && image.matrix[i - 1][j] == image.matrix[i][j] && imageRst.matrix[i - 1][j] != k)
                    union(k, imageRst.matrix[i - 1][j], parent);
            }
        }

        nextLabel = 1;
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.matrix[i][j] == 1){
                    imageRst.matrix[i][j] = find(imageRst.matrix[i][j], parent, labels);
                }
            }
        }

        return imageRst;
    }

    private void union(int i, int j, int[] parent){
        while (parent[i] > 0)
            i = parent[i];
        while (parent[j] > 0)
            j = parent[j];
        if(i != j){
            if(i < j)
                parent[i] = j;
            else
                parent[j] = i;
        }
    }

    private int find(int i, int[] parent, int[] label){
        while (parent[i] > 0)
            i = parent[i];
        if(label[i] == 0)
            label[i] = nextLabel++;
        return label[i];
    }

    public Image recursionConnect(){
        negate();
        int label = 0;
        for(int i = 0;i < image.height;i++)
            for(int j = 0;j < image.width;j++)
                if(image.matrix[i][j] == -1){
                    search(++label, i, j);
                }
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
