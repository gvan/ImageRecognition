package com.gvan;

public class Main {

    public static void main(String[] args) {

        String outputFile = "/home/ivan/Study/diplom/images/output.pgm";

        String inputFile = "/home/ivan/Study/diplom/images/binary.pgm";
//        String inputFile = "/home/betinvest/Study/diplom/images/test.pgm";

        Image image = new Image(inputFile);
        image.toBinary(image.intensity / 2);
//        ObjectCounter objectCounter = new ObjectCounter();
//        Utils.log("object count %s", objectCounter.getObjectCount(image));

        int cycles = 10;
        long avgTime = 0;
        Image markedImage = null;
        for(int i = 0;i < cycles;i++){
            long time = System.currentTimeMillis();
            ConnectedComponent connectedComponent = new ConnectedComponent(image);
//            markedImage = connectedComponent.classicalConnect();
            markedImage = connectedComponent.recursionConnect();
            avgTime += (System.currentTimeMillis() - time);
        }
        Utils.log("avg time %s", (avgTime/cycles));



        markedImage.saveFile(outputFile);


    }

}
