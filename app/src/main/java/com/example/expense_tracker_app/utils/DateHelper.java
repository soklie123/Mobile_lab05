// app/src/main/java/com/example/expense_tracker_app/utils/DateHelper.java
package com.example.expense_tracker_app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return formatter.format(date);
    }
}
