package com.enginious.snowblossom;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by waleed on 11/9/18.
 */

public class TimeAgo {

    public static String covertTimeToText(Date pasTime, Context context) {

        String convTime = null;

        String prefix = "";
        String suffix = "";

        try {


            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second+" "+ context.getString(R.string.title_seconds_ago) ;
            } else if (minute < 60) {
                convTime = minute+" "+ context.getString(R.string.title_minutes_ago);
            } else if (hour < 24) {
                convTime = hour+" "+ context.getString(R.string.title_hours_ago);
            } else if (day >= 7) {
                if (day > 30) {
                    convTime = (day / 30)+" "+ context.getString(R.string.title_months_ago);
                } else if (day > 360) {
                    convTime = (day / 360)+" "+ context.getString(R.string.title_years_ago);
                } else {
                    convTime = (day / 7) + " "+ context.getString(R.string.title_week_ago);
                }
            } else if (day < 7) {
                convTime = day+" "+ context.getString(R.string.title_days_ago);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;

    }
}
