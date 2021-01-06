package com.maherhanna.dollarrate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
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
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
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
    DateTimeHelper dateTimeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_price = findViewById(R.id.tv_price);
        lineChart = findViewById(R.id.lineChart);

        dateTimeHelper = new DateTimeHelper();


    }

    @Override
    protected void onStart() {
        super.onStart();


        jsonDataSourceUrl = "https://sp-today.com/app_api/cur_damascus.json";
        Date date = dateTimeHelper.getDateFromString("jijr");

        new DownloadDollarPrice().execute(jsonDataSourceUrl);

        getPrices();


    }

    void getPrices() {

        Date from = dateTimeHelper.getDate(2021,1,1,14);
        
        Date to = dateTimeHelper.getDate(2021,1,4,15);


        final String fromParam = dateTimeHelper.getStringFromDate(from);
        final String toParam = dateTimeHelper.getStringFromDate(to);

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
                        Date date = dateTimeHelper.getDateFromString(key);
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
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.RED);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        final XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                xAxis.setLabelCount(30,true);
                Date date = new Date((long)value);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                return simpleDateFormat.format(date);

            }
        });

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