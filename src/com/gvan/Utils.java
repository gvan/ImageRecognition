package com.gvan;

/**
 * Created by ivan on 4/13/16.
 */
public class Utils {

    public static int sqrt(int val){
        int temp, g = 0, b = 0x8000, bshft = 15;
        do{
            if (val >= (temp = (((g << 1) + b)<<bshft--))){
                g += b;
                val -= temp;
            }
        } while ((b >>= 1) > 0);

        return g;
    }

    public static void log(String str){
        System.out.println(str);
    }

    public static void log(String str, Object... args){
        System.out.printf(String.format("%s\n",str), args);
    }

}
