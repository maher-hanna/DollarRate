package com.maherhanna.dollarrate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tv_price;
    DownloadPricesList downloadPricesList;
    LineChart lineChart;
    DateTimeHelper dateTimeHelper;
    DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_price = findViewById(R.id.tv_price);
        lineChart = findViewById(R.id.lineChart);

        dateTimeHelper = new DateTimeHelper();
        dataBase = new DataBase(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        new DownloadDollarPrice().execute(GlobalParameters.PriceNowUrl);

        dataBase.update();

    }
    public void databaseUpdated(){
        drawCurrentMonth(dataBase.getStoredPrices(dateTimeHelper.getStartOfMonth(new Date()),new Date()));
    }



    private void drawCurrentMonth(List<Entry> priceList) {
        LineDataSet lineDataSet = new LineDataSet(priceList,getString(R.string.damascus_price));

        //Decorations
        lineChart.getDescription().setText(getString(R.string.current_month_chart));
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.RED);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);


        //


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        final XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMaximum((float)dateTimeHelper.getEndOfMonth(new Date()).getTime());
        //xAxis.setLabelCount(dateTimeHelper.getNumberOfDaysInMonth(new Date()),true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
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
            if(str.isEmpty()){
                tv_price.setTextColor(Color.RED);
                tv_price.setText(getString(R.string.message_error_price));
            }
            else{
                tv_price.setText(price);

            }
            super.onPostExecute(str);
        }

    }
}