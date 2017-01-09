package com.example.android.sunshine.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private ArrayAdapter<String> forecastAdapter;
    public static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    public static final String baseURL = "api.openweathermap.org/data/2.5/weather?q=London";
    public static final String api_key = "&APPID=" + BuildConfig.OPEN_WEATHER_API_KEY;

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

        Ion.with(this)
                .load(api_key)
                .setLogging(LOG_TAG, Log.DEBUG)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(result != null) {
                            Log.v(LOG_TAG, result);
                        }


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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.menu.forecastfragment) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}




