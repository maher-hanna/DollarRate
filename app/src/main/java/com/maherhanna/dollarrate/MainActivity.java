package com.maherhanna.dollarrate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    String jsonDataSourceUrl;
    TextView tv_price;
    DownloadPricesList downloadPricesList;
    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_price = findViewById(R.id.tv_price);
        lineChart = findViewById(R.id.lineChart);


    }

    @Override
    protected void onStart() {
        super.onStart();


        jsonDataSourceUrl = "https://sp-today.com/app_api/cur_damascus.json";

        new DownloadDollarPrice().execute(jsonDataSourceUrl);

        getPrices();


    }

    void getPrices() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.YEAR, 2021);
        Date from = calendar.getTime();

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.YEAR, 2021);

        Date to = calendar.getTime();

        final SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        final String fromParam = dateFormat.format(from);
        final String toParam = dateFormat.format(to);

        downloadPricesList =  new DownloadPricesList() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    List<Entry> priceList = new ArrayList<>();
                    JSONObject jsonResponse = new JSONObject(s);
                    Iterator<String> keysItr = jsonResponse.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = jsonResponse.getString(key);
                        Date date = dateFormat.parse(key);
                        float price = Float.parseFloat(value);
                        priceList.add(new Entry((float)date.getTime(),price));


                    }
                    drawCurrentMonth(priceList);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        downloadPricesList.execute(fromParam, toParam);

    }

    private void drawCurrentMonth(List<Entry> priceList) {
        LineDataSet lineDataSet = new LineDataSet(priceList,"lineDataSet");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(downloadPricesList != null){
            if(downloadPricesList.isCancelled() != true){
                downloadPricesList.cancel(true);
            }

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


            } catch (Exception ex) {
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