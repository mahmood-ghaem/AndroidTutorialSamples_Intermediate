package nl.mahmood.guesscities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1 - set the views in xml
 * 2 - create menu_option.xml
 * 3 - create empty method for buttons
 * 4 - override onCreateOptionsMenu & onOptionsItemSelected
 * 5 - initialize variables and views
 * 6 - create HtmlDownloader class
 * 7 - create ImageDownloader class
 * 8 - add uses-permission tag for internet in AndroidManifest.xml
 * 9 - create option_menu_dialog.xml
 * 10 - write showAboutDialog method
 * 11 - write showExitDialog method
 * 12 - create ReadFromFile class
 * 13 - change to use read from assets
 * 14 - add cities.txt to assets folder
 */
public class MainActivity extends AppCompatActivity
{

    ArrayList<String> cityURLs = new ArrayList<String>();
    ArrayList<String> cityNames = new ArrayList<String>();
    int chosenCity = 0;
    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;
    ImageView imageViewCity;
    Button buttonCityName_0;
    Button buttonCityName_1;
    Button buttonCityName_2;
    Button buttonCityName_3;
    TextView textViewScore;
    Context context = this;
    int totalCorrectAnswer;
    int totalQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewCity = findViewById(R.id.imageViewCity);
        buttonCityName_0 = findViewById(R.id.buttonCityName_0);
        buttonCityName_1 = findViewById(R.id.buttonCityName_1);
        buttonCityName_2 = findViewById(R.id.buttonCityName_2);
        buttonCityName_3 = findViewById(R.id.buttonCityName_3);
        textViewScore = findViewById(R.id.textViewScore);


        //HtmlDownloader htmlDownloader = new HtmlDownloader();
        ReadFromfile readFromfile = new ReadFromfile();
        String result = null;
        //source web page :
        //https://www.cntraveler.com/galleries/2016-01-08/the-50-most-beautiful-cities-in-the-world
        //I create a small web page and upload to my host
        try {

            //result = htmlDownloader.execute("http://ghaem.atwebpages.com/cities.html").get();
            result = readFromfile.ReadFromfile("cities.txt",context);
            String[] splitResult = result.split("<body>");

            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[1]);

            while (m.find()) {
                cityURLs.add(m.group(1));
            }

            p = Pattern.compile("<h1>(.*?)</h1>");
            m = p.matcher(splitResult[1]);

            while (m.find()) {
                cityNames.add(m.group(1));
            }
            Log.i("info:", cityURLs.toString());
            Log.i("info:", cityNames.toString());

            newQuestion();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cityNameClick(View view)
    {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            //Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
            totalCorrectAnswer++;
        } else {
            Toast.makeText(getApplicationContext(), "Worng! It was " + cityNames.get(chosenCity), Toast.LENGTH_SHORT).show();
        }
        totalQuestion++;
        textViewScore.setText(totalCorrectAnswer+" / "+totalQuestion);
        newQuestion();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {

            case R.id.about:
                showAboutDialog();
                break;
            case R.id.exit:
                showExitDialog();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    private void newQuestion()
    {
        try {
            Random rand = new Random();
            chosenCity = rand.nextInt(cityURLs.size());
            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImage = imageTask.execute(cityURLs.get(chosenCity)).get();
            imageViewCity.setImageBitmap(celebImage);
            locationOfCorrectAnswer = rand.nextInt(4);
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = cityNames.get(chosenCity);
                } else {
                    incorrectAnswerLocation = rand.nextInt(cityURLs.size());
                    while (incorrectAnswerLocation == chosenCity) {
                        incorrectAnswerLocation = rand.nextInt(cityURLs.size());
                    }
                    answers[i] = cityNames.get(incorrectAnswerLocation);
                }
            }

            buttonCityName_0.setText(answers[0]);
            buttonCityName_1.setText(answers[1]);
            buttonCityName_2.setText(answers[2]);
            buttonCityName_3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAboutDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.option_menu_dialog, null);
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


}