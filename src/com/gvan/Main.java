package com.gvan;

public class Main {

    public static void main(String[] args) {

//        String fileName = "/home/ivan/Study/diplom/images/binary.pgm";
        String fileName = "/home/betinvest/Study/diplom/images/test.pgm";

        Image image = new Image(fileName);
        image.toBinary(image.intensity/2);
        image.printImage();

        ConnectedComponent connectedComponent = new ConnectedComponent(image);
        image = connectedComponent.classicalConnect();
        image.printImage();

        MorphologyCircle morphology = new MorphologyCircle();
        for(float pair : morphology.radialDistance(image))
            Utils.log("round value %s", pair);

    }

}
