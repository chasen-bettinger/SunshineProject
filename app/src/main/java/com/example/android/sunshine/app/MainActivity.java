package com.example.android.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends ActionBarActivity {

    //@BindView(R.id.list_view_forecast) ListView forecastListView;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
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


            ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(getActivity()
                    ,R.layout.list_item_forecast
                    ,R.id.list_item_forecast_textview
                    ,forecastEntries);

            Ion.with(this)
                    .load("api.openweathermap.org/data/2.5/weather?q=London")
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            logResult(result);
                        }
                    });

            listView.setAdapter(forecastAdapter);
            return rootView;
        }


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
