package com.jmengxy.location.algorithms;

import com.jmengxy.location.models.Base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DoGroup {
    private Integer[] a;

    public List<Base> doGroup(List<Base> baseList) {

        Map<String, Group> groupedBases = group(baseList);

        if (groupedBases.size() < 3) {
            return null;
        }

        List<Base> uniqueBases = dealByGroup(groupedBases);

        int len = uniqueBases.size();
        if (len > 3) {
            Collections.sort(uniqueBases);
            return uniqueBases.subList(0, 3);
        }

        return uniqueBases;
    }

    public Map<String, Group> group(List<Base> baseList) {

        Map<String, Group> groupedBases = new HashMap<>();

        Set<String> ids = new HashSet<>();

        for (Base base : baseList) {
            ids.add(base.getId());
        }

        for (String id : ids) {
            groupedBases.put(id, new Group());
        }

        for (Base base : baseList) {
            Group group = groupedBases.get(base.getId());
            if (group == null) {
                group = new Group();
                groupedBases.put(base.getId(), group);
            }

            group.addBase(base);
        }

        return groupedBases;
    }

    public List<Base> dealByGroup(Map<String, Group> groups) {

        double distance;

        List<Base> bases = new ArrayList<>();

        int baseNum = groups.size();

        a = new Integer[baseNum];

        int k = 0;

        @SuppressWarnings("rawtypes")
        Iterator it = groups.keySet().iterator();

        while (it.hasNext()) {

            String id = (String) it.next();

            Group g = groups.get(id);

            int len = g.getBases().size();

            int len2 = len / 4;

            if (len >= 4) {
                double count = 0;
                for (int i = len2; i < len - len2; i++) {
                    count += g.getBases().get(i).getDistance();
                }
                distance = count / (len - 2 * len2);
            } else if (len == 1) {
                distance = g.getBases().get(0).getDistance();
            } else {
                distance = getMedian(g.getBases());
            }

            Base base = new Base(id, g.getBases().get(0).getLocation(), g.getBases().get(0).getHeight(), distance);
            bases.add(base);

            a[k] = k;
            k++;
        }

        return bases;
    }

    public double getMedian(List<Base> ls) {
        double m;

        Collections.sort(ls);

        if (ls.size() % 2 == 0) {
            m = (ls.get((ls.size() / 2) - 1).getDistance() + ls.get(ls.size() / 2).getDistance()) / 2;
        } else {
            m = (ls.get(ls.size() / 2).getDistance());
        }
        return m;
    }

    public Integer[] getA() {
        return a;
    }

    public void setA(Integer[] a) {
        this.a = a;
    }

    private class Group {
        private List<Base> bases = new ArrayList<>();

        public List<Base> getBases() {
            return bases;
        }

        public void setBases(List<Base> bases) {
            this.bases = bases;
        }

        public void addBase(Base base) {
            bases.add(base);
        }
    }
}
