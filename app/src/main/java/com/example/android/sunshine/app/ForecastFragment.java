package com.example.android.sunshine.app;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by Chasen on 1/7/2017.
 */

public class ForecastFragment extends Fragment {

    public static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private ArrayAdapter<String> forecastAdapter;
    @BindView(R.id.list_view_forecast)
    ListView listView;
    private Context mContext;
    private FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();


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
        ButterKnife.bind(this, rootView);

        mContext = rootView.getContext();

        forecastAdapter = new ArrayAdapter<String>(mContext
                , R.layout.list_item_forecast
                , R.id.list_item_forecast_textview
                , new ArrayList<String>());

        listView.setAdapter(forecastAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchWeatherTask.getWeatherData();
    }

    @OnItemClick(R.id.list_view_forecast)
    public void sendWeatherData(int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("weather_item", forecastAdapter.getItem(position));
        startActivity(intent);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.map_menu_button) {
            fetchWeatherTask.showMap();
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask {

        public final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


        private static final String baseURL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        private String[] weatherData;
        private double longitude;
        private double latitude;

        FetchWeatherTask() {
        }

        public void showMap() {
            Uri GPSUri = buildGPSUri(longitude, latitude);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(GPSUri);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }

        }

        private Uri buildGPSUri(double longitude, double latitude) {
            final String GEO = "geo:";
            return Uri.parse(GEO + latitude + "," + longitude);
        }


        public void getWeatherData() {

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
            final String OWM_TEMP = "temp";
            final String OWM_MAIN = "main";
            final String OWM_TEMP_MAX = "max";
            final String OWM_TEMP_MIN = "min";
            final String OWM_CITY = "city";
            final String OWM_COORD = "coord";
            final String OWM_LON = "lon";
            final String OWM_LAT = "lat";

            Time time = new Time();
            time.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), time.gmtoff);

            time = new Time();

            JSONObject json = new JSONObject(jsonData);

            longitude = json.getJSONObject(OWM_CITY).getJSONObject(OWM_COORD).getDouble(OWM_LON);
            latitude = json.getJSONObject(OWM_CITY).getJSONObject(OWM_COORD).getDouble(OWM_LAT);
            JSONArray weatherData = json.getJSONArray(OWM_LIST);

            String[] weatherForWeek = new String[7];

            for (int i = 0; i < weatherForWeek.length; i++) {

                String day;
                String description;
                String highAndLow;

                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = time.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                JSONObject currentDay = weatherData.getJSONObject(i);

                // Get description for current item. Ex. Snow, Rain, Cloudy
                description = currentDay.getJSONArray(OWM_WEATHER).getJSONObject(0).getString(OWM_MAIN);

                // Get high temperature for the current day
                double high = currentDay.getJSONObject(OWM_TEMP).getDouble(OWM_TEMP_MAX);
                // Get low temperature for the current day
                double low = currentDay.getJSONObject(OWM_TEMP).getDouble(OWM_TEMP_MIN);

                // Format the high and lows so they look nicer for the output
                highAndLow = formatHighAndLows(high, low);

                // Add to the weatherForWeek array that contains all of the weather information for the
                // current week
                weatherForWeek[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : weatherForWeek) {
                Log.v(LOG_TAG, s);
            }

            return weatherForWeek;
        }

        private String getReadableDateString(long time) {
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        private String formatHighAndLows(double high, double low) {

            int formattedHigh = (int) Math.round(high);
            int formattedLow = (int) Math.round(low);

            String formattedTemp = formattedHigh + " / " + formattedLow;
            return formattedTemp;
        }


        private String buildURL(String baseURL) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

            final String QUERY_PARAM = "q=";
            String zip_code = sharedPreferences.getString(getString(R.string.zip_code_key), "38085");

            final String TYPE_PARAM = "&type=";
            final String TYPE = "accurate";

            final String CNT_PARAM = "&cnt=";
            final String CNT = "7";

            final String UNIT_PARAM = "&units=";
            String unit = sharedPreferences.getString(getString(R.string.temperature_key), "imperial");

            final String api_key = "&APPID=" + BuildConfig.OPEN_WEATHER_API_KEY;

            StringBuffer URL = new StringBuffer(baseURL);

            URL.append(QUERY_PARAM).append(zip_code)
                    .append(TYPE_PARAM).append(TYPE)
                    .append(CNT_PARAM).append(CNT)
                    .append(UNIT_PARAM).append(unit)
                    .append(api_key);

            Log.v(LOG_TAG, zip_code);
            Log.v(LOG_TAG, URL.toString());
            return URL.toString();
        }

    }


}




