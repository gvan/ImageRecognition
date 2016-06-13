package com.gvan;

public class Main {

    public static void main(String[] args) {

        String fileName = "/home/betinvest/Study/diplom/images/qr2.pgm";
        String outFileName = "/home/ivan/Study/diplom/images/output.pgm";
//        String fileName = "/home/betinvest/Study/diplom/images/test.pgm";

        QrReader.read(fileName);
    }

}
