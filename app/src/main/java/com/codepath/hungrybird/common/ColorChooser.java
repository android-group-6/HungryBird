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
        colors.put(2, "#66cdaa");
        colors.put(3, "#48a3e6");
        colors.put(4, "#87edec");
        colors.put(5, "#44e3e1");
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
