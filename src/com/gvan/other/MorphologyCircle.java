package com.gvan.other;

import com.gvan.Const;
import com.gvan.Utils;
import com.gvan.geom.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 5/11/16.
 */
public class MorphologyCircle {

    private int markerCount = 0;
    private ArrayList<Integer> squares;
    private ArrayList<float[]> centroids;
    private float[] muRRArr;
    private float[] muRCArr;
    private float[] muCCArr;
    private float[] muRCaL;
    private float[] muRCaS;

    public static  final String statFileName = "/home/ivan/Study/diplom/images/digits/stat.json";


    public ArrayList<Integer> squareAndCentroid(Image image){
        squares = new ArrayList<Integer>();
        centroids = new ArrayList<float[]>();

        for(int i = 0;i < image.height;i++) {
            for (int j = 0; j < image.width; j++) {
                if(image.bitmap[i][j] != 0){
                    int p = image.bitmap[i][j];
                    while (p >= squares.size()) {
                        squares.add(0);
                        float[] pair = {0f, 0f};
                        centroids.add(pair);
                    }
                    squares.set(p, squares.get(p) + 1);
                    float[] pair = centroids.get(p);
                    pair[0] += i;
                    pair[1] += j;
                }
            }
        }

        Utils.log("================================");
        Utils.log("centroids");
        for(int i = 0;i < centroids.size();i++){
            float[] pair = centroids.get(i);
            pair[0] /= squares.get(i);
            pair[1] /= squares.get(i);
            Utils.log("%s: %s %s", i, pair[0], pair[1]);
        }

        markerCount = squares.size();
        Utils.log("squares");
        for(int i = 0;i < squares.size();i++){
            Utils.log("%s: %s", i , squares.get(i));
        }
        return squares;
    }

