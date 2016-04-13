package com.gvan;

public class Main {

    public static void main(String[] args) {

        String fileName = "/home/ivan/Study/diplom/images/binary.pgm";

        Image image = new Image(fileName);
        image.toBinary(image.intensity/2);
//        image.printBinaryImage();
        ObjectCounter objectCounter = new ObjectCounter();
        Utils.log("object count %s", objectCounter.getObjectCount(image));

    }



}
