package com.customcode420.caffeinecutter;

import android.icu.text.DecimalFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.customcode420.caffeinecutter.ProgressBarAnimation;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    float currentLevel = 0;
    float oldLevel = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<Float> history = new ArrayList<>();

        //Defining FABs for different kinds of coffee.
        final FloatingActionButton instantCoffe250 =
                (FloatingActionButton) findViewById(R.id.instantCoffee250);
        final FloatingActionButton instantCoffe500 =
                (FloatingActionButton) findViewById(R.id.instantCoffee500);
        final FloatingActionButton brewedCoffe250 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee250);
        final FloatingActionButton brewedCoffe500 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee500);
        //Defining Undo FAB
        final FloatingActionButton undoButton =
                (FloatingActionButton) findViewById(R.id.undoFAB);


        final ProgressBar caffeineMeter = (ProgressBar) findViewById(R.id.progressBar);
        final TextView levelNum = (TextView) findViewById(R.id.levelNumber);

        final ProgressBarAnimation animation =
                new ProgressBarAnimation(caffeineMeter);
        animation.setDuration(1000);


        //Creating onClickListeners with appropriate float values for caffeine amount.
        instantCoffe250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 60.2F;
                String tempStr = round(currentLevel) + " mg";
                levelNum.setText(tempStr);
                //Using setStartEnd function to change values in animation class.
                animation.setStartEnd(oldLevel, currentLevel);
                caffeineMeter.startAnimation(animation);

                //Adding drink to history
                history.add(60.2F);
            }
        });

        instantCoffe500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 120.4F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                animation.setStartEnd(oldLevel, currentLevel);
                caffeineMeter.startAnimation(animation);
                history.add(120.4F);
            }
        });

        brewedCoffe250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 172.2F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                animation.setStartEnd(oldLevel, currentLevel);
                caffeineMeter.startAnimation(animation);
                history.add(172.2F);
            }
        });
        //Use ProgressBarAnimation function to animate the progress bad
        brewedCoffe500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 344.4F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                animation.setStartEnd(oldLevel, currentLevel);
                caffeineMeter.startAnimation(animation);

                history.add(344.4F);

                if (currentLevel >= 10800){
                    levelNum.setText("How the fuck are you not dead yet.");
                }
            }
        });

        //Setting onClickListener for undo button
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (history.size() != 0){
                    oldLevel = currentLevel;
                    //Removing last entry in history from current level.
                    currentLevel -= history.get(history.size() - 1);
                    if (currentLevel <= 0)
                        currentLevel = 0;

                    String tempStr = currentLevel + " mg";
                    levelNum.setText(tempStr);
                    animation.setStartEnd(oldLevel, currentLevel);
                    caffeineMeter.startAnimation(animation);

                    //Removing last item from history
                    history.remove(history.size() - 1);
                }
            }
        });

    }

    private static double round (double value) {
        int precision = 2;
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}