    public void centralMoment(Image image){
        squareAndCentroid(image);
        muRRArr = new float[markerCount];
        muRCArr = new float[markerCount];
        muCCArr = new float[markerCount];

        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.bitmap[i][j] != 0){
                    int p = image.bitmap[i][j];
                    float[] centroid = centroids.get(p);
                    muRRArr[p] += Math.pow(i - centroid[0], 2);
                    muRCArr[p] += ((i - centroid[0])*(j - centroid[1]));
                    muCCArr[p] += Math.pow(j - centroid[1], 2);
                }
            }
        }
        Utils.log("second-order moments");
        for(int i = 0;i < markerCount;i++){
            muRRArr[i] /= squares.get(i);
            muRCArr[i] /= squares.get(i);
            muCCArr[i] /= squares.get(i);
            Utils.log("%s: %s %s %s", i, muRRArr[i], muRCArr[i], muCCArr[i]);
        }
    }

    public void principalAxis(Image image){
        centralMoment(image);
        double[] alphasL = new double[markerCount];
        double[] alphasS = new double[markerCount];
        double[] betasL = new double[markerCount];
        double[] betasS = new double[markerCount];
        double[] lengthL = new double[markerCount];
        double[] lengthS = new double[markerCount];
        muRCaL = new float[markerCount];
        muRCaS = new float[markerCount];
        for(int i = 0;i < markerCount;i++){
            muRCaL[i] = 0;
            muRCaS[i] = 0;
        }
        for(int i = 0;i < markerCount;i++){
            float muRR = muRRArr[i];
            float muRC = muRCArr[i];
            float muCC = muCCArr[i];
            if(muRC == 0 && muRR > muCC){
                alphasL[i] = -90;
                alphasS[i] = 0;
                lengthL[i] = 4*Math.sqrt(muRR);
                lengthS[i] = 4*Math.sqrt(muCC);
            } else
            if(muRC == 0 && muRR <= muCC){
                alphasL[i] = 0;
                alphasS[i] = -90;
                lengthL[i] = 4*Math.sqrt(muCC);
                lengthS[i] = 4*Math.sqrt(muRR);
            } else
            if(muRC != 0 && muRR <= muCC){
                double angle = (-2*muRC) / (muRR - muCC + Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2)));
                alphasL[i] = Math.toDegrees(Math.atan(angle));
                alphasL[i] = alphasL[i] < 0 ? -alphasL[i] + 90 : 90 - alphasL[i];
                alphasS[i] = alphasL[i] + 90;
                lengthL[i] = Math.sqrt(8*(muRR + muCC + Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2))));
                lengthS[i] = Math.sqrt(8*(muRR + muCC - Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2))));
            } else
            if(muRC != 0 && muRR > muCC){
                double angle = Math.sqrt(muCC + muRR + Math.sqrt(Math.pow(muCC - muRR, 2) + 4*Math.pow(muRC, 2))) / (-2*muRC);
                alphasL[i] = Math.toDegrees(Math.atan(angle));
                alphasL[i] = alphasL[i] < 0 ? -alphasL[i] + 90 : 90 - alphasL[i];
                alphasS[i] = alphasL[i] + 90;
                lengthL[i] = Math.sqrt(8*(muRR + muCC + Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2))));
                lengthS[i] = Math.sqrt(8*(muRR + muCC - Math.sqrt(Math.pow(muRR - muCC, 2) + 4*Math.pow(muRC, 2))));
            }
            betasL[i] = Math.toRadians(alphasL[i]);
            betasS[i] = Math.toRadians(alphasS[i]);
        }
        Utils.log("alphas");
        for(int i = 0;i < markerCount;i++)
            Utils.log("%s: %s %s", i, alphasL[i], alphasS[i]);
        Utils.log("lengths");
        for(int i = 0;i < markerCount;i++)
            Utils.log("%s: %s %s", i, lengthL[i], lengthS[i]);

        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.bitmap[i][j] != 0){
                    int p = image.bitmap[i][j];
                    float[] centroid = centroids.get(p);
                    muRCaL[p] += Math.pow((i - centroid[0])*Math.cos(betasL[p]) + (j - centroid[1])*Math.sin(betasL[p]), 2);
                    muRCaS[p] += Math.pow((i - centroid[0])*Math.cos(betasS[p]) + (j - centroid[1])*Math.sin(betasS[p]), 2);
                }
            }
        }
        for(int i = 0;i < markerCount;i++){
            muRCaL[i] = muRCaL[i] / squares.get(i);
            muRCaS[i] = muRCaS[i] / squares.get(i);
            Utils.log("%s: muRCaL %s muRCaS %s", i, muRCaL[i], muRCaS[i]);
        }
    }

    public float[] getMuRRArr() {
        return muRRArr;
    }

    public float[] getMuRCArr() {
        return muRCArr;
    }

    public float[] getMuCCArr() {
        return muCCArr;
    }

    public float[] getMuRCaL() {
        return muRCaL;
    }

    public float[] getMuRCaS() {
        return muRCaS;
    }

    public static void saveStat(){
        JSONArray jsonArray = new JSONArray();
        for(int i = 0;i < 10;i++){
            String fileName = String.format("/home/ivan/Study/diplom/images/digits/%s.pgm", i);
            Image image = new Image(fileName);
            image.toBinary(image.intensity / 2);

            ConnectedComponent connected = new ConnectedComponent(image);
            image = connected.classicalConnect();

            MorphologyCircle morphology = new MorphologyCircle();
            morphology.principalAxis(image);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Const.VALUE, i);
            jsonObject.put(Const.MU_RR, morphology.getMuRRArr()[1]);
            jsonObject.put(Const.MU_CC, morphology.getMuCCArr()[1]);
            jsonObject.put(Const.MU_RC, morphology.getMuRCArr()[1]);
            jsonObject.put(Const.MU_RCA_L, morphology.getMuRCaL()[1]);
            jsonObject.put(Const.MU_RCA_S, morphology.getMuRCaS()[1]);

            jsonArray.put(jsonObject);
        }

        File file = new File(statFileName);
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(jsonArray.toString().getBytes());
            outputStream.flush();
            outputStream.close();
            Utils.log("stat %s", jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int classifyDigit(String fileName){
        int digit = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(statFileName));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuffer.append(line);
            bufferedReader.close();

            JSONArray jsonArray = new JSONArray(stringBuffer.toString());
            List<SecondOrderMoment> moments = new ArrayList<SecondOrderMoment>();
            for(int i = 0;i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SecondOrderMoment moment = new SecondOrderMoment();
                moment.muRR = (float) jsonObject.getDouble(Const.MU_RR);
                moment.muRC = (float) jsonObject.getDouble(Const.MU_RC);
                moment.muCC = (float) jsonObject.getDouble(Const.MU_CC);
                moment.muRCaL = (float) jsonObject.getDouble(Const.MU_RCA_L);
                moment.muRCaS = (float) jsonObject.getDouble(Const.MU_RCA_S);
                moment.value = jsonObject.getInt(Const.VALUE);
                moments.add(moment);
            }

            Image image = new Image(fileName);
            image.toBinary(image.intensity/2);

            ConnectedComponent connectedComponent = new ConnectedComponent(image);
            image = connectedComponent.classicalConnect();

            MorphologyCircle morphology = new MorphologyCircle();
            morphology.principalAxis(image);

            float minDiff = Float.MAX_VALUE;
            for(SecondOrderMoment moment : moments){
                moment.muRR = Math.abs(morphology.getMuRRArr()[1] - moment.muRR);
                moment.muRC = Math.abs(morphology.getMuRCArr()[1] - moment.muRC);
                moment.muCC = Math.abs(morphology.getMuCCArr()[1] - moment.muCC);
                moment.muRCaL = Math.abs(morphology.getMuRCaL()[1] - moment.muRCaL);
                moment.muRCaS = Math.abs(morphology.getMuRCaS()[1] - moment.muRCaS);
                float diff = moment.muRR + moment.muRC + moment.muCC + moment.muRCaL + moment.muRCaS;
                if(diff < minDiff){
                    minDiff = diff;
                    digit = moment.value;
                }
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return digit;
    }

    public List<BoundsRectangle> getBoundsRectangles(Image image){
        List<BoundsRectangle> rectangles = new ArrayList<BoundsRectangle>();
        for(int i = 0;i < image.height;i++){
            for(int j = 0;j < image.width;j++){
                if(image.bitmap[i][j] != 0){
                    int p = image.bitmap[i][j];
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
        squareAndCentroid(image);
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
                if(image.bitmap[i][j] != 0){
                    int p = image.bitmap[i][j];
                    float[] centroid = centroids.get(p);
                    if(i == 0 || j == 0 ||
                            i == image.height - 1 || j == image.width - 1 ||
                            hasNeighbor(i, j, image.bitmap, p)){
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
                if(image.bitmap[i][j] != 0){
                    int p = image.bitmap[i][j];
                    float[] centroid = centroids.get(p);
                    if(i == 0 || j == 0 ||
                            i == image.height - 1 || j == image.width - 1 ||
                            hasNeighbor(i, j, image.bitmap, p)){
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

    private static class SecondOrderMoment{

        public SecondOrderMoment() {
        }

        private float muRR;
        private float muRC;
        private float muCC;
        private float muRCaL;
        private float muRCaS;
        private int value;
    }

}
