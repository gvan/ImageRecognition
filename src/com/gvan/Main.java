package com.gvan;

public class Main {

    public static void main(String[] args) {

//        String fileName = "/home/ivan/Study/diplom/images/binary.pgm";
        String fileName = "/home/betinvest/Study/diplom/images/test.pgm";

        Image image = new Image(fileName);
        image.toBinary(image.intensity/2);
        image.printBinaryImage();

        Morphologic morphologic = new Morphologic();
        morphologic.initStruct(5, 5);
        morphologic.breaking(image).printBinaryImage();

    }

}
