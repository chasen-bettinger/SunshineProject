package com.example.android.sunshine.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chasen on 1/7/2017.
 */

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;
    public static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        ArrayList<String> forecastEntries = new ArrayList<String>();
        forecastEntries.add("Today - Sunny - 88/63");
        forecastEntries.add("Tomorrow - Foggy - 70/46");
        forecastEntries.add("Wed - Cloudy - 72/63");
        forecastEntries.add("Thurs - Rainy - 64/52");
        forecastEntries.add("Fri - Foggy - 70/46");
        forecastEntries.add("Sat - Sunny - 76/68");


        forecastAdapter = new ArrayAdapter<String>(rootView.getContext()
                ,R.layout.list_item_forecast
                ,R.id.list_item_forecast_textview
                ,forecastEntries);

        Ion.with(this)
                .load("api.openweathermap.org/data/2.5/weather?q=London")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.v(LOG_TAG, result);

                        //logResult(result);
                    }
                });

        listView.setAdapter(forecastAdapter);
        return rootView;
    }

    private static void logResult(String result) {
        try {
            JSONObject json = new JSONObject(result);
            Log.v(LOG_TAG, json.toString());
        }
        catch (JSONException e) {
            Log.wtf(LOG_TAG, e);
        }

    }


}




