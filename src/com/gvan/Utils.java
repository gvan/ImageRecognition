package com.gvan;

/**
 * Created by ivan on 4/13/16.
 */
public class Utils {

    public static void log(String str, Object... args){
        System.out.printf(String.format("%s\n",str), args);
    }

}
