package com.gvan.geom;

import java.io.*;
import java.util.Scanner;

/**
 * Created by ivan on 4/13/16.
 */
public class Image {

    public int width;
    public int height;
    public int intensity;
    public int[][] bitmap;

    public Image() {
    }

    public Image(int width, int height, int intensity){
        this.width = width;
        this.height = height;
        this.intensity = intensity;
        bitmap = new int[height][width];
        for(int i = 0;i < height;i++)
            for(int j = 0;j < width;j++)
                bitmap[i][j] = 0;
    }

    public Image(String fileName){
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            Scanner scanner = new Scanner(fileInputStream);
            scanner.nextLine();
            scanner.nextLine();

            width = scanner.nextInt();
            height = scanner.nextInt();
            intensity = scanner.nextInt();

            fileInputStream.close();

            fileInputStream = new FileInputStream(fileName);

            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            int lines = 4;
            while (lines > 0){
                char c;
                do {
                    c = (char)(dataInputStream.readUnsignedByte());
                } while (c != '\n');
                lines--;
            }

            bitmap = new int[height][width];
            for(int r = 0;r < height; r++)
                for(int c = 0;c < width;c++){
                    bitmap[r][c] = dataInputStream.readUnsignedByte();
                }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(){
        String outFileName = "/home/ivan/Study/diplom/images/output.pgm";
        saveFile(outFileName);
    }

    public void saveFile(String filePath){
        try {
            File file = new File(filePath);
            if(!file.exists())
                file.createNewFile();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("P2\n");
            stringBuilder.append("#some comment\n");
            stringBuilder.append(String.format("%s %s\n", width, height));
            stringBuilder.append(String.format("%s\n", intensity));
            for(int i = 0;i < height;i++)
                for(int j = 0;j < width;j++)
                    stringBuilder.append(String.format("%s ", bitmap[i][j]));

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(stringBuilder.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toBinary(int threshold){
        for(int r = 0;r < height;r++)
            for(int c = 0;c < width;c++)
                bitmap[r][c] = bitmap[r][c] > threshold ? 1 : 0;
    }

    public int getLongSide(){
        return width > height ? width : height;
    }

    public  void printImage(){
        System.out.printf("width %s height %s\n", width, height);
        for(int r = 0;r < height;r++) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0;c < width;c++){
                builder.append(String.format("%s ", bitmap[r][c]));
            }
            System.out.printf(String.format("%s\n", builder.toString()));
        }
    }

    public  void printBinaryImage(){
        System.out.printf("width %s height %s intensity %s\n", width, height, intensity);
        for(int r = 0;r < height;r++) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0;c < width;c++){
                builder.append(String.format("%s ", bitmap[r][c] == 1 ? '⬛' : ' '));
            }
            System.out.printf(String.format("%s\n", builder.toString()));
        }
    }

    public Image clone(){
        Image image = new Image();
        image.width = width;
        image.height = height;
        image.intensity = intensity;
        image.bitmap = new int[height][width];
        for(int i = 0;i < height;i++)
            for(int j = 0;j < width;j++)
                image.bitmap[i][j] = bitmap[i][j];
        return image;
    }

    public boolean equals(Image image){
        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                if(bitmap[i][j] != image.bitmap[i][j])
                    return false;
            }
        }
        return true;
    }

}
