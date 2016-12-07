package com.customcode420.caffeinecutter;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.github.clans.fab.FloatingActionButton;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    Integer currentLevel = 0;
    Integer oldLevel = 0;
    private Realm realm;
    private final ArrayList<String> favIdList = new ArrayList<>();
    DrinksDatabaseHelper dbHelper = new DrinksDatabaseHelper(this);

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

        //Initialising drinks database
        try{
            dbHelper.createDataBase();

        } catch (java.io.IOException ioe){
            throw new Error("Cant create database");
        }

        try{
            dbHelper.openDataBase();
        } catch (SQLException sqle){
            throw sqle;
        }

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
                final String drinkId = "instantCoffee";
                final Integer cafContent = getCafContent(drinkId);

                oldLevel = currentLevel;
                currentLevel += cafContent;
                String tempStr = currentLevel + " mg";
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
                        history.setCafContent(cafContent);
                        history.setName(getDrinkName(drinkId));
                        history.setTime(date);
                    }
                });

                historyAdapter.notifyDataSetChanged();
            }
        });

        instantCoffee500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "instantCoffee";
                final Integer cafContent = getCafContent(drinkId);

                oldLevel = currentLevel;
                currentLevel += cafContent;
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
                        history.setCafContent(cafContent);
                        history.setName(getDrinkName(drinkId));
                        history.setTime(date);
                    }
                });
            }
        });

        brewedCoffee250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "brewedCoffee";
                final Integer cafContent = getCafContent(drinkId);
                oldLevel = currentLevel;
                currentLevel += cafContent;
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
                        history.setCafContent(cafContent);
                        history.setName(getDrinkName(drinkId));
                        history.setTime(date);
                    }
                });
            }
        });
        //Use ProgressBarAnimation function to animate the progress bad
        brewedCoffee500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "brewedCoffee";
                final Integer cafContent = getCafContent(drinkId);
                oldLevel = currentLevel;
                currentLevel += cafContent;

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
                        history.setCafContent(cafContent);
                        history.setName(getDrinkName(drinkId));
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

                }
            }
        });

    }

    public ArrayList<String> getFavIdList() {
        return favIdList;
    }

    public void addFavId(String id){
        favIdList.add(id);
        //Add query then implement into toast

        Cursor cursor = dbHelper.queryDb("SELECT * FROM drinks WHERE drinkId = '" + id +"';");

        //Moves the cursor to the first location.
        cursor.moveToFirst();
        Toast.makeText(this, "You've added " +
                        cursor.getString(cursor.getColumnIndex("drinkName")) + " to your favourites",
                Toast.LENGTH_SHORT).show();
    }

    public String getDrinkName(String drinkId){
        //Creating cursor to pull data from DB.
        Cursor cursor = dbHelper.queryDb("SELECT * FROM drinks WHERE drinkId = '" + drinkId +"';");
        //Moving cursor to first position in results.
        cursor.moveToFirst();
        //Returning drink name and appending the volume of the drink to the end of the drink name
        return cursor.getString(cursor.getColumnIndex("drinkName")) + " " +
                cursor.getString(cursor.getColumnIndex("drinkVol"));
    }

    public Integer getCafContent(String drinkId){
        Cursor cursor = dbHelper.queryDb("SELECT * FROM drinks WHERE drinkId = '" + drinkId +"';");
        //Moving cursor to first position in results.
        cursor.moveToFirst();
        //Returning caffeine content in the drink from cafContent column.
        return cursor.getInt(cursor.getColumnIndex("cafContent"));
    }

    private static double round (double value) {
        int precision = 2;
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}
