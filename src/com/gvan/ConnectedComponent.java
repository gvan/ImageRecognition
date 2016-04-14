package com.gvan;

/**
 * Created by betinvest on 4/14/16.
 */
public class ConnectedComponent {

    private Image image;
    private int[][] mask =
            {{-1,-1},{-1,0},{-1,1},
            {0,-1},{0,0},{0,-1},
            {1,-1},{1,0},{1,1}};

    public ConnectedComponent(Image image) {
        this.image = image.clone();
    }

    public Image getConnectedComponent(){
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
