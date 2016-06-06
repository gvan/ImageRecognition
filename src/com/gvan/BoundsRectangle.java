package com.gvan;

/**
 * Created by ivan on 5/14/16.
 */
public class BoundsRectangle {

    public int r0;
    public int c0;
    public int r1;
    public int c1;


    public BoundsRectangle(){
        r0 = Integer.MAX_VALUE;
        c0 = Integer.MAX_VALUE;
        r1 = 0;
        c1 = 0;
    }

    @Override
    public String toString() {
        return String.format("r0:%s c0:%s r1:%s c1:%s", r0, c0, r1, c1);
    }
}
