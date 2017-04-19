package com.codepath.hungrybird.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gauravb on 4/13/17.
 */

public class DateUtils {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    SimpleDateFormat shortFormat = new SimpleDateFormat("MMM dd, yyyy");
    SimpleDateFormat format2  = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm ");
    Calendar c = Calendar.getInstance();

    public String getDate(Date date) throws ParseException {
        c.setTime(date);
        return shortFormat.format(c.getTime());
    }
}
