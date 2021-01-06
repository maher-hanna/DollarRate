package com.maherhanna.dollarrate;

import java.util.Date;

public class GlobalParameters {
    static final String WebDatabaseUrl =  "https://maher802.000webhostapp.com/dollar_prices.php";
    static final String PriceNowUrl = "https://sp-today.com/app_api/cur_damascus.json";
    static boolean DatabaseUpdated = false;
    static final Date StartDateOfDatabase =  DateTimeHelper.getDate(2021,1,1,1);

}
