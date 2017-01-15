package com.example.android.sunshine.app;

import android.content.Context;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by Chasen on 1/15/2017.
 */

public class FetchWeatherTask {

    private static Context mContext;
    private static String URL;
    private static JsonObject jsonData;

    public FetchWeatherTask(Context context, String URL){
        mContext = context;
        this.URL = URL;
    }


        /*
        {"coord":{"lon":-0.13,"lat":51.51}
        ,"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}]
        ,"base":"stations"
        ,"main":{"temp":281.82,"pressure":1012,"humidity":93,"temp_min":281.15,"temp_max":282.15}
        ,"visibility":10000
        ,"wind":{"speed":7.7,"deg":190}
        ,"clouds":{"all":75},"dt":1483969800
        ,"sys":{"type":1,"id":5187
        ,"message":0.2086
        ,"country":"GB","sunrise":1483948990,"sunset":1483978386},"id":2643743,"name":"London","cod":200}

         */

    public static JsonObject getWeatherData() {

        jsonData = null;

        Ion.with(mContext)
                .load(URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            jsonData = result;
                        }
                        else {
                            return;
                        }
                    }
                });

        return jsonData;
    }
}
