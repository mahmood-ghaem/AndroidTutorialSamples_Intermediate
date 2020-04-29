package nl.mahmood.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    Context context = this;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ProgressBar progressBar;
    public static SQLiteDatabase newsDB;

    ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);

        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, titles);

        DownloadTask task = new DownloadTask();
        task.execute("http://newsapi.org/v2/top-headlines?country=nl&apiKey=f1add8eaf65945daa34f68c868c95908");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                position++;
                intent.putExtra("newsId", position);
                startActivity(intent);
            }
        });

        newsDB = this.openOrCreateDatabase("NewsDataBase", MODE_PRIVATE, null);
        try
        {
            newsDB.execSQL("DROP TABLE IF EXISTS tblNews");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        newsDB.execSQL("CREATE TABLE IF NOT EXISTS tblNews (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT, url TEXT, urlImage TEXT)");
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

                return null;
            }

        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            JSONArray jsonArray;
            JSONObject jsonObject;
            try
            {
                jsonObject = new JSONObject(s);
                String articles = jsonObject.getString("articles");
                jsonArray = new JSONArray(articles);
                String title;
                String content;
                String url;
                String urlImage;
                String sql;
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    title = jsonPart.getString("title");
                    content = jsonPart.getString("content");
                    url = jsonPart.getString("url");
                    urlImage = jsonPart.getString("urlToImage");

                    sql = "INSERT INTO tblNews (title, content, url, urlImage) VALUES (?, ?, ?, ?)";
                    SQLiteStatement statement = newsDB.compileStatement(sql);
                    statement.bindString(1, title);
                    statement.bindString(2, content);
                    statement.bindString(3, url);
                    statement.bindString(4, urlImage);

                    statement.execute();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }


            progressBar.setVisibility(View.GONE);
            updateListView();
            listView.setAdapter(arrayAdapter);
        }

    }

    private void updateListView()
    {
        Cursor c = newsDB.rawQuery("SELECT * FROM tblNews", null);

        int titleIndex = c.getColumnIndex("title");
        int idNumber = 1;
        if (c.moveToFirst())
        {
            titles.clear();
            do
            {
                titles.add(idNumber + "- " + c.getString(titleIndex));
                idNumber++;
            } while (c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
        }
    }
    //=================================
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
            case R.id.about:
                showAboutDialog();
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
        alertDialogBuilder.setIcon(R.drawable.outline_info_black_18dp);
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
        alertDialogBuilder.setIcon(R.drawable.exit_icon);
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
}
