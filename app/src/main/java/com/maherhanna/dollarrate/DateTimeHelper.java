package com.maherhanna.dollarrate;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeHelper {
    private static final String TAG = "DateTimeHelper";

    private SimpleDateFormat simpleDateFormat;

    public DateTimeHelper() {
        this.simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
    }
    boolean isHourNewer(String fromDate,String toDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        try{
            Date from = simpleDateFormat.parse(fromDate);
            Date to  = simpleDateFormat.parse(toDate);
            if(to.after(from)){
                return true;

            }
            else {
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;

        }



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
    Date getStartOfCurrentMonth(){
        Calendar calendar = Calendar.getInstance();   // this takes current date
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }


    public static Date getDate(int year,int monthOfYear,int dayOfMonth,int hour24Format){
        TimeZone utc = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(utc);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour24Format);
        //Decrease month by one because in Calender it's zero based
        calendar.set(Calendar.MONTH, monthOfYear - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.YEAR, year);
        Date result = calendar.getTime();
        return result;
    }
}
