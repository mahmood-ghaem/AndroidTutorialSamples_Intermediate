package nl.mahmood.memorableplaceexercise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class WeatherActivity extends AppCompatActivity implements LocationResultListener
{
    //handle permission result
    private final int PERMISSION_REQUEST = 1000;
    //handle enable location result
    private final int LOCATION_REQUEST_CODE = 2000;
    private LocationHandler locationHandler;

    TextView textViewWeather;
    TextView textViewWeatherName;
    ImageView imageViewWeatherIcon;
    TextView textViewPhotographer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textViewWeather = findViewById(R.id.textViewWeather);
        textViewWeatherName = findViewById(R.id.textViewWeatherName);
        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon);
        textViewPhotographer = findViewById(R.id.textViewPhotographer);

        locationHandler = new LocationHandler(this, this, LOCATION_REQUEST_CODE, PERMISSION_REQUEST);
        Intent intent = getIntent();
        int placeNumber = intent.getIntExtra("PlaceNumber", 0);
        if (placeNumber == 0)
        {
            locationHandler.getUserLocation();
        }
        else
        {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locations.get(
                    intent.getIntExtra("PlaceNumber", 0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(
                    intent.getIntExtra("PlaceNumber", 0)).longitude);
            locationHandler.setCustomLocation(placeLocation, MainActivity.places.get(
                    intent.getIntExtra("PlaceNumber", 0)));
        }
        showPhotographer();
    }

    private void showPhotographer()
    {
        String photographer = "Photographer: <b><i><a href=\"https://unsplash.com/@";
        photographer += "kevinmueller\">Kevin Mueller";
        photographer += "</a></i></b></p>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            textViewPhotographer.setText(Html.fromHtml(photographer, Html.FROM_HTML_MODE_COMPACT));
        }
        else
        {
            textViewPhotographer.setText(Html.fromHtml(photographer));
        }
        textViewPhotographer.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @SuppressLint("MissingPermission")
    @Override
    public void getLocation(Location location, String title)
    {

        try
        {
            DownloadTask task = new DownloadTask();
            // TODO (2) input your openweathermap API key here you can get free from www.openweathermap.org
            task.execute("http://api.openweathermap.org/data/2.5/weather?lat=" +
                    location.getLatitude() + "&lon=" + location.getLongitude() +
                    "&units=metric&appid=place your api key here");
        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls)
        {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try
            {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;


            } catch (Exception e)
            {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();

                return null;
            }

        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            String message = "";
            String icon = "";

            try
            {
                JSONObject jsonObject = new JSONObject(s);

                String locationName = jsonObject.getString("name");

                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(weatherInfo);

                JSONObject mainInfo = jsonObject.getJSONObject("main");
                String temp = mainInfo.getString("temp");
                String feels_like = mainInfo.getString("feels_like");

                JSONObject windInfo = jsonObject.getJSONObject("wind");
                String speed = windInfo.getString("speed");


                for (int i = 0; i < jsonArray.length(); i++)
                {

                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    icon = jsonPart.getString("icon");

                    if (!main.equals("") && !description.equals(""))
                    {
                        message += "<p><b>Weather</b> :  " + main + ", " + description + "</p>";
                    }
                }
                message += "<p><b>Temperature</b> :  " + temp + " °C</p>";
                message += "<p><b>Feels like</b> :  " + feels_like + " °C</p>";
                message += "<p><b>Wind Speed</b> :  " + speed + " meter/sec</p><br>";
                message += "<p><i><a href=\"https://openweathermap.org/find?q=" + locationName + "\">Read more</a></i></p>";

                if (!message.equals(""))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        textViewWeather.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT));
                    }
                    else
                    {
                        textViewWeather.setText(Html.fromHtml(message));
                    }

                    textViewWeather.setMovementMethod(LinkMovementMethod.getInstance());
                    ImageDownloader imageTask = new ImageDownloader();
                    Bitmap iconImage = imageTask.execute("http://openweathermap.org/img/wn/" + icon + "@2x.png").get();
                    imageViewWeatherIcon.setImageBitmap(iconImage);
                    textViewWeatherName.setText(locationName);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e)
            {

                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

        }
    }

}
