package nl.mahmood.braintrainer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * 1 - set views in xml file
 * 2 - initial variables and views
 * 3 - find views in activity
 * 4 - write on click empty methods for buttons
 * 5 - write makeQuestion method
 * 6 - write stopPlay method
 */
public class MainActivity extends AppCompatActivity
{
    /**
     * 2
     */
    ConstraintLayout constraintLayout;
    Button buttonStart;
    Button buttonAnswer_0;
    Button buttonAnswer_1;
    Button buttonAnswer_2;
    Button buttonAnswer_3;
    TextView textViewResult;
    TextView textViewScore;
    TextView textViewSum;
    TextView textViewTimer;
    TextView textViewEnd;
    TextView textViewSummary;

    // all questions in a round play
    int questionNum;
    int correctAnswerNum;
    // a random number between 0 to 4 to randomize correct button
    int correctAnswerPosition;
    ArrayList<Integer> answers = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 3
         */
        constraintLayout = findViewById(R.id.consttraintLayout);
        buttonStart = findViewById(R.id.buttonStart);
        buttonAnswer_0 = findViewById(R.id.buttonAnswer_0);
        buttonAnswer_1 = findViewById(R.id.buttonAnswer_1);
        buttonAnswer_2 = findViewById(R.id.buttonAnswer_2);
        buttonAnswer_3 = findViewById(R.id.buttonAnswer_3);
        textViewResult = findViewById(R.id.textViewResult);
        textViewScore = findViewById(R.id.textViewScore);
        textViewSum = findViewById(R.id.textViewSum);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewEnd = findViewById(R.id.textViewEnd);
        textViewSummary = findViewById(R.id.textViewSummary);
    }

    /**
     * 4
     */
    public void selectAnswer(View view)
    {
        questionNum++;
        if (Integer.toString(correctAnswerPosition).equals(view.getTag().toString())) {
            correctAnswerNum++;
            textViewResult.setText("Correct Answer :)");
        } else {
            textViewResult.setText("Wrong Answer :(");
        }
        Log.i("info :::", correctAnswerPosition + " : " + view.getTag().toString());
        textViewScore.setText(correctAnswerNum + " / " + questionNum);
        makeQuestion();
    }

    /**
     * 4
     */
    public void startClick(View view)
    {
        constraintLayout.setVisibility(View.VISIBLE);
        buttonStart.setVisibility(View.INVISIBLE);
        textViewEnd.setVisibility(View.INVISIBLE);
        textViewSummary.setVisibility(View.INVISIBLE);
        correctAnswerNum = 0;
        questionNum = 0;
        textViewResult.setText("");

        textViewTimer.setText("40");
        textViewScore.setText(correctAnswerNum + " / " + questionNum);
        makeQuestion();

        new CountDownTimer(40000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                textViewTimer.setText(String.valueOf(millisUntilFinished / 1000) + " s");
            }

            @Override
            public void onFinish()
            {
                stopPlay();
            }
        }.start();
    }

    /**
     * 5
     */
    private void makeQuestion()
    {
        Random random = new Random();
        int num_1 = random.nextInt(21);
        int num_2 = random.nextInt(21);

        textViewSum.setText(num_1 + " + " + num_2 + " = ?");

        correctAnswerPosition = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            if (i == correctAnswerPosition) {
                answers.add(i, num_1 + num_2);
            } else {
                int wrongAnswer = random.nextInt(41);

                while (wrongAnswer == num_1 + num_2) {
                    wrongAnswer = random.nextInt(41);
                }
                answers.add(i, wrongAnswer);
            }
            switch (i) {
                case 0:
                    buttonAnswer_0.setText(Integer.toString(answers.get(0)));
                    buttonAnswer_0.setBackgroundColor(Color.argb(255, random.nextInt(256-170)+170, random.nextInt(256), random.nextInt(256)));
                    break;
                case 1:
                    buttonAnswer_1.setText(Integer.toString(answers.get(1)));
                    buttonAnswer_1.setBackgroundColor(Color.argb(255, random.nextInt(256-170)+170, random.nextInt(256), random.nextInt(256)));
                    break;
                case 2:
                    buttonAnswer_2.setText(Integer.toString(answers.get(2)));
                    buttonAnswer_2.setBackgroundColor(Color.argb(255, random.nextInt(256-170)+170, random.nextInt(256), random.nextInt(256)));
                    break;
                case 3:
                    buttonAnswer_3.setText(Integer.toString(answers.get(3)));
                    buttonAnswer_3.setBackgroundColor(Color.argb(255, random.nextInt(256-170)+170, random.nextInt(256), random.nextInt(256)));
                    break;

            }
        }


    }

    /**
     * 6
     */
    private void stopPlay()
    {
        constraintLayout.setVisibility(View.INVISIBLE);
        buttonStart.setVisibility(View.VISIBLE);

        if ((correctAnswerNum + 3) >= questionNum && questionNum > 10) {
            textViewEnd.setText("You Win!");

        } else {
            textViewEnd.setText("You Loss!");
        }
        textViewEnd.setVisibility(View.VISIBLE);
        textViewSummary.setVisibility(View.VISIBLE);
        textViewSummary.setText("Summary : \n"+"Wrong Answer : "+(questionNum-correctAnswerNum)+"\n Correct Answer : "+correctAnswerNum);
    }

}
