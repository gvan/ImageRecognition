package com.gvan.other;

import com.gvan.util.Utils;
import com.gvan.geom.Image;

/**
 * Created by ivan on 4/14/16.
 */
public class ObjectCounter {

    final int[][][] externalShapes =
            {{{0,0},{0,1}},
            {{0,0},{1,0}},
            {{1,0},{0,0}},
            {{0,1},{0,0}}};

    final int[][][] internalShapes =
            {{{1,1},{1,0}},
            {{1,1},{0,1}},
            {{1,0},{1,1}},
            {{0,1},{1,1}}};

    private boolean externalMatch(int r, int c, int[][] matrix){
        for(int[][] external : externalShapes){
            boolean match = true;
            for(int i = 0;i < external.length;i++)
                for(int j = 0;j < external[i].length;j++)
                    if(matrix[r+i][c+j] != external[i][j])
                        match = false;
            if(match) return true;
        }
        return false;
    }

    private boolean internalMatch(int r, int c, int[][] matrix){
        for(int[][] internal : internalShapes){
            boolean match = true;
            for(int i = 0;i < internal.length;i++)
                for(int j = 0;j < internal[i].length;j++)
                    if(matrix[r+i][c+j] != internal[i][j])
                        match = false;
            if(match) return true;
        }
        return false;
    }

    public int getObjectCount(Image image){
        int externals = 0, internals = 0;
        for(int r = 0;r < image.bitmap.length - 1;r++){
            for(int c = 0;c < image.bitmap[r].length - 1;c++){
                if(externalMatch(r, c, image.bitmap)) externals++;
                if(internalMatch(r, c, image.bitmap)) internals++;
            }
        }
        Utils.log("externals %s internals %s", externals, internals);
        return ((externals - internals)/4);
    }

}
