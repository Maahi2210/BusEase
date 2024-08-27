package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Weather_Activity extends AppCompatActivity {
    private TextView cityName, show, weatherforecast;
    private Button search, logout, editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);
        weatherforecast = findViewById(R.id.forecast);
        logout = findViewById(R.id.logout);
        editProfileButton = findViewById(R.id.editProfileButton);

        search.setOnClickListener(v -> {
            String city = cityName.getText().toString();
            if (!city.isEmpty()) {
                String url = "https://api.openweathermap.org/data/2.5/weather?q="
                        + city + "&appid=e604b95ee8eb1ee1abc5944cdec0a7e1";
                new GetWeatherTask().execute(url);
            } else {
                Toast.makeText(Weather_Activity.this, "Enter City", Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Weather_Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Weather_Activity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private class GetLatLongTask extends android.os.AsyncTask<String, Void, String> {

        private String weatherInfo;

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                System.out.println("Connecting to URL: " + url.toString());

                int responseCode = urlConnection.getResponseCode();

                System.out.println("Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    System.out.println("Result: " + result.toString());
                } else {
                    System.out.println("Error: Server responded with code " + responseCode);
                    return null;
                }

                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            JSONObject location = jsonArray.getJSONObject(0);
                            double lat = location.getDouble("lat");
                            double lon = location.getDouble("lon");

                            String forecastUrl =
                                    "https://api.openweathermap.org/data/2.5/forecast?lat="
                                            + lat + "&lon=" + lon + "&cnt=5&appid=a78fd19b7a04bdc3a8204f6cc8b196ad\n";
                            new GetForecastTask().execute(forecastUrl);
                        } else {
                            weatherforecast.setText("Location not found.");
                        }
                    } catch (Exception e) {
                        weatherforecast.setText("Error parsing location data.");
                        e.printStackTrace();
                    }
                } else {
                    weatherforecast.setText("Error retrieving location data.");
                }
            }
    }
    private class GetForecastTask extends android.os.AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray dailyForecasts = jsonObject.getJSONArray("list");

                    StringBuilder forecastInfo = new StringBuilder();
                    for (int i = 0; i < dailyForecasts.length(); i++) {
                        JSONObject dayForecast = dailyForecasts.getJSONObject(i);

                        long dt = dayForecast.getLong("dt");
                        String date = new java.text.SimpleDateFormat("EEE, MMM d, HH:mm",
                                java.util.Locale.getDefault())
                                .format(new java.util.Date(dt * 1000));

                        JSONObject main = dayForecast.getJSONObject("main");
                        double temp = main.getDouble("temp") - 273.15;
                        double tempMin = main.getDouble("temp_min") - 273.15;
                        double tempMax = main.getDouble("temp_max") - 273.15;
                        int humidity = main.getInt("humidity");
                        int pressure = main.getInt("pressure");

                        JSONObject weatherObject = dayForecast.getJSONArray("weather").getJSONObject(0);
                        String description = weatherObject.getString("description");

                        JSONObject wind = dayForecast.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");

                        String rainInfo = "";
                        if (dayForecast.has("rain")) {
                            JSONObject rain = dayForecast.getJSONObject("rain");
                            double rainVolume = rain.optDouble("3h", 0);
                            rainInfo = "Rain: " + rainVolume + "mm\n";
                        }

                        forecastInfo.append(String.format(
                                "%s\nTemperature: %.2f°C (Min: %.2f°C, Max: %.2f°C)\nHumidity: %d%%\nPressure: %d hPa\nWind Speed: %.2f m/s\n%sDescription: %s\n\n",
                                date, temp, tempMin, tempMax, humidity, pressure, windSpeed, rainInfo, description));
                    }
                    weatherforecast.setText(forecastInfo.toString());
                } catch (Exception e) {
                    weatherforecast.setText("Error parsing forecast data.");
                    e.printStackTrace();
                }
            } else {
                weatherforecast.setText("Error retrieving forecast data.");
            }
        }
    }
    private class GetWeatherTask extends android.os.AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject main = jsonObject.getJSONObject("main");

                    double tempCelsius = main.getDouble("temp") - 273.15;
                    double feelsLikeCelsius = main.getDouble("feels_like") - 273.15;
                    double tempMaxCelsius = main.getDouble("temp_max") - 273.15;
                    double tempMinCelsius = main.getDouble("temp_min") - 273.15;

                    String weatherInfo = String.format(
                            "Temperature: %.2f°C\nFeels Like: %.2f°C\nTemperature Max: %.2f°C\nTemperature Min: %.2f°C\nPressure: %s hPa\nHumidity: %s%%",
                            tempCelsius, feelsLikeCelsius, tempMaxCelsius, tempMinCelsius,
                            main.getString("pressure"), main.getString("humidity")
                    );

                    show.setText(weatherInfo);

                    String city = cityName.getText().toString();

                    String geoUrl = "https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=a78fd19b7a04bdc3a8204f6cc8b196ad\n";
                    new GetLatLongTask().execute(geoUrl);
                } catch (Exception e) {
                    show.setText("Error parsing weather data.");
                    e.printStackTrace();
                }
            } else {
                show.setText("Error retrieving weather data.");
            }
        }
    }
}
