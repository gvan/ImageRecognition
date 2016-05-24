package com.gvan;

public class Main {

    public static void main(String[] args) {

        String fileName = "/home/ivan/Study/diplom/images/baboon.pgm";
        String outFileName = "/home/ivan/Study/diplom/images/binarization.pgm";
//        String fileName = "/home/betinvest/Study/diplom/images/test.pgm";

        Image image = new Image(fileName);
        Binarization binarization = new Binarization();
        binarization.binarize(image).saveFile(outFileName);

    }

}
