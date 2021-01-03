package com.maherhanna.dollarrate;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


class DownloadPricesList extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... strings) {
        final String databaseApiUrl = "https://maher802.000webhostapp.com/dollar_prices.php";

        final String fromParam = strings[0];
        final String toParam = strings[1];



        String response = "";
        try {


            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("from",fromParam)
                    .appendQueryParameter("to",toParam);
            String query = builder.build().getEncodedQuery();

            URL url = new URL(databaseApiUrl + "?" + query);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(8000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                response += line;

            }


            bufferedReader.close();
            httpURLConnection.disconnect();

        } catch (Exception ex) {
            ex.printStackTrace();


        }

        return response;
    }
}


