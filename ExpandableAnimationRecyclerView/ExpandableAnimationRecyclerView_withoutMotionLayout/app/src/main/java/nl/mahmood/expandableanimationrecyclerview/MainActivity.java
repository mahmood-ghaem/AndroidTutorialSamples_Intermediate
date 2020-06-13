package nl.mahmood.expandableanimationrecyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    List<City> cityList;
    CityAdapter cityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        cityList = new ArrayList<>();
        cityAdapter = new CityAdapter(this, cityList, recyclerView);
        getDataFirstTime();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cityAdapter);

        cityAdapter.setOnLoadMoreListener(new OnLoadMoreListener()
        {
            @Override
            public void onLoadMore()
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getDataNextTime();

                    }
                }, 2000);
            }
        });
    }


    private void getDataFirstTime()
    {
        addItemsFromJSON();

        cityList.add(null);
        cityAdapter.notifyDataSetChanged();
    }

    private void getDataNextTime()
    {
        cityList.remove(cityList.size() - 1);
        cityAdapter.notifyItemRemoved(cityList.size() - 1);

        addItemsFromJSON();

        cityList.add(null);
        cityAdapter.notifyDataSetChanged();
        cityAdapter.setLoading(false);
    }

    private void addItemsFromJSON()
    {
        try
        {

            String jsonDataString = readJSONDataFromFile();
            JSONArray jsonArray = new JSONArray(jsonDataString);

            for (int i = 0; i < jsonArray.length(); ++i)
            {

                JSONObject itemObj = jsonArray.getJSONObject(i);

                String name = itemObj.getString("name");
                String avatar = itemObj.getString("avatar");
                String image = itemObj.getString("image");
                String description = itemObj.getString("description");
                String website = itemObj.getString("website");

                City city = new City(name, avatar, image, description, website);
                cityList.add(city);
            }

        } catch (JSONException | IOException e)
        {
            Log.d(TAG, "addItemsFromJSON: ", e);
        }
    }

    private String readJSONDataFromFile() throws IOException
    {

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try
        {

            String jsonString = null;
            inputStream = getResources().openRawResource(R.raw.cities);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));

            while ((jsonString = bufferedReader.readLine()) != null)
            {
                builder.append(jsonString);
            }

        } finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
        return new String(builder);
    }
}
