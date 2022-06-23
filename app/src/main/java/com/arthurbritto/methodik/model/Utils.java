package com.arthurbritto.methodik.model;

import com.arthurbritto.methodik.R;

public final class Utils {

    /**
     * Associates a color to a task or panel by the color Id.
     */
    public static int colorSelector(int colorId) {
        if (colorId == 1) return R.color.red_background;
        else if (colorId == 2) return R.color.green_background;
        else if (colorId == 3) return R.color.blue_background;
        else return R.color.black; //default background
    }
}
