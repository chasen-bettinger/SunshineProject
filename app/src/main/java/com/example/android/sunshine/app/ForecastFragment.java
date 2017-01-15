package com.example.android.sunshine.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Chasen on 1/7/2017.
 */

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;
    public static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    public static final String baseURL = "http://api.openweathermap.org/data/2.5/weather?q=London";
    public static final String api_key = "&APPID=" + BuildConfig.OPEN_WEATHER_API_KEY;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                , R.layout.list_item_forecast
                , R.id.list_item_forecast_textview
                , forecastEntries);


        listView.setAdapter(forecastAdapter);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}




