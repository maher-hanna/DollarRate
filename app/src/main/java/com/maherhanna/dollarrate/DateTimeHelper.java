package com.maherhanna.dollarrate;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {
    private static final String TAG = "DateTimeHelper";

    private SimpleDateFormat simpleDateFormat;

    public DateTimeHelper() {
        this.simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
    }

    public String getStringFromDate(Date date){
        return simpleDateFormat.format(date);
    }
    public Date getDateFromString(String date){
        Date result = null;
        try{
            result = simpleDateFormat.parse(date);

        }
        catch (ParseException ex)
        {
            Log.e(TAG, "getDateFromString: Could not parse date");
        }
        return result;

    }

    public Date getDate(int year,int monthOfYear,int dayOfMonth,int hour24Format){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour24Format);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }
}
