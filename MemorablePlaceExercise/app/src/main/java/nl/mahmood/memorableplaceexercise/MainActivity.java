package nl.mahmood.memorableplaceexercise;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO (1) input your google map key here you can get from google
 * TODO (2) input your openweathermap API key here you can get free from www.openweathermap.org
 * TODO (3) set uses permissions
 * 4 - create FileHelper, ImageDownloader, LocationHandler, LocationResultListener classes
 *      you can get information about LocationHandler, LocationResultListener from this link:
 *      https://mobiledevhub.com/2018/10/27/android-getting-user-location/
 * This is the fourth intermediate level tutorial that I put on the site.
 * If you have followed the tutorials so far, in previous sources,
 * I would have numbered all the steps for writing code.
 * But I didn't do that in this source because I think it's clear.
 * If there was a question, I'd be happy to ask and I'll answer quickly.
 * pejvakco@gmail.com
 */

public class MainActivity extends AppCompatActivity
{
    Context context = this;
    ListView listView;
    RadioGroup radioGroup;
    static ArrayList<String> places;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<LatLng> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        radioGroup = findViewById(R.id.radioGroup);
        places = new ArrayList<String>();
        locations = new ArrayList<LatLng>();



        fillListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent;
                switch (radioGroup.getCheckedRadioButtonId())
                {
                    case R.id.radioButtonWeather:
                        intent = new Intent(context, WeatherActivity.class);
                        break;
                    default:
                        intent = new Intent(context, MapsActivity.class);
                        break;
                }
                intent.putExtra("PlaceNumber", position);
                startActivity(intent);
            }
        });
    }

    public void fillListView()
    {
        places.clear();
        locations.clear();
        if (FileHelper.readFileAsString(context) != "")
        {
            try
            {
                JSONArray jsonArray = new JSONArray(FileHelper.readFileAsString(context));
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    //places.set(i, jsonObject.getString("places"));
                    places.add(i, jsonObject.getString("places"));
                    locations.add(i, new LatLng(
                            Double.parseDouble(jsonObject.getString("latitude")),
                            Double.parseDouble(jsonObject.getString("longitude"))));

                }

            } catch (JSONException e)
            {
                e.printStackTrace();
                FileHelper.writeStringAsFile(context, "");
            }
        }
        else
        {

            places.add("Select Cities on map");
            locations.add(new LatLng(0, 0));
        }


        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (places.size() == 1)
        {
            radioGroup.setVisibility(View.INVISIBLE);
            radioGroup.check(R.id.radioButtonShowMap);
        }
        else
        {
            radioGroup.setVisibility(View.VISIBLE);
            places.set(0,"Your Current Location");
        }
    }

    //=================================Define methods for option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.clear:
                clearListView();
                break;
            case R.id.about:
                showAboutDialog();
                break;
            case R.id.save:
                try
                {
                    saveCities();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;

            case R.id.help:
                showHelpDialog();
                break;
            case R.id.exit:
                showExitDialog();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_about, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("About");
        alertDialog.show();
    }

    private void showHelpDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_help, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("Help");
        alertDialog.show();
    }

    private void showExitDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.outline_info_black_18dp);
        alertDialogBuilder.setTitle("Exit Dialog");
        alertDialogBuilder.setMessage("Are you sure to exit?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void saveCities() throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        List<String[]> myList = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("places", places.get(i));
            jsonObject.put("latitude", locations.get(i).latitude);
            jsonObject.put("longitude", locations.get(i).longitude);
            jsonArray.put(jsonObject);
        }

        FileHelper.writeStringAsFile(context, jsonArray.toString());
    }

    private void clearListView()
    {
        FileHelper.writeStringAsFile(context, "");

        fillListView();
        onResume();
    }
}
