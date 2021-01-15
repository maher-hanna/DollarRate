package com.maherhanna.dollarrate;

import android.content.Context;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DataBase {
    private Context context;
    private SqliteHelper sqliteHelper;

    public DataBase(Context context) {

        this.context = context;
        sqliteHelper = new SqliteHelper(context);
    }

    public void update() {

        DateTimeHelper dateTimeHelper = new DateTimeHelper();

        Date to = new Date();

        String latestStoredDate = sqliteHelper.getLatestDate();
        if (latestStoredDate == "") {
            //local database is empty
            //download all records in remote database
            Date from = DateTimeHelper.getDate(2021,1,1,1);
            downloadRemoteDatabase(from, to);
        } else {
            //download only new records from remote database

            Date from = dateTimeHelper.getDateFromString(latestStoredDate);

            if(dateTimeHelper.isHourNewer(latestStoredDate,dateTimeHelper.getStringFromDate(to))){
                downloadRemoteDatabase(from, to);

            }
            else {
                ((MainActivity)context).databaseUpdated();
            }


        }


        return;

    }
    public List<Entry> getStoredPrices(Date from,Date to){
        DateTimeHelper dateTimeHelper = new DateTimeHelper();
        List<Entry> entries = new ArrayList<Entry>();
        String fromParam = dateTimeHelper.getStringFromDate(from);
        String toParam = dateTimeHelper.getStringFromDate(to);
        List<PriceRcord> priceRcords  = sqliteHelper.getPrices(fromParam,toParam);
        for (PriceRcord record: priceRcords) {
            Entry entry = new Entry(dateTimeHelper.getDateFromString(record.time).getTime()
                    ,record.price);
            entries.add(entry);


        }
        return entries;

    }

    private void downloadRemoteDatabase(Date from, Date to) {
        final DateTimeHelper dateTimeHelper = new DateTimeHelper();

        String fromParam = dateTimeHelper.getStringFromDate(from);
        String toParam = dateTimeHelper.getStringFromDate(to);
        DownloadPricesList downloadPricesList = new DownloadPricesList() {
            @Override
            protected void onPostExecute(String s) {
                try {

                    JSONObject jsonResponse = new JSONObject(s);
                    Iterator<String> keysItr = jsonResponse.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = jsonResponse.getString(key);
                        Date date = dateTimeHelper.getDateFromString(key);
                        float price = Float.parseFloat(value);
                        sqliteHelper.add(key, price);

                    }
                    ((MainActivity)context).databaseUpdated();


                } catch (Exception ex) {
                    ((MainActivity)context).databaseUpdated();
                }
            }
        };
        downloadPricesList.execute(fromParam, toParam);

    }
}
