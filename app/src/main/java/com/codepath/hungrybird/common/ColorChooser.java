package com.codepath.hungrybird.common;

import java.util.HashMap;

/**
 * Created by gauravb on 3/19/17.
 */

public class ColorChooser {
    public static HashMap<Integer, String> colors = new HashMap<>();
    static {
        colors.put(0, "#FE5F55");
        colors.put(1, "#E63462");
        colors.put(2, "#7f35ce");
        colors.put(3, "#208cdb");
        colors.put(4, "#148943");
        colors.put(5, "#e36e44");
    }
    public ColorChooser() {

    }

    public String getColor(String newsDesk) {
        long code = newsDesk.hashCode();
        if (code < 0) {
            code = -code;
        }
        long key = code%colors.size();
        return colors.get((int)key);
    }
}
