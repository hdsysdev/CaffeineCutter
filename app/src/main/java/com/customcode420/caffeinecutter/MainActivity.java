package com.customcode420.caffeinecutter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    ProgressBarAnimation animation = null;
    ProgressBar caffeineMeter = null;
    TextView levelNum = null;
    HistoryAdapter historyAdapter = null;

    SharedPreferences sharedPrefs = null;
    int dailyCaffeine = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialising SharedPreferences to check if it's the app's first run
        sharedPrefs = getSharedPreferences("com.customcode420.caffeinecutter", MODE_PRIVATE);

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

        //Defining list item array, drawer ListView and
        String[] drawerListArray = getResources().getStringArray(R.array.drawerMenuArray);
        ListView drawerList = (ListView) findViewById(R.id.drawerList);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Log.v("USER", drawerListArray.toString());
        //Creating adapter to populate ListView
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.drawer_item, drawerListArray);
        drawerList.setAdapter(adapter);


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
        //Defining Other FAB
        final FloatingActionButton otherButton =
                (FloatingActionButton) findViewById(R.id.otherFab);

        //Defining ListView
        final ListView historyList = (ListView) findViewById(R.id.listView);
        historyAdapter = new HistoryAdapter(this);
        historyList.setAdapter(historyAdapter);


        caffeineMeter = (ProgressBar) findViewById(R.id.progressBar);
        levelNum = (TextView) findViewById(R.id.levelNumber);

        animation = new ProgressBarAnimation(caffeineMeter);
        animation.setDuration(1000);
        //Setting max value of progress bar to daily caffeine intake
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(sharedPrefs.getInt("dailyCaffeine", 300));


        //Creating onClickListeners with appropriate float values for caffeine amount.
        instantCoffee250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drink drink = new Drink(oldLevel, Integer.parseInt(levelNum.getText().toString()), levelNum,
                        animation, dbHelper, caffeineMeter, historyAdapter, realm);
                drink.addDrink("instantCoffee");
                oldLevel = Integer.parseInt(levelNum.getText().toString());
            }
        });

        instantCoffee500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "instantCoffee";
                Drink drink = new Drink(oldLevel, Integer.parseInt(levelNum.getText().toString()), levelNum,
                        animation, dbHelper, caffeineMeter, historyAdapter, realm);
                drink.addDrink(drinkId);
                oldLevel = Integer.parseInt(levelNum.getText().toString());
            }
        });

        brewedCoffee250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "brewedCoffee";
                Drink drink = new Drink(oldLevel, Integer.parseInt(levelNum.getText().toString()),levelNum,
                        animation, dbHelper, caffeineMeter, historyAdapter, realm);
                drink.addDrink(drinkId);
                oldLevel = Integer.parseInt(levelNum.getText().toString());
            }
        });
        //Use ProgressBarAnimation function to animate the progress bad
        brewedCoffee500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "brewedCoffee";
                Drink drink = new Drink(oldLevel, Integer.parseInt(levelNum.getText().toString()),levelNum,
                        animation, dbHelper, caffeineMeter, historyAdapter, realm);
                drink.addDrink(drinkId);
                oldLevel = Integer.parseInt(levelNum.getText().toString());

            }
        });
        //Defining Other button
        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting activity using a created intent.
                Intent intent = new Intent(getBaseContext(), DrinkSelection.class);
                startActivity(intent);
            }
        });

        //Setting onClickListener for undo button
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RealmResults<History> results = realm.where(History.class).findAll();
                //TODO: Fix this
                if (!results.isEmpty()){
                    currentLevel = Integer.parseInt(levelNum.getText().toString());
                    oldLevel = currentLevel;
                    //Removing last entry in history realm from current level.
                    currentLevel -= results.last().getCafContent();
                    if (currentLevel <= 0)
                        currentLevel = 0;
                    levelNum.setText(currentLevel.toString());
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
        //Query to find drink

        Cursor cursor = dbHelper.queryDb("SELECT * FROM drinks WHERE drinkId = '" + id +"';");

        //Moves the cursor to the first location.
        cursor.moveToFirst();
        Toast.makeText(this, "You've added " +
                        cursor.getString(cursor.getColumnIndex("drinkName")) + " to your favourites",
                Toast.LENGTH_SHORT).show();
    }


    private static double round (double value) {
        int precision = 2;
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }



    @Override
    protected void onResume() {
        super.onResume();
        //Creating Alert Dialog Builder for pop up message.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input your daily caffeine intake");
        final EditText input = new EditText(this);
        builder.setView(input);

        //Setting builder positive and negative buttons to apply and cancel.
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dailyCaffeine = Integer.parseInt(input.getText().toString());
                sharedPrefs.edit().putInt("dailyCaffeine", dailyCaffeine).apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //Checking if it's the first run. If it is then firstrun is set to false and is committed.
        if (sharedPrefs.getBoolean("firstrun", true)) {
            //Showing Dialog
            builder.show();
            sharedPrefs.edit().putBoolean("firstrun", false).apply();
        }
    }
}
