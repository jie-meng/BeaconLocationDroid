package com.jmengxy.location;

import com.jmengxy.location.models.Base;
import com.jmengxy.location.models.Location;

import java.util.List;

public interface Locator {
    Location getLocation(List<Base> bases);
}
