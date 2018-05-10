package com.jmengxy.location.algorithms;

public class CombineAlgorithm {
    private int m;

    private int n;

    private int objLineIndex;

    public Object[][] obj;

    public CombineAlgorithm(Object[] src, int getNum) throws Exception {
        if (src == null)
            throw new Exception("Src array is empty");
        if (src.length < getNum)
            throw new Exception("Retrieve data length bigger than src");
        m = src.length;
        n = getNum;

        objLineIndex = 0;
        obj = new Object[combination(m,n)][n];

        Object[] tmp = new Object[n];
        combine(src, 0, 0, n, tmp);
    }

    public int combination(int m, int n) {
        if (m < n)
            return 0;

        int k = 1;
        int j = 1;

        for (int i = n; i >= 1; i--) {
            k = k * m;
            j = j * n;
            m--;
            n--;
        }
        return k / j;
    }

    private void combine(Object src[], int srcIndex, int i, int n, Object[] tmp) {
        int j;
        for (j = srcIndex; j < src.length - (n - 1); j++ ) {
            tmp[i] = src[j];
            if (n == 1) {
                System.arraycopy(tmp, 0, obj[objLineIndex], 0, tmp.length);
                objLineIndex ++;
            } else {
                n--;
                i++;
                combine(src, j+1, i, n, tmp);
                n++;
                i--;
            }
        }
    }

    public Object[][] getResult() {
        return obj;
    }
}
