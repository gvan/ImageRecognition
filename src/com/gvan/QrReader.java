package com.gvan;

import com.gvan.exception.InvalidDataBlockException;
import com.gvan.geom.BinaryImage;
import com.gvan.geom.Image;
import com.gvan.pattern.AlignmentPattern;
import com.gvan.pattern.FindPattern;
import com.gvan.reedsolomon.RsDecode;
import com.gvan.util.DebugCanvas;
import com.gvan.util.Utils;

import java.util.List;

/**
 * Created by ivan on 5/28/16.
 */
public class QrReader {

    public static int DECIMAL_POINT = 21;

    public static void read(String fileName, DebugCanvas debugCanvas){
        Image colorImage = new Image(fileName);
        DECIMAL_POINT = 23 - Utils.sqrt(colorImage.getLongSide() / 256);
        Utils.log("DECIMAL POINT %s", DECIMAL_POINT);
        BinaryImage image = new BinaryImage(colorImage);
        List<FindPattern> findPatterns = FindPattern.recogFindPattern(image, debugCanvas);
        for(int i = 0;i < findPatterns.size();i++){
            FindPattern findPattern = findPatterns.get(i);
            AlignmentPattern alignmentPattern = AlignmentPattern.findAlignmentPattern(image.bitmap, findPattern);
            SamplingGrid samplingGrid = SamplingGrid.getSamplingGrid(findPattern, alignmentPattern);
            boolean[][] qrCodeMatrix = getQrCodeMatrix(image.bitmap, samplingGrid);
            BinaryImage qrImage = new BinaryImage(qrCodeMatrix);
            qrImage.saveFile(String.format("res/output%s.pgm", i));
//            QrCodeSymbol qrCodeSymbol = new QrCodeSymbol(qrCodeMatrix);
//            int[] blocks = qrCodeSymbol.getBlocks();
//            for(int j = 0;j < blocks.length;j++)
//                Utils.log("%s - %s", j, blocks[j]);
//            blocks = correctDataBlocks(blocks, qrCodeSymbol);
//            QRCodeDataBlockReader reader = new QRCodeDataBlockReader(blocks, qrCodeSymbol.getVersion(), qrCodeSymbol.getNumErrorCollectionCode());
//            try {
//                Utils.log(reader.getDataString());
//            } catch (InvalidDataBlockException e) {
//                throw e;
//            }
        }
    }

    public static boolean[][] getQrCodeMatrix(boolean[][] image, SamplingGrid gridLines){
        int gridSize = gridLines.getTotalWidth();
        boolean[][] sampledMatrix = new boolean[gridSize][gridSize];
        for(int ay = 0;ay < gridLines.getHeight();ay++){
            for(int ax = 0;ax < gridLines.getWidth();ax++){
                for(int y = 0;y < gridLines.getHeight(ax, ay);y++){
                    for(int x = 0;x < gridLines.getWidth(ax, ay);x++){
                        int x1 = gridLines.getXLine(ax,ay,x).getP1().getX();
                        int y1 = gridLines.getXLine(ax,ay,x).getP1().getY();
                        int x2 = gridLines.getXLine(ax,ay,x).getP2().getX();
                        int y2 = gridLines.getXLine(ax,ay,x).getP2().getY();
                        int x3 = gridLines.getYLine(ax,ay,y).getP1().getX();
                        int y3 = gridLines.getYLine(ax,ay,y).getP1().getY();
                        int x4 = gridLines.getYLine(ax,ay,y).getP2().getX();
                        int y4 = gridLines.getYLine(ax,ay,y).getP2().getY();

                        int e = (y2 - y1) * (x3 - x4) - (y4 - y3) * (x1 - x2);
                        int f = (x1 * y2 - x2 * y1) * (x3 - x4) - (x3 * y4 - x4 * y3) * (x1 - x2);
                        int g = (x3 * y4 - x4 * y3) * (y2 - y1) - (x1 * y2 - x2 * y1) * (y4 - y3);
//                        Utils.log("f/e=%s, g/e=%s", (f/e), (g/e));
                        sampledMatrix[gridLines.getX(ax, x)][gridLines.getY(ay, y)] = image[f/e][g/e];
                    }
                }
            }
        }

        return sampledMatrix;
    }

