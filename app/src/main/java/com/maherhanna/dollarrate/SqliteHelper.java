package com.maherhanna.dollarrate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dollar_prices";
    private static final int DATABASE_VERSION = 1;



    public SqliteHelper(@Nullable Context context) {
        
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CreateTableQuery = "CREATE TABLE damascus(time VARCHAR PRIMARY KEY, price REAL);";
        db.execSQL(CreateTableQuery);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void add(String time,Float price){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("price", price);

        // Inserting Row
        db.insert("damascus", null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public List<PriceRcord> getPrices(String fromParam, String toParam){
        SQLiteDatabase db = this.getReadableDatabase();
        List<PriceRcord> prices = new ArrayList<PriceRcord>();
        String[] args = new String[]{fromParam,toParam};
        Cursor cursor = db.rawQuery("SELECT * FROM damascus WHERE time BETWEEN ? AND ?;",args);
        if (cursor.moveToFirst()){
            do {
                PriceRcord priceRcord = new PriceRcord(cursor.getString(0),cursor.getFloat(1));
                prices.add(priceRcord);

            } while(cursor.moveToNext());
        }
        cursor.close();
        return prices;
    }

    public String getLatestDate(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM damascus WHERE time = (SELECT MAX(time) FROM damascus);"
                ,new String[]{});
        if(cursor.getCount() == 0) {
            cursor.close();
            return "";
        }
        else {
            cursor.moveToFirst();
            String result = cursor.getString(0);
            cursor.close();
            return result;
        }


    }

}
