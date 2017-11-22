package com.mushrooming.algorithms;

/**
 * Created by piotrek on 05.11.17.
 */

public class PairSortedBy1<T1 extends Comparable<T1>, T2> implements Comparable<PairSortedBy1<T1,T2>>{
    private T1 f;
    private T2 s;

    public PairSortedBy1(T1 e1, T2 e2) {
        f = e1;
        s = e2;
    }

    public T1 getF() {
        return f;
    }

    public T2 getS() {
        return s;
    }

    public void setF(T1 f) {
        this.f = f;
    }

    public void setS(T2 s) {
        this.s = s;
    }

    @Override
    public int compareTo(PairSortedBy1<T1,T2> p2) {
        return f.compareTo(p2.f);
    }
}
