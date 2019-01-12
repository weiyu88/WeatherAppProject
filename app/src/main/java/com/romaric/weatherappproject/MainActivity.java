package com.romaric.weatherappproject;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
//import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Debug";

    TextView cityTextView, dateTextView, weatherTextView, humidityTextView, pressureTextView, tempTextView;
    ImageView weatherImageView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//        ComponentName componentName = new ComponentName(MainActivity.this, SearchResultsActivity.class);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);

        String[] string_array = getResources().getStringArray(R.array.city);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,string_array);
        searchAutoComplete.setAdapter(arrayAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString = (String) adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText(""+queryString);
                Toast.makeText(MainActivity.this, queryString,Toast.LENGTH_LONG).show();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCity(query);

//                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextView = findViewById(R.id.cityTextView);
        dateTextView = findViewById(R.id.dateTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        tempTextView = findViewById(R.id.tempTextView);
        weatherImageView = findViewById(R.id.weatherImageView);


        searchCity("Petaling Jaya");

//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        //LocationListener locationListener = new MyLocationListener();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

    }


//    private class MyLocationListener implements LocationListener{
//
//        @Override
//        public void onLocationChanged(Location loc) {
//            Toast.makeText(
//                        getBaseContext(),
//                        "Location changed: Lat: " + loc.getLatitude() + " Lng: "
//                                + loc.getLongitude(), Toast.LENGTH_SHORT).show();
//                String longitude = "Longitude: " + loc.getLongitude();
//                Log.v(TAG,longitude);
//                String latitude = "Latitude: " + loc.getLatitude();
//                Log.v(TAG,latitude);
//
//                String cityName = null;
//                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
//                List<Address> addresses;
//                try {
//                    addresses = gcd.getFromLocation(loc.getLatitude(),
//                            loc.getLongitude(), 1);
//                    if (addresses.size() > 0) {
//                        System.out.println(addresses.get(0).getLocality());
//                        cityName = addresses.get(0).getLocality();
//                    }
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//                String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
//                        + cityName;
//                searchCity(cityName);
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//    }

    public String searchCity(String city){
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=3315720001601d46a16a5afebcd97725";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String cityWeather = jsonObject.getString("name");
                            cityTextView.setText(cityWeather);

                            int unix = jsonObject.getInt("dt");
                            Date date = new Date(unix * 1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss z");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                            String formattedDate = simpleDateFormat.format(date);
                            dateTextView.setText(formattedDate);

                            String image = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                            String url = "https://openweathermap.org/img/w/" + image + ".png";
                            Glide.with(getApplicationContext()).load(url).into(weatherImageView);

                            String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                            weatherTextView.setText(weather);

                            double temp = jsonObject.getJSONObject("main").getDouble("temp");
                            double celsius = temp - 273.16;
                            String df2 = String.format("%.2f", celsius);
                            tempTextView.setText(df2 + "\u2103");

                            int pressure = jsonObject.getJSONObject("main").getInt("pressure");
                            pressureTextView.setText("Pressure: " + pressure + " hPa");

                            int humidity = jsonObject.getJSONObject("main").getInt("humidity");
                            humidityTextView.setText("Humidity: " + humidity + "%");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        queue.add(stringRequest);
        return city;
    }

}
