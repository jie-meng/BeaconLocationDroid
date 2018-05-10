package com.jmengxy.location.algorithms;

import com.jmengxy.location.Locator;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;

public class WeightTrilateral implements Locator {

    private double totalWeight;

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

        CombineAlgorithm ca = null;

        try {
            ca = new CombineAlgorithm(doGrouper.getA(), 3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object[][] c = ca.getResult();

        double[] tempLocation = new double[2];

        for (int i = 0; i < c.length; i++) {

            List<Base> triBases = new ArrayList<>();

            for (int j = 0; j < 3; j++) {
                Base bb = uniqueBases.get((int) c[i][j]);
                triBases.add(bb);
            }

            double[] weightLocation;

            try {
                weightLocation = calculate(triBases);
            } catch (Exception e) {
                return null;
            }

            tempLocation[0] += weightLocation[0];
            tempLocation[1] += weightLocation[1];
        }

        location.setxAxis(tempLocation[0] / totalWeight);
        location.setyAxis(tempLocation[1] / totalWeight);

        return location;
    }

    public double[] calculate(List<Base> bases) {
        double[] rawLocation;
        double[] loc;

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

        rawLocation = new double[2];

        for (int i = 0; i < 2; i++) {
            rawLocation[i] = resultArray[i][0];
        }

        double weight = 0;

        for (int i = 0; i < 3; i++) {
            weight += (1.0 / bases.get(i).getDistance());
        }

        totalWeight += weight;

        loc = new double[2];

        for (int i = 0; i < 2; i++) {
            loc[i] = rawLocation[i] * weight;
        }

        return loc;
    }
}
