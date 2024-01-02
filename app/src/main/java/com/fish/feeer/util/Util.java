package com.fish.feeer.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class Util {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getLastFedTime(long epochTime) {

        if(epochTime == 0) {
            return "";
        }

        long currentEpochTime = Instant.now().getEpochSecond() + (8 * 3600);
        long timeDifference = (currentEpochTime - epochTime);

        if(timeDifference < 60 && timeDifference >= 0) {

            return timeDifference == 1 ? "1 second ago" : timeDifference + " seconds ago";

        } else if(timeDifference < 60 * 60 && timeDifference >= 60) {

            int minutes = (int) (timeDifference / 60);
            int remainder = (int) (timeDifference % 60);

            return minutes == 1 && remainder == 0 ? "1 min & 0 sec ago" : minutes + " min & " + remainder + " sec ago";

        } else if (timeDifference < 60 * 60 * 24 && timeDifference >= (60 * 60)) {

            int hours = (int) (timeDifference / 3600);
            int remainder = (int) ((timeDifference % 3600) / 60);

            return hours == 1 && remainder == 0 ? "1 hr & 0 min ago" : hours + " hr & " + remainder + " min ago";

        } else {

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC+8"));

            return sdf.format(new Date(epochTime * 1000));

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getFeedTime(String format) throws ParseException {

        if(format == null || format.isEmpty()) {
            return "";
        }

        long epochTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH).parse(format).getTime() / 1000;
        long currentEpochTime = Instant.now().getEpochSecond();
        long timeDifference = (epochTime - currentEpochTime);

        if(timeDifference < 0)
            return "No Scheduled time";

        if(timeDifference < 60 && timeDifference >= 0) {

            return timeDifference == 1 ? "1 second to go" : timeDifference + " seconds to go";

        } else if(timeDifference < 60 * 60 && timeDifference >= 60) {

            int minutes = (int) (timeDifference / 60);
            int remainder = (int) (timeDifference % 60);

            return minutes == 1 && remainder == 0 ? "1 min & 0 sec to go" : minutes + " min & " + remainder + " sec to go";

        } else if (timeDifference < 60 * 60 * 24 && timeDifference >= (60 * 60)) {

            int hours = (int) (timeDifference / 3600);
            int remainder = (int) ((timeDifference % 3600) / 60);

            return hours == 1 && remainder == 0 ? "1 hr & 0 min to go" : hours + " hr & " + remainder + " min to go";

        } else {

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd hh:mm a");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC+8"));

            return sdf.format(new Date(epochTime * 1000));

        }

    }

    public static void setGradientBackground(View view, String startColor, String endColor, float radius, boolean ripple) {

        /*view.setBackground(new GradientDrawable() {
            public GradientDrawable gradientDrawable() {
                this.setOrientation(Orientation.LEFT_RIGHT);
                this.setColors(new int[] {Color.parseColor(startColor), Color.parseColor(endColor)});
                this.setCornerRadius(radius);
                return this;
            }
        }.gradientDrawable());*/

        GradientDrawable gradientDrawable = new GradientDrawable();

        gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientDrawable.setColors(new int[] {Color.parseColor(startColor), Color.parseColor(endColor)});
        gradientDrawable.setCornerRadius(radius);

        if(ripple) {

            view.setClickable(true);
            view.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.parseColor(startColor)), gradientDrawable, null));

        } else {

            view.setBackground(gradientDrawable);

        }

    }

}
