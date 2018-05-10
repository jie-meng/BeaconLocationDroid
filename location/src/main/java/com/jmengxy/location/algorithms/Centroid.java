package com.jmengxy.location.algorithms;

import com.jmengxy.location.Locator;
import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Coordinate;
import com.jmengxy.location.models.Location;
import com.jmengxy.location.models.Round;

import java.util.ArrayList;
import java.util.List;

public class Centroid implements Locator {

    private double totalWeight;
    private Location location;

    @Override
    public Location getLocation(List<Base> bases) {

        location = new Location();

        DoGroup doGrouper = new DoGroup();
        List<Base> uniqueBases = doGrouper.doGroup(bases);
        if (uniqueBases == null) {
            return null;
        }

        Integer[] a = doGrouper.getA();
        CombineAlgorithm ca = null;

        try {
            ca = new CombineAlgorithm(a, 3);
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
        location.setxAxis(tempLocation[1] / totalWeight);

        return location;
    }

    public double[] calculate(List<Base> bases) {

        double[] rawLocation;

        double[] loc;

        Round r[] = new Round[3];

        for (int i = 0; i < 3; i++) {
            r[i] = new Round(bases.get(i).getLocation().getxAxis(), bases.get(i).getLocation().getyAxis(), bases.get(i).getDistance());
        }

        Coordinate rawCoor = triCentroid(r[0], r[1], r[2]);

        rawLocation = new double[]{rawCoor.getX(), rawCoor.getY()};

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

    public static Coordinate triCentroid(Round r1, Round r2, Round r3) {

        Coordinate p1 = null;
        Coordinate p2 = null;
        Coordinate p3 = null;

        Coordinate centroid = new Coordinate();

        List<Coordinate> intersections1 = intersection(r1.getX(), r1.getY(), r1.getR(),
                r2.getX(), r2.getY(), r2.getR());

        if (intersections1 != null && !intersections1.isEmpty()) {
            for (Coordinate intersection : intersections1) {
                if (p1 == null && Math.pow(intersection.getX() - r3.getX(), 2)
                        + Math.pow(intersection.getY() - r3.getY(), 2) <= Math.pow(r3.getR(), 2)) {
                    p1 = intersection;
                } else if (p1 != null) {
                    if (Math.pow(intersection.getX() - r3.getX(), 2) + Math.pow(intersection.getY()
                            - r3.getY(), 2) <= Math.pow(r3.getR(), 2)) {
                        if (Math.sqrt(Math.pow(intersection.getX() - r3.getX(), 2)
                                + Math.pow(intersection.getY() - r3.getY(), 2)) > Math.sqrt(Math.pow(p1.getX()
                                - r3.getX(), 2) + Math.pow(p1.getY() - r3.getY(), 2))) {
                            p1 = intersection;
                        }
                    }
                }
            }
        } else {
            return null;
        }

        List<Coordinate> intersections2 = intersection(r1.getX(), r1.getY(), r1.getR(),
                r3.getX(), r3.getY(), r3.getR());

        if (intersections2 != null && !intersections2.isEmpty()) {
            for (Coordinate intersection : intersections2) {//有交点
                if (p2 == null && Math.pow(intersection.getX() - r2.getX(), 2)
                        + Math.pow(intersection.getY() - r2.getY(), 2) <= Math.pow(r2.getR(), 2)) {
                    p2 = intersection;

                } else if (p2 != null) {
                    if (Math.pow(intersection.getX() - r2.getX(), 2) + Math.pow(intersection.getY()
                            - r2.getY(), 2) <= Math.pow(r2.getR(), 2)) {
                        if (Math.pow(intersection.getX() - r2.getX(), 2) + Math.pow(intersection.getY()
                                - r2.getY(), 2) > Math.sqrt(Math.pow(p2.getX() - r2.getX(), 2)
                                + Math.pow(p2.getY() - r2.getY(), 2))) {
                            p1 = intersection;
                        }
                    }
                }
            }
        } else {
            return null;
        }

        List<Coordinate> intersections3 = intersection(r2.getX(), r2.getY(), r2.getR(),
                r3.getX(), r3.getY(), r3.getR());

        if (intersections3 != null && !intersections3.isEmpty()) {
            for (Coordinate intersection : intersections3) {
                if (Math.pow(intersection.getX() - r1.getX(), 2)
                        + Math.pow(intersection.getY() - r1.getY(), 2) <= Math.pow(r1.getR(), 2)) {
                    p3 = intersection;
                } else if (p3 != null) {
                    if (Math.pow(intersection.getX() - r1.getX(), 2) + Math.pow(intersection.getY()
                            - r1.getY(), 2) <= Math.pow(r1.getR(), 2)) {
                        if (Math.pow(intersection.getX() - r1.getX(), 2) + Math.pow(intersection.getY()
                                - r1.getY(), 2) > Math.sqrt(Math.pow(p3.getX() - r1.getX(), 2)
                                + Math.pow(p3.getY() - r1.getY(), 2))) {
                            p3 = intersection;
                        }
                    }
                }
            }
        } else {
            return null;
        }

        centroid.setX((p1.getX() + p2.getX() + p3.getX()) / 3);
        centroid.setY((p1.getY() + p2.getY() + p3.getY()) / 3);

        return centroid;
    }

    public static List<Coordinate> intersection(double x1, double y1, double r1,
                                                double x2, double y2, double r2) {

        double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

        if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) < (r1 + r2)) {

        }

        List<Coordinate> points = new ArrayList<>();

        Coordinate coor;

        if (d > r1 + r2 || d < Math.abs(r1 - r2)) {
            return null;
        } else if (x1 == x2 && y1 == y2) {
            return null;
        } else if (y1 == y2 && x1 != x2) {
            double a = ((r1 * r1 - r2 * r2) - (x1 * x1 - x2 * x2)) / (2 * x2 - 2 * x1);
            if (d == Math.abs(r1 - r2) || d == r1 + r2) {
                coor = new Coordinate();
                coor.setX(a);
                coor.setY(y1);
                points.add(coor);
            } else {
                double t = r1 * r1 - (a - x1) * (a - x1);
                coor = new Coordinate();
                coor.setX(a);
                coor.setY(y1 + Math.sqrt(t));
                points.add(coor);
                coor = new Coordinate();
                coor.setX(a);
                coor.setY(y1 - Math.sqrt(t));
                points.add(coor);
            }
        } else if (y1 != y2) {
            double k, disp;
            k = (2 * x1 - 2 * x2) / (2 * y2 - 2 * y1);
            disp = ((r1 * r1 - r2 * r2) - (x1 * x1 - x2 * x2) - (y1 * y1 - y2 * y2))
                    / (2 * y2 - 2 * y1);
            double a, b, c;
            a = (k * k + 1);
            b = (2 * (disp - y1) * k - 2 * x1);
            c = (disp - y1) * (disp - y1) - r1 * r1 + x1 * x1;
            double disc;
            disc = b * b - 4 * a * c;
            if (d == Math.abs(r1 - r2) || d == r1 + r2) {
                coor = new Coordinate();
                coor.setX((-b) / (2 * a));
                coor.setY(k * coor.getX() + disp);
                points.add(coor);
            } else {
                coor = new Coordinate();
                coor.setX(((-b) + Math.sqrt(disc)) / (2 * a));
                coor.setY(k * coor.getX() + disp);
                points.add(coor);
                coor = new Coordinate();
                coor.setX(((-b) - Math.sqrt(disc)) / (2 * a));
                coor.setY(k * coor.getX() + disp);
                points.add(coor);
            }
        }
        return points;
    }
}
