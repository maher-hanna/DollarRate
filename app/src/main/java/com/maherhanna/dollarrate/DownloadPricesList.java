package com.maherhanna.dollarrate;

import android.os.AsyncTask;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


class DownloadPricesList extends AsyncTask<String, Void, String> {


    @Override
    protected String doInBackground(String... strings) {

        final String fromParam = strings[0];
        final String toParam = strings[1];



        String response = "";
        try {
            URL url = new URL(GlobalParameters.WebDatabaseUrl + "?" + "from=" + fromParam
            + "&to=" + toParam);
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


