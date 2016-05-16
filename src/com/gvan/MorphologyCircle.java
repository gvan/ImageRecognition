package com.gvan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 5/11/16.
 */
public class MorphologyCircle {

    private ArrayList<Integer> squares;
    private ArrayList<float[]> centroids;
    private float[] muRRArr;
    private float[] muRCArr;
    private float[] muCCArr;
    private float[] principleAxis;

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

        Utils.log("centroid");
        for(int i = 0;i < centroids.size();i++){
            float[] pair = centroids.get(i);
            pair[0] /= squares.get(i);
            pair[1] /= squares.get(i);
            Utils.log("%s: %s %s", i, pair[0], pair[1]);
        }

        return centroids;
    }

    public void centralMoment(Image image){
        centroid(image);
        int markerCount = squares.size();
        muRRArr = new float[markerCount];
        muRCArr = new float[markerCount];
        muCCArr = new float[markerCount];

        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.matrix[i][j] != 0){
                    int p = image.matrix[i][j];
                    float[] centroid = centroids.get(p);
                    muRRArr[p] += Math.pow(i - centroid[0], 2);
                    muRCArr[p] += ((i - centroid[0])*(j - centroid[1]));
                    muCCArr[p] += Math.pow(j - centroid[1], 2);
                }
            }
        }
        Utils.log("second-order moment");
        for(int i = 0;i < markerCount;i++){
            muRRArr[i] /= squares.get(i);
            muRCArr[i] /= squares.get(i);
            muCCArr[i] /= squares.get(i);
            Utils.log("%s: %s %s %s", i, muRRArr[i], muRCArr[i], muCCArr[i]);
        }
    }

    public void principalAxis(Image image){
        centralMoment(image);
        int marketCount = squares.size();
        double[] alphasL = new double[marketCount];
        double[] alphasS = new double[marketCount];
        double[] betasL = new double[marketCount];
        double[] betasS = new double[marketCount];
        double[] lengthL = new double[marketCount];
        double[] lengthS = new double[marketCount];
        double[] muRCaB = new double[marketCount];
        double[] muRCaS = new double[marketCount];
        for(int i = 0;i < marketCount;i++){
            muRCaB[i] = 0;
            muRCaS[i] = 0;
        }
        for(int i = 0;i < marketCount;i++){
            float muRR = muRRArr[i];
            float muRC = muRCArr[i];
            float muCC = muCCArr[i];
            if(muRC == 0 && muRR > muCC){
                alphasL[i] = 90;
                alphasS[i] = 0;
                lengthL[i] = 4*Math.sqrt(muRR);
                lengthS[i] = 4*Math.sqrt(muCC);
            } else
            if(muRC == 0 && muRR <= muCC){
                alphasL[i] = 0;
                alphasS[i] = 90;
                lengthL[i] = 4*Math.sqrt(muCC);
                lengthS[i] = 4*Math.sqrt(muRR);
            } else
            if(muRC != 0 && muRR <= muCC){
                double angle = (-2*muRC) / (muRR - muCC + Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2)));
                alphasL[i] = Math.toDegrees(Math.atan(angle));
                alphasL[i] = alphasL[i] < 0 ? -alphasL[i] + 90 : 90 - alphasL[i];
                alphasS[i] = alphasL[i] - 90;
                lengthL[i] = Math.sqrt(8*(muRR + muCC + Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2))));
                lengthS[i] = Math.sqrt(8*(muRR + muCC - Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2))));
            } else
            if(muRC != 0 && muRR > muCC){
                double angle = Math.sqrt(muCC + muRR + Math.sqrt(Math.pow(muCC - muRR, 2) + 4*Math.pow(muRC, 2))) / (-2*muRC);
                alphasL[i] = Math.toDegrees(Math.atan(angle));
                alphasL[i] = alphasL[i] < 0 ? -alphasL[i] + 90 : 90 - alphasL[i];
                alphasS[i] = alphasL[i] - 90;
                lengthL[i] = Math.sqrt(8*(muRR + muCC + Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2))));
                lengthS[i] = Math.sqrt(8*(muRR + muCC - Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2))));
            }
            betasL[i] = Math.toRadians(alphasL[i]);
            betasS[i] = Math.toRadians(alphasS[i]);
        }
        Utils.log("alpha");
        for(int i = 0;i < marketCount;i++)
            Utils.log("%s: %s %s", i, alphasL[i], alphasS[i]);
        Utils.log("length");
        for(int i = 0;i < marketCount;i++)
            Utils.log("%s: %s %s", i, lengthL[i], lengthS[i]);

        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.matrix[i][j] != 0){
                    int p = image.matrix[i][j];
                    float[] centroid = centroids.get(p);
                    muRCaB[p] += Math.pow((i - centroid[0])*Math.cos(betasL[p]) + (j - centroid[1])*Math.sin(betasL[p]), 2);
                    muRCaS[p] += Math.pow((i - centroid[0])*Math.cos(betasS[p]) + (j - centroid[1])*Math.sin(betasS[p]), 2);
                }
            }
        }
        for(int i = 0;i < marketCount;i++){
            muRCaB[i] = muRCaB[i] / squares.get(i);
            muRCaS[i] = muRCaS[i] / squares.get(i);
            Utils.log("%s: muRCaL %s muRCaS %s", i, muRCaB[i], muRCaS[i]);
        }
    }

    public List<BoundsRectangle> getBoundsRectangles(Image image){
        List<BoundsRectangle> rectangles = new ArrayList<BoundsRectangle>();
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.matrix[i][j] != 0){
                    int p = image.matrix[i][j];
                    while (p >= rectangles.size())
                        rectangles.add(new BoundsRectangle());
                    BoundsRectangle rectangle = rectangles.get(p);
                    if (rectangle.r0 > i) rectangle.r0 = i;
                    if (rectangle.c0 > j) rectangle.c0 = j;
                    if (rectangle.r1 < i) rectangle.r1 = i;
                    if (rectangle.c1 < j) rectangle.c1 = j;
                }
            }
        }
        return rectangles;
    }

    public float[] roundCriterion(Image image){
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
