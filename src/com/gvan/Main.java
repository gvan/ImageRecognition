package com.gvan;

public class Main {

    public static void main(String[] args) {

        String fileName = "/home/ivan/Study/diplom/images/baboon.pgm";
        String outFileName = "/home/ivan/Study/diplom/images/output.pgm";
//        String fileName = "/home/betinvest/Study/diplom/images/test.pgm";

        Image image = new Image(fileName);
        Binarization binarization = new Binarization();
        image = binarization.binarize(image);

        AffineTransformation affine = new AffineTransformation();
        image = affine.rotate(image, Math.toRadians(130));
        image.saveFile(outFileName);
    }

}
