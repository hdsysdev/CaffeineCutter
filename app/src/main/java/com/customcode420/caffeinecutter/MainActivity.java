package com.customcode420.caffeinecutter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    float currentLevel = 0;
    float oldLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Defining FABs for different kinds of coffee.
        final FloatingActionButton instantCoffe250 =
                (FloatingActionButton) findViewById(R.id.instantCoffee250);
        final FloatingActionButton instantCoffe500 =
                (FloatingActionButton) findViewById(R.id.instantCoffee500);
        final FloatingActionButton brewedCoffe250 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee250);
        final FloatingActionButton brewedCoffe500 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee500);


        final ProgressBar caffeineMeter = (ProgressBar) findViewById(R.id.progressBar);
        final TextView levelNum = (TextView) findViewById(R.id.levelNumber);

        //Creating onClickListeners with appropriate float values for caffeine amount.
        instantCoffe250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 60.2F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                caffeineMeter.setProgress(Math.round(currentLevel));
            }
        });

        instantCoffe500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 120.4F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                caffeineMeter.setProgress(Math.round(currentLevel));
            }
        });

        brewedCoffe250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 172.2F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                caffeineMeter.setProgress(Math.round(currentLevel));
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
                caffeineMeter.setProgress(Math.round(currentLevel));
                if (currentLevel >= 10800){
                    levelNum.setText("How the fuck are you not dead yet.");
                }
            }
        });
    }
}
