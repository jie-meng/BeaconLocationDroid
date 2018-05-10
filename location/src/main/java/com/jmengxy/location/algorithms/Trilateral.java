package com.jmengxy.location.algorithms;

import com.jmengxy.location.Locator;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import java.util.List;

import Jama.Matrix;

public class Trilateral implements Locator {

    private Location location;

    @Override
    public Location getLocation(List<Base> bases) {
        location = new Location();

        if (bases == null || bases.isEmpty() || bases.size() < 3) {
            return null;
        }

        DoGroup doGrouper = new DoGroup();
        List<Base> uniqueBases = doGrouper.doGroup(bases);
        if (uniqueBases == null) {
            return null;
        }

        System.out.println(bases.get(0).getId());
        System.out.println(bases.get(1).getId());
        System.out.println(bases.get(2).getId());

        try {
            return calculate(uniqueBases);
        } catch (Exception e) {
            return null;
        }
    }

    private Location calculate(List<Base> bases) {
        double[][] a = new double[2][2];
        double[][] b = new double[2][1];

        for (int i = 0; i < 2; i++) {
            a[i][0] = 2 * (bases.get(i).getLocation().getxAxis() - bases.get(2).getLocation().getxAxis());
            a[i][1] = 2 * (bases.get(i).getLocation().getyAxis() - bases.get(2).getLocation().getyAxis());
        }


        for (int i = 0; i < 2; i++) {
            b[i][0] = Math.pow(bases.get(i).getLocation().getxAxis(), 2)
                    - Math.pow(bases.get(2).getLocation().getxAxis(), 2)
                    + Math.pow(bases.get(i).getLocation().getyAxis(), 2)
                    - Math.pow(bases.get(2).getLocation().getyAxis(), 2)
                    + Math.pow(bases.get(2).getFlatDistance(), 2)
                    - Math.pow(bases.get(i).getFlatDistance(), 2);
        }

        Matrix b1 = new Matrix(b);
        Matrix a1 = new Matrix(a);

        Matrix a2 = a1.transpose();

        Matrix tmpMatrix1 = a2.times(a1);
        Matrix reTmpMatrix1 = tmpMatrix1.inverse();
        Matrix tmpMatrix2 = reTmpMatrix1.times(a2);

        Matrix resultMatrix = tmpMatrix2.times(b1);
        double[][] resultArray = resultMatrix.getArray();

        location.setxAxis(resultArray[0][0]);
        location.setyAxis(resultArray[1][0]);

        return location;
    }
}
