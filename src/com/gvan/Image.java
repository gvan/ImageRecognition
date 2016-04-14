package com.gvan;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by ivan on 4/13/16.
 */
public class Image {

    public int width;
    public int height;
    public int intensity;
    public int[][] matrix;

    public Image() {
    }

    public Image(int width, int height, int intensity){
        this.width = width;
        this.height = height;
        this.intensity = intensity;
        matrix = new int[height][width];
        for(int i = 0;i < height;i++)
            for(int j = 0;j < width;j++)
                matrix[i][j] = 0;
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

            matrix = new int[height][width];
            for(int r = 0;r < height; r++)
                for(int c = 0;c < width;c++){
                    matrix[r][c] = dataInputStream.readUnsignedByte();
                }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toBinary(int threshold){
        for(int r = 0;r < height;r++)
            for(int c = 0;c < width;c++)
                matrix[r][c] = matrix[r][c] > threshold ? 1 : 0;
    }

    public  void printImage(){
        System.out.printf("width %s height %s\n", width, height);
        for(int r = 0;r < height;r++) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0;c < width;c++){
                builder.append(String.format("%s ", matrix[r][c]));
            }
            System.out.printf(String.format("%s\n", builder.toString()));
        }
    }

    public  void printBinaryImage(){
        System.out.printf("width %s height %s intensity %s\n", width, height, intensity);
        for(int r = 0;r < height;r++) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0;c < width;c++){
                builder.append(String.format("%s ", matrix[r][c] == 1 ? '⬛' : ' '));
            }
            System.out.printf(String.format("%s\n", builder.toString()));
        }
    }

    public Image clone(){
        Image image = new Image();
        image.width = width;
        image.height = height;
        image.intensity = intensity;
        image.matrix = new int[height][width];
        for(int i = 0;i < height;i++)
            for(int j = 0;j < width;j++)
                image.matrix[i][j] = matrix[i][j];
        return image;
    }

    public Image zero(){
        for(int i = 0;i < height;i++)
            for(int j = 0;j < width;j++)
                matrix[i][j] = 0;
        return this;
    }

}
