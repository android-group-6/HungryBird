package com.codepath.hungrybird.common;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by dshah on 5/7/2017.
 */

public class NotificationUtil {

    public NotificationUtil() {

    }

    public static void showSnackBar(View parentView, String snackBarText) {
        Snackbar.make(parentView, snackBarText, Snackbar.LENGTH_LONG).show();
    }
}
