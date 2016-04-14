package com.gvan;

public class Main {

    public static void main(String[] args) {

//        String fileName = "/home/ivan/Study/diplom/images/binary.pgm";
        String fileName = "/home/betinvest/Study/diplom/images/test.pgm";

        Image image = new Image(fileName);
        image.toBinary(image.intensity/2);
//        ObjectCounter objectCounter = new ObjectCounter();
//        Utils.log("object count %s", objectCounter.getObjectCount(image));
        ConnectedComponent connectedComponent = new ConnectedComponent(image);
        Image connImage = connectedComponent.getConnectedComponent();
        connImage.printImage();

    }

}
