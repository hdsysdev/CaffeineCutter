package com.customcode420.caffeinecutter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.github.clans.fab.FloatingActionButton;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    float currentLevel = 0;
    float oldLevel = 0;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialising RealmDb and Stetho for use in Chrome dev tools
        Realm.init(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        realm = Realm.getDefaultInstance();



        //Defining FABs for different kinds of coffee.
        final FloatingActionButton instantCoffee250 =
                (FloatingActionButton) findViewById(R.id.instantCoffee250);
        final FloatingActionButton instantCoffee500 =
                (FloatingActionButton) findViewById(R.id.instantCoffee500);
        final FloatingActionButton brewedCoffee250 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee250);
        final FloatingActionButton brewedCoffee500 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee500);
        //Defining Undo FAB
        final FloatingActionButton undoButton =
                (FloatingActionButton) findViewById(R.id.undoFAB);

        //Defining ListView
        final ListView historyList = (ListView) findViewById(R.id.listView);
        final HistoryAdapter historyAdapter = new HistoryAdapter(this);
        historyList.setAdapter(historyAdapter);


        final ProgressBar caffeineMeter = (ProgressBar) findViewById(R.id.progressBar);
        final TextView levelNum = (TextView) findViewById(R.id.levelNumber);

        final ProgressBarAnimation animation =
                new ProgressBarAnimation(caffeineMeter);
        animation.setDuration(1000);


        //Creating onClickListeners with appropriate float values for caffeine amount.
        instantCoffee250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 60.2F;
                String tempStr = round(currentLevel) + " mg";
                levelNum.setText(tempStr);
                //Using setStartEnd function to change values in animation class.
                animation.setStartEnd(oldLevel, currentLevel);
                caffeineMeter.startAnimation(animation);


                //Inserting drink to history realm
                final String date = java.text.DateFormat.getDateTimeInstance()
                        .format(Calendar.getInstance().getTime());
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        History history = realm.createObject(History.class);
                        history.setCafContent(60.2F);
                        history.setName("instantCoffee250");
                        history.setTime(date);
                    }
                });

                historyAdapter.notifyDataSetChanged();
            }
        });

        instantCoffee500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 120.4F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                animation.setStartEnd(oldLevel, currentLevel);
                caffeineMeter.startAnimation(animation);


                //Inserting drink to history realm
                final String date = java.text.DateFormat.getDateTimeInstance()
                        .format(Calendar.getInstance().getTime());
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        History history = realm.createObject(History.class);
                        history.setCafContent(120.4F);
                        history.setName("instantCoffee500");
                        history.setTime(date);
                    }
                });
            }
        });

        brewedCoffee250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 172.2F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                animation.setStartEnd(oldLevel, currentLevel);
                caffeineMeter.startAnimation(animation);

                //Inserting drink to history realm
                final String date = java.text.DateFormat.getDateTimeInstance()
                        .format(Calendar.getInstance().getTime());
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        History history = realm.createObject(History.class);
                        history.setCafContent(172.2F);
                        history.setName("brewedCoffee250");
                        history.setTime(date);
                    }
                });
            }
        });
        //Use ProgressBarAnimation function to animate the progress bad
        brewedCoffee500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldLevel = currentLevel;
                currentLevel += 344.4F;
                String tempStr = currentLevel + " mg";
                levelNum.setText(tempStr);
                animation.setStartEnd(oldLevel, currentLevel);
                caffeineMeter.startAnimation(animation);


                if (currentLevel >= 10800){
                    levelNum.setText("How the fuck are you not dead yet.");
                }

                //Inserting drink to history realm
                final String date = java.text.DateFormat.getDateTimeInstance()
                        .format(Calendar.getInstance().getTime());
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        History history = realm.createObject(History.class);
                        history.setCafContent(344.4F);
                        history.setName("brewedCoffee500");
                        history.setTime(date);
                    }
                });
            }
        });

        //Setting onClickListener for undo button
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RealmResults<History> results = realm.where(History.class).findAll();


                if (!results.isEmpty()){
                    oldLevel = currentLevel;
                    //Removing last entry in history realm from current level.
                    currentLevel -= results.last().getCafContent();;
                    if (currentLevel <= 0)
                        currentLevel = 0;

               /* if (history.size() != 0){
                    oldLevel = currentLevel;
                    //Removing last entry in history from current level.
                    currentLevel -= history.get(history.size() - 1);
                    if (currentLevel <= 0)
                        currentLevel = 0;*/

                    String tempStr = currentLevel + " mg";
                    levelNum.setText(tempStr);
                    animation.setStartEnd(oldLevel, currentLevel);
                    caffeineMeter.startAnimation(animation);

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            results.deleteLastFromRealm();
                        }
                    });


                    //Removing last item from history
                    //history.remove(history.size() - 1);

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
