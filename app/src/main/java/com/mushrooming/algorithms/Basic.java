package com.mushrooming.algorithms;

/**
 * Created by piotrek on 05.11.17.
 */
// implemented because Integer.min etc may be not be available on older Androids??
public abstract class Basic {
    static int min(int a, int b) {
        if (a<b) return a; else return b;
    }

    static double min(double a, double b) {
        if (a<b) return a; else return b;
    }

    static int max(int a, int b) {
        if (a>b) return a; else return b;
    }

    static double max(double a, double b) {
        if (a>b) return a; else return b;
    }
}
