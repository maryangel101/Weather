package com.example.weather;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** @noinspection ALL*/
public class MainActivity extends AppCompatActivity {
    TextView cityName;
    Button search;
    TextView show;
    String url;

    /** @noinspection deprecation*/
    @SuppressLint("StaticFieldLeak")
    class GetWeather extends AsyncTask<String, Void, String> {
        /** @noinspection deprecation*/
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                return null;
            }
        }

        /** @noinspection deprecation, deprecation */
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject main = jsonObject.getJSONObject("main");
                    String weatherInfo = main.toString();
                    weatherInfo = weatherInfo.replace("\"temp\"", "Temperature");
                    weatherInfo = weatherInfo.replace("\"feels_like\"", "Feels Like");
                    weatherInfo = weatherInfo.replace("\"temp_max\"", "Temperature Max");
                    weatherInfo = weatherInfo.replace("\"temp_min\"", "Temperature Min");
                    weatherInfo = weatherInfo.replace("\"pressure\"", "Pressure");
                    weatherInfo = weatherInfo.replace("\"humidity\"", "Humidity");
                    weatherInfo = weatherInfo.replace("\"sea_level\"", "Sea Level");
                    weatherInfo = weatherInfo.replace("\"grnd_level\"", "Ground Level");

                    weatherInfo = weatherInfo.replace("{", "");
                    weatherInfo = weatherInfo.replace("}", "");
                    weatherInfo = weatherInfo.replace(",", "\n");
                    weatherInfo = weatherInfo.replace(":", " : ");

                    show.setText(weatherInfo);
                } else {
                    show.setText("Cannot find the weather");
                }
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                show.setText("Error parsing weather data");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        search.setOnClickListener(new View.OnClickListener() {
            /** @noinspection deprecation*/
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                if (!city.isEmpty()) {
                    //noinspection SpellCheckingInspection
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=24991040bc1c17696cc24c8b99268672";
                    try {
                        GetWeather task = new GetWeather();
                        task.execute(url);
                    } catch (Exception e) {
                        //noinspection CallToPrintStackTrace
                        e.printStackTrace();
                        show.setText("Error fetching weather data");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
