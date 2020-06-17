package nl.mahmood.customsnackbarmessage;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
{
    CustomSnackbar customSnackbar;
    ViewGroup layoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutRoot = findViewById(R.id.layoutRoot);
        customSnackbar = CustomSnackbar.make(layoutRoot, CustomSnackbar.LENGTH_INDEFINITE);

        customSnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<CustomSnackbar>()
        {
            @Override
            public void onDismissed(CustomSnackbar transientBottomBar, int event)
            {
                super.onDismissed(transientBottomBar, event);
                switch (event)
                {
                    // Some available modes

//                    case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
//                    case Snackbar.Callback.DISMISS_EVENT_MANUAL:
//                    case Snackbar.Callback.DISMISS_EVENT_SWIPE:
//                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                        Toast.makeText(MainActivity.this, "Time Out", Toast.LENGTH_SHORT).show();
                        break;
                }
                }

            @Override
            public void onShown(CustomSnackbar transientBottomBar)
            {
                super.onShown(transientBottomBar);
                Toast toast = Toast.makeText(MainActivity.this, "Custom Snackbar is showing.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 150);
                toast.show();
            }
        });

    }

    public void imageButtonInfoClick(View view)
    {
        customSnackbar.setDuration(3000);
        customSnackbar.setColor(CustomSnackbar.Color.BLUE);
        customSnackbar.setText("This is an info text.");
        customSnackbar.setIcon(android.R.drawable.ic_dialog_info);
        customSnackbar.show();
    }

    public void imageButtonEmailClick(View view)
    {
        customSnackbar.setDuration(5000);
        customSnackbar.setColor(CustomSnackbar.Color.GREEN);
        customSnackbar.setText("Do you want to send an email?");
        customSnackbar.setIcon(android.R.drawable.ic_dialog_email);
        customSnackbar.setAction("Send Mail", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast toast = Toast.makeText(MainActivity.this, "Action Button Send Mail Clicked", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 150);
                toast.show();
            }
        });
        customSnackbar.show();
    }

    public void imageButtonAlertClick(View view)
    {
        customSnackbar.setDuration(5000);
        customSnackbar.setColor(CustomSnackbar.Color.RED);
        customSnackbar.setText("This is an alert text!");
        customSnackbar.setIcon(android.R.drawable.ic_dialog_alert);
        customSnackbar.setAction("Retry", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast toast = Toast.makeText(MainActivity.this, "Action Button Retry Clicked", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 150);
                toast.show();
            }
        });
        customSnackbar.show();
    }
}