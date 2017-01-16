package com.example.android.sunshine.app;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Chasen on 1/7/2017.
 */

public class ForecastFragment extends Fragment {

    public static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private ArrayAdapter<String> forecastAdapter;
    private Context mContext;


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

        mContext = rootView.getContext();

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        ArrayList<String> forecastEntries = new ArrayList<String>();
        forecastEntries.add("Today - Sunny - 88/63");
        forecastEntries.add("Tomorrow - Foggy - 70/46");
        forecastEntries.add("Wed - Cloudy - 72/63");
        forecastEntries.add("Thurs - Rainy - 64/52");
        forecastEntries.add("Fri - Foggy - 70/46");
        forecastEntries.add("Sat - Sunny - 76/68");

        forecastAdapter = new ArrayAdapter<String>(mContext
                , R.layout.list_item_forecast
                , R.id.list_item_forecast_textview
                , forecastEntries);

        forecastAdapter.setNotifyOnChange(true);

        listView.setAdapter(forecastAdapter);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.getWeatherData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask {

        public final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private static final String baseURL = "http://api.openweathermap.org/data/2.5/forecast?";
        private static final String QUERY_PARAM = "q=";
        private static final String CITY_PARAM = "id=";
        private static final String zip_code = "27235";
        private static final String country_code = "4474221";
        private static final String api_key = "&APPID=" + BuildConfig.OPEN_WEATHER_API_KEY;

        private String[] weatherData;

        FetchWeatherTask(){}

        public void getWeatherData(){

            Ion.with(mContext)
                    .load(buildURL(baseURL))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                weatherData = formatJSONData(result);

                                forecastAdapter.clear();

                                for (String s : weatherData) {
                                    forecastAdapter.add(s);
                                }
                            } catch (JSONException j) {
                                Log.wtf(LOG_TAG, j);
                            }
                        }
                    });
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

            int formattedHigh = (int) Math.round(kelvinToFarenheit(high));
            int formattedLow = (int) Math.round(kelvinToFarenheit(low));

            String formattedTemp = formattedHigh + " / " + formattedLow;
            return formattedTemp;
        }

        private Double kelvinToFarenheit(double tempInKelvin) {
            Double tempInFarenheit = 9.0/5.0 * (tempInKelvin - 273.0) + 32.0;
            return tempInFarenheit;
        }

        private String buildURL(String baseURL) {
            StringBuffer URL = new StringBuffer(baseURL);
            URL.append(CITY_PARAM).append(country_code).append(api_key);
            return URL.toString();
        }

    }


}