    private static int[] correctDataBlocks(int[] blocks, QrCodeSymbol qrCodeSymbol) {
        int numSucceededCorrections = 0;
        int numCorrectionFailures = 0;
        int dataCapacity = qrCodeSymbol.getDataCapacity();
        int[] dataBlocks = new int[dataCapacity];
        int numErrorCollectionCode = qrCodeSymbol.getNumErrorCollectionCode();
        int numRSBlocks = qrCodeSymbol.getNumRSBlocks();
        int eccPerRSBlock = numErrorCollectionCode / numRSBlocks;
        if (numRSBlocks == 1) {
            RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
            int ret = corrector.decode(blocks);
            if (ret > 0)
                numSucceededCorrections += ret;
            else if (ret < 0)
                numCorrectionFailures++;
            return blocks;
        }
        else  { //we have to interleave data blocks because symbol has 2 or more RS blocks
            int numLongerRSBlocks = dataCapacity % numRSBlocks;
            if (numLongerRSBlocks == 0) { //symbol has only 1 type of RS block
                int lengthRSBlock = dataCapacity / numRSBlocks;
                int[][] RSBlocks = new int[numRSBlocks][lengthRSBlock];
                //obtain RS blocks
                for (int i = 0; i < numRSBlocks; i++) {
                    for (int j = 0; j < lengthRSBlock; j++) {
                        RSBlocks[i][j] = blocks[j * numRSBlocks + i];
                    }
                    RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
                    int ret = corrector.decode(RSBlocks[i]);
                    if (ret > 0)
                        numSucceededCorrections += ret;
                    else if (ret < 0)
                        numCorrectionFailures++;
                }
                //obtain only data part
                int p = 0;
                for (int i = 0; i < numRSBlocks; i++) {
                    for (int j = 0; j < lengthRSBlock - eccPerRSBlock; j++) {
                        dataBlocks[p++] = RSBlocks[i][j];
                    }
                }
            }
            else { //symbol has 2 types of RS blocks
                int lengthShorterRSBlock = dataCapacity / numRSBlocks;
                int lengthLongerRSBlock = dataCapacity / numRSBlocks + 1;
                int numShorterRSBlocks = numRSBlocks - numLongerRSBlocks;
                int[][] shorterRSBlocks = new int[numShorterRSBlocks][lengthShorterRSBlock];
                int[][] longerRSBlocks = new int[numLongerRSBlocks][lengthLongerRSBlock];
                for (int i = 0; i < numRSBlocks; i++) {
                    if (i < numShorterRSBlocks) { //get shorter RS Block(s)
                        int mod = 0;
                        for (int j = 0; j < lengthShorterRSBlock; j++) {
                            if (j == lengthShorterRSBlock - eccPerRSBlock) mod = numLongerRSBlocks;
                            shorterRSBlocks[i][j] = blocks[j * numRSBlocks + i + mod];
                        }
                        RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
                        int ret = corrector.decode(shorterRSBlocks[i]);
                        if (ret > 0)
                            numSucceededCorrections += ret;
                        else if (ret < 0)
                            numCorrectionFailures++;

                    }
                    else { 	//get longer RS Blocks
                        int mod = 0;
                        for (int j = 0; j < lengthLongerRSBlock; j++) {
                            if (j == lengthShorterRSBlock - eccPerRSBlock) mod = numShorterRSBlocks;
                            longerRSBlocks[i - numShorterRSBlocks][j] = blocks[j * numRSBlocks + i - mod];
                        }
                        RsDecode corrector = new RsDecode(eccPerRSBlock / 2);
                        int ret = corrector.decode(longerRSBlocks[i - numShorterRSBlocks]);
                        if (ret > 0)
                            numSucceededCorrections += ret;
                        else if (ret < 0)
                            numCorrectionFailures++;
                    }
                }
                int p = 0;
                for (int i = 0; i < numRSBlocks; i++) {
                    if (i < numShorterRSBlocks) {
                        for (int j = 0; j < lengthShorterRSBlock - eccPerRSBlock; j++) {
                            dataBlocks[p++] = shorterRSBlocks[i][j];
                        }
                    }
                    else {
                        for (int j = 0; j < lengthLongerRSBlock - eccPerRSBlock; j++) {
                            dataBlocks[p++] = longerRSBlocks[i - numShorterRSBlocks][j];
                        }
                    }
                }
            }
            return dataBlocks;
        }
    }

}
