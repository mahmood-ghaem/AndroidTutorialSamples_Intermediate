package nl.mahmood.mapsclusteringdemo.utils;

import android.content.Context;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JSONHandler
{
    public static JSONArray readAssets(Context context, String fileName)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader
                    (new InputStreamReader(context.getAssets().open(fileName)));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder("");
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            return new JSONArray(stringBuilder.toString());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return new JSONArray();
    }
}
