package com.maherhanna.dollarrate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String jsonDataSourceUrl;
    TextView tv_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_price = findViewById(R.id.tv_price);

        try {
            InputStream urlFile = getAssets().open("data_source_url.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlFile));
            jsonDataSourceUrl = bufferedReader.readLine();
            bufferedReader.close();
            new DownloadDollarPrice().execute(jsonDataSourceUrl);


        }
        catch (IOException ex){
            ex.printStackTrace();

        }


    }
    class DownloadDollarPrice extends AsyncTask<String, String, String> {

        String price = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                ArrayList<String> lines = new ArrayList<String>();
                String jsonDataSourceUrl = strings[0];
                URL url = new URL(jsonDataSourceUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(8000);
                httpURLConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line = bufferedReader.readLine();
                JSONArray jsonArray = new JSONArray(line);
                price = jsonArray.getJSONObject(0).getString("bid");

                bufferedReader.close();
                return price;


            }
            catch (Exception ex) {
                ex.printStackTrace();


            }
            return "";
        }

        @Override
        protected void onPostExecute(String str) {
            tv_price.setText(price);
            super.onPostExecute(str);
        }

    }
}