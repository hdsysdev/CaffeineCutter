package com.customcode420.caffeinecutter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        //Creating Alert Dialog Builder for pop up message.
        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Input your daily caffeine intake")
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", null)
                .create();
        final EditText input = new EditText(this);
        builder.setView(input);

        //Setting builder positive and negative buttons to apply and cancel.
        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dailyCaffeine = Integer.parseInt(input.getText().toString());
                        sharedPrefs.edit().putInt("dailyCaffeine", dailyCaffeine).apply();
                        builder.dismiss();
                    }
                });
                Button buttonNegative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(), "Please Enter A Value", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Defining list item array, drawer ListView and
        String[] drawerListArray = getResources().getStringArray(R.array.drawerMenuArray);
        ListView drawerList = (ListView) findViewById(R.id.drawerList);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Creating adapter to populate ListView
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerListArray));

        //Making intent to start info activity
        final Intent infoIntent = new Intent(this, InfoActivity.class);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    Intent intent = new Intent(getBaseContext(), DrinkSelection.class);
                    startActivityForResult(intent, 1);
                } else if(position == 2){
                    startActivity(infoIntent);
                } else if(position == 3){
                    builder.show();
                }
            }
        });

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
        animation.setDuration(500);
        //Setting max value of progress bar to daily caffeine intake
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(sharedPrefs.getInt("dailyCaffeine", 300));


        //Checking if it's the first run. If it is then firstrun is set to false and is committed.
        if (sharedPrefs.getBoolean("firstrun", true)) {
            //Showing Dialog
            builder.show();
            sharedPrefs.edit().putBoolean("firstrun", false).apply();
        }


        //Creating onClickListeners with appropriate float values for caffeine amount.
        instantCoffee250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDrink("instantCoffee");
            }
        });


        instantCoffee500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "instantCoffee";
                addDrink(drinkId);
            }
        });

        brewedCoffee250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "brewedCoffee";
                addDrink(drinkId);
            }
        });
        //Use ProgressBarAnimation function to animate the progress bad
        brewedCoffee500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String drinkId = "brewedCoffee";
                addDrink(drinkId);

            }
        });
        //Defining Other button
        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting activity using a created intent.
                Intent intent = new Intent(getBaseContext(), DrinkSelection.class);
                startActivityForResult(intent, 1);
            }
        });

        //Setting onClickListener for undo button
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RealmResults<History> results = realm.where(History.class).findAll();

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

                    //Adding current level to SharedPrefs
                    sharedPrefs.edit().putInt("cafLevel", currentLevel).apply();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            results.deleteLastFromRealm();
                        }
                    });
                }
            }
        });
        //Restoring caffeine meter progress and current level
        caffeineMeter.setProgress(sharedPrefs.getInt("cafLevel", 0));
        currentLevel = sharedPrefs.getInt("cafLevel", 0);
        levelNum.setText(Integer.toString(sharedPrefs.getInt("cafLevel", 0)));
    }

    //Adding drink after drink selection activity is closed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                addDrink(data.getStringExtra("drinkId"));
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }

        }
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

        //Setting progress bar and level text to caffeine level from shared prefs
        caffeineMeter.setProgress(sharedPrefs.getInt("cafLevel", 0));
        levelNum.setText(Integer.toString(sharedPrefs.getInt("cafLevel", 0)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Setting progress bar to caffeine level from shared prefs
        caffeineMeter.setProgress(sharedPrefs.getInt("cafLevel", 0));
    }

    public void addDrink(final String drinkId){
        final Integer cafContent = getCafContent(drinkId);

        currentLevel =  caffeineMeter.getProgress();
        oldLevel = currentLevel;

        currentLevel += cafContent;

        //Adding current level to SharedPrefs
        sharedPrefs.edit().putInt("cafLevel", currentLevel).apply();
        levelNum.setText(Integer.toString(currentLevel));

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

        //Using setStartEnd function to change values in animation class.
        animation.setStartEnd(oldLevel, currentLevel);
        caffeineMeter.startAnimation(animation);
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
        Cursor cursor = dbHelper.queryDb("SELECT * FROM drinks WHERE drinkId = '" + drinkId +"';");
        //Moving cursor to first position in results.
        cursor.moveToFirst();
        //Returning caffeine content in the drink from cafContent column.
        return cursor.getInt(cursor.getColumnIndex("cafContent"));
    }


    public Integer getCurrentLevel() {
        return currentLevel;
    }



}
