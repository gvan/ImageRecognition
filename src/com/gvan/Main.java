package com.gvan;

public class Main {

    public static void main(String[] args) {

        String fileName = "/home/betinvest/Study/diplom/images/buffalo.pgm";
        String outFileName = "/home/betinvest/Study/diplom/images/output.pgm";
//        String fileName = "/home/betinvest/Study/diplom/images/test.pgm";

        Image image = new Image(fileName);
        Binarization binarization = new Binarization();
        image = binarization.binarize(image);

        AffineTransformation affine = new AffineTransformation();
        affine.rotate(image, Math.toRadians(0)).saveFile(outFileName);
    }

}
