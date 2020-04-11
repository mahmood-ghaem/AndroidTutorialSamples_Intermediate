package nl.mahmood.countdowntimerdemo;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

/**
 * pre requirements
 * A - make layout file correctly
 * B - add string value in string.xml file
 * C - add tollingbell.mp3 in raw folder in res
 * 1 - define widget
 * 2 - find widget
 * 3 - write methods for buttons
 * 3-1 - method on click for start
 * 3-2 - method on click for stop
 * 4 - get hour and minuet from timePicker
 * 5 - get current hour and minuet
 * 6 - check selected time grater than current time
 * 7 - we use CountDownTimer class
 * 7-1 - define countDownTimer
 * 7-2 - create startCountDownTimer method
 * 7-3 - stopCountDownTimer
 * 7-4 - define to variables INTERVAL & didStartCountDown
 * 7-5 - call startCountDownTimer method to start countDownTimer
 * 7-6 - create onTick method
 * 7-7 - create onFinish method define a media player object to play tollingbell.mp3
 * 7-8 - create formatNumber method to convert a time like 10:3 --> 10:03
 * 8 - change stage
 * 9 - if selected time smaller or equal than current time
 * 10 - check for countDownTimer is working or not
 * 11 - write a body for alarmOff
 */
public class MainActivity extends AppCompatActivity
{
    /**
     * 1
     */
    TimePicker timePicker;
    TextView textViewHint;
    TextView textViewCounter;
    /**
     * 7-1
     */
    CountDownTimer countDownTimer;
    /**
     * 7-4
     */
    private final long INTERVAL = 1;//interval when countdown should update value
    private boolean didStartCountDown = false;//indicates if the countdown started or not

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 2
         */
        timePicker = findViewById(R.id.timePicker);
        textViewHint = findViewById(R.id.textViewHint);
        textViewCounter = findViewById(R.id.textViewCounter);
    }

    /**
     * 3-1
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void alarmOn(View view)
    {
        /**
         * 10
         */
        if (!didStartCountDown) {
            /**
             * 4
             */
            int timePickerHour = timePicker.getHour();
            int timePickerMinute = timePicker.getMinute();
            int sumMinPick = (timePickerHour * 60) + timePickerMinute;//convert hour to minute
            /**
             * 5
             */
            Calendar rightNow = Calendar.getInstance();
            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            int currentMinute = rightNow.get(Calendar.MINUTE);
            int sumMinCurrent = (currentHour * 60) + currentMinute;//convert hour to minute
            /**
             * 6
             */
            if (sumMinPick > sumMinCurrent) {

                /**
                 * 7-5
                 */
                startCountDownTimer((sumMinPick - sumMinCurrent) * 60 * 1000, INTERVAL);
                /**
                 * 8
                 */
                textViewHint.setText(getString(R.string.alarm_is_on) + "\n" + timePickerHour + ":" + timePickerMinute);
                timePicker.setVisibility(View.INVISIBLE);
                textViewCounter.setVisibility(View.VISIBLE);
                /**
                 * 9
                 */
            } else {
                textViewHint.setText("Please select a time ahead");
            }
        }
    }

    /**
     * 3-2
     */
    public void alarmOff(View view)
    {
        /**
         * 11
         */
        textViewHint.setText(getString(R.string.alarm_is_off));
        stopCountDownTimer();

    }

    /**
     * 7-2
     */
    private void startCountDownTimer(long duration, long interval)
    {
        countDownTimer = new CountDownTimer(duration, interval)
        {
            /**
             * 7-6
             */
            @Override
            public void onTick(long millisUntilFinished)
            {
                long seconds = millisUntilFinished / 1000;//convert to seconds
                long minutes = seconds / 60;//convert to minutes
                long hours = minutes / 60;//convert to hours

                if (minutes > 0)//if we have minutes, then there might be some remainder seconds
                    seconds = seconds % 60;//seconds can be between 0-60, so we use the % operator to get the remainder
                if (hours > 0)
                    minutes = minutes % 60;//similar to seconds
                String time = formatNumber(hours) + ":" + formatNumber(minutes) + ":" +
                        formatNumber(seconds);
                textViewCounter.setText(time);//set value to text
            }

            /**
             * 7-7
             */
            @Override
            public void onFinish()
            {

                MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.tollingbell);
                mplayer.start();
                stopCountDownTimer();
            }
        };
        countDownTimer.start();
        didStartCountDown = true;
    }

    /**
     * 7-3
     */
    private void stopCountDownTimer()
    {
        countDownTimer.cancel();
        didStartCountDown = false;

        textViewCounter.setVisibility(View.INVISIBLE);
        timePicker.setVisibility(View.VISIBLE);
        textViewHint.setText(getString(R.string.alarm_is_off));
    }

    /**
     * 7-8
     */
    private String formatNumber(long value)
    {
        if (value < 10)
            return "0" + value;
        return value + "";
    }
}
