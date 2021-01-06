package com.maherhanna.dollarrate;

public class DataBase {
    public void update(){
        if(!GlobalParameters.DatabaseUpdated){

            GlobalParameters.DatabaseUpdated = true;
        }

    }
}
