package com.example.android.sunshine.app;

import android.app.Activity;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;


/**
 * Created by Chasen on 1/15/2017.
 */

public class FetchWeatherTask {

    private  String URL;
    private String jsonData;
    private Activity mActivity;
    public static final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    public FetchWeatherTask(Activity activity, String URL){
        mActivity = activity;
        this.URL = URL;
    }

    public String[] getWeatherData(){

        String[] weatherData = null;

        Ion.with(mActivity)
                .load(URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        jsonData = result.toString();
                    }
                });


        try {
            weatherData = formatJSONData(jsonData);
        }
        catch (JSONException e)
        {
            Log.wtf(LOG_TAG, e);
        }

        return weatherData;
    }

    private String[] formatJSONData(String jsonData) throws JSONException {

        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_MAIN = "main";
        final String OWM_TEMP_MAX = "temp_max";
        final String OWM_TEMP_MIN = "temp_min";

        Time time = new Time();
        time.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), time.gmtoff);

        time = new Time();

        JSONObject json = new JSONObject(jsonData);
        JSONArray weatherData = json.getJSONArray(OWM_LIST);

        String[] weatherForWeek = new String[7];

        for(int i = 0; i < 7; i++) {

            String day;
            String description;
            String highAndLow;

            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = time.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            JSONObject currentItem = weatherData.getJSONObject(i);

            description = currentItem.getJSONArray(OWM_WEATHER).getJSONObject(0).getString(OWM_MAIN);

            double high = currentItem.getJSONObject(OWM_MAIN).getDouble(OWM_TEMP_MAX);
            double low = currentItem.getJSONObject(OWM_MAIN).getDouble(OWM_TEMP_MIN);

            highAndLow = formatHighAndLows(high, low);

            weatherForWeek[i] = day + " - " + description + " - " + highAndLow;
        }

        for(String s : weatherForWeek) {
            Log.v(LOG_TAG, s);
        }

        return weatherForWeek;
    }

    private String getReadableDateString(long time) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    private String formatHighAndLows(double high, double low) {

        high = Math.round(kelvinToFarenheit(high));
        low = Math.round(kelvinToFarenheit(low));

        String formattedTemp = high + " / " + low;
        return formattedTemp;
    }

    private Double kelvinToFarenheit(double tempInKelvin) {
        Double tempInFarenheit = 9.0/5.0 * (tempInKelvin - 273.0) + 32.0;
        return tempInFarenheit;
    }
}
