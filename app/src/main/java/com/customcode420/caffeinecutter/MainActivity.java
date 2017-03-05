package com.customcode420.caffeinecutter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    Integer currentLevel = 0;
    Integer oldLevel = 0;
    private Realm realm;
    DrinksDatabaseHelper dbHelper = new DrinksDatabaseHelper(this);
    ProgressBarAnimation animation = null;
    ProgressBar caffeineMeter = null;
    TextView levelNum = null;
    HistoryAdapter historyAdapter = null;

    SharedPreferences sharedPreferences = null;
    SharedPreference sharedPrefs = new SharedPreference();
    int dailyCaffeine = 0;

    ArrayList<Drink> favDrinks = new ArrayList<>(Arrays.asList(new Drink("instantCoffee", "Instant Coffee"),
            new Drink("instantCoffee", "Instant Coffee"),
            new Drink("brewedCoffee", "Brewed Coffee"),
            new Drink("brewedCoffee", "Brewed Coffee")));

    ArrayList<Drink> defaultDrinks = new ArrayList<>(Arrays.asList(new Drink("instantCoffee", "Instant Coffee"),
            new Drink("instantCoffee", "Instant Coffee"),
            new Drink("brewedCoffee", "Brewed Coffee"),
            new Drink("brewedCoffee", "Brewed Coffee")));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialising SharedPreferences
        sharedPreferences = getSharedPreferences("com.customcode420.caffeinecutter", MODE_PRIVATE);

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
                .setTitle("Input Your Daily Caffeine Intake:")
                .setCancelable(false)
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
                        //Using try/catch statement to validate that the number entered is correct
                        try {
                            dailyCaffeine = Integer.parseInt(input.getText().toString());

                            sharedPreferences.edit().putInt("dailyCaffeine", dailyCaffeine).apply();
                            builder.dismiss();


                            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

                            dailyCaffeine = sharedPreferences.getInt("dailyCaffeine", 300);
                            //Setting max value of progress bar to daily caffeine intake
                            int tempInt = (int) todayCaf(dailyCaffeine);
                            progressBar.setMax(tempInt);

                            TextView todayMax = (TextView) findViewById(R.id.todayMax);
                            todayMax.setText(Integer.toString(tempInt));
                        } catch (NumberFormatException e){
                            Toast.makeText(getBaseContext(), "Please Enter A Valid Number", Toast.LENGTH_SHORT).show();
                        }
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

        //Defining FABs for favourite buttons.
        final FloatingActionButton fab1 =
                (FloatingActionButton) findViewById(R.id.instantCoffee250);
        final FloatingActionButton fab2 =
                (FloatingActionButton) findViewById(R.id.instantCoffee500);
        final FloatingActionButton fab3 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee250);
        final FloatingActionButton fab4 =
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
        //Setting animation for progress bar
        animation = new ProgressBarAnimation(caffeineMeter);
        animation.setDuration(500);


        dailyCaffeine = sharedPreferences.getInt("dailyCaffeine", 300);
        //Setting max value of progress bar to daily caffeine intake
        int tempInt = (int) todayCaf(dailyCaffeine);
        caffeineMeter.setMax(tempInt);

        TextView todayMax = (TextView) findViewById(R.id.todayMax);
        todayMax.setText(Integer.toString(tempInt));


        //Checking if it's the first run. If it is then firstrun is set to false and is committed.
        if (sharedPreferences.getBoolean("firstrun", true)) {
            //Showing Dialog
            builder.show();
            sharedPreferences.edit().putBoolean("firstrun", false).commit();

            Date date = new Date(System.currentTimeMillis());
            sharedPreferences.edit().putLong("startDate", date.getTime()).commit();
        }


        //Setting max value of progress bar to daily caffeine intake
        ArrayList<FloatingActionButton> fabsArray = new ArrayList<>(Arrays.asList(fab1,
                fab2,
                fab3,
                fab4));

        //Setting labels and ids for FABs if favDrinks is not empty
        if (sharedPrefs.getFavorites(this) != null) {
            favDrinks = sharedPrefs.getFavorites(this);
            for (Drink drink : favDrinks) {
                fabsArray.get(favDrinks.indexOf(drink)).setLabelText(drink.getDrinkName());
            }
        } else {
            favDrinks = new ArrayList<>(Arrays.asList(new Drink("instantCoffee", "Instant Coffee"),
                    new Drink("instantCoffee", "Instant Coffee"),
                    new Drink("brewedCoffee", "Brewed Coffee"),
                    new Drink("brewedCoffee", "Brewed Coffee")));
        }

        //Check if the fav drinks is of size 4, if not then pad using values from defaultDrinks array
        if(favDrinks.size() < 4){
            while (favDrinks.size() != 4){
                favDrinks.add(defaultDrinks.get(favDrinks.size() - 1));
                //Resetting name for FABs to be accurate
                fabsArray.get(favDrinks.size() - 1).setLabelText(favDrinks.get(favDrinks.size() - 1).getDrinkName());
            }
        }

        //Creating onClickListeners with appropriate drinkIds
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDrink(favDrinks.get(0).getDrinkId());
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDrink(favDrinks.get(1).getDrinkId());
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDrink(favDrinks.get(2).getDrinkId());
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDrink(favDrinks.get(3).getDrinkId());
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

                    //Adding current level to sharedPreferences
                    sharedPreferences.edit().putInt("cafLevel", currentLevel).apply();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            results.deleteLastFromRealm();
                        }
                    });
                }
                checkColour(currentLevel, caffeineMeter);
            }
        });
        //Restoring caffeine meter progress and current level
        caffeineMeter.setProgress(sharedPreferences.getInt("cafLevel", 0));
        currentLevel = sharedPreferences.getInt("cafLevel", 0);
        levelNum.setText(Integer.toString(sharedPreferences.getInt("cafLevel", 0)));
    }

    //Adding drink after drink selection activity is closed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //Checking if drinkId was passed
                if (data.getStringExtra("drinkId") != null)
                    addDrink(data.getStringExtra("drinkId"));

            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }

            sharedPrefs.getFavorites(this);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Setting progress bar and level text to caffeine level from shared prefs
        caffeineMeter.setProgress(sharedPreferences.getInt("cafLevel", 0));
        levelNum.setText(Integer.toString(sharedPreferences.getInt("cafLevel", 0)));

        final FloatingActionButton fab1 =
                (FloatingActionButton) findViewById(R.id.instantCoffee250);
        final FloatingActionButton fab2 =
                (FloatingActionButton) findViewById(R.id.instantCoffee500);
        final FloatingActionButton fab3 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee250);
        final FloatingActionButton fab4 =
                (FloatingActionButton) findViewById(R.id.brewedCoffee500);

        ArrayList<FloatingActionButton> fabsArray = new ArrayList<>(Arrays.asList(fab1,
                fab2,
                fab3,
                fab4));

        favDrinks = sharedPrefs.getFavorites(this);

        //Setting labels and ids for FABs if favDrinks is not empty
        if (sharedPrefs.getFavorites(this) != null) {
            favDrinks = sharedPrefs.getFavorites(this);
            for (Drink drink : favDrinks) {
                fabsArray.get(favDrinks.indexOf(drink)).setLabelText(drink.getDrinkName());
            }
        } else {
            favDrinks = new ArrayList<>(Arrays.asList(new Drink("instantCoffee", "Instant Coffee"),
                    new Drink("instantCoffee", "Instant Coffee"),
                    new Drink("brewedCoffee", "Brewed Coffee"),
                    new Drink("brewedCoffee", "Brewed Coffee")));
        }

        //Check if the fav drinks is of size 4, if not then pad using values from defaultDrinks array
        if(favDrinks.size() < 4){
            while (favDrinks.size() != 4){
                favDrinks.add(defaultDrinks.get(favDrinks.size() - 1));
                //Resetting name for FABs to be accurate
                fabsArray.get(favDrinks.size() - 1).setLabelText(favDrinks.get(favDrinks.size() - 1).getDrinkName());
            }
        }
        checkColour(currentLevel, caffeineMeter);
    }
    public void checkColour(Integer currentLevel, ProgressBar caffeineMeter){
        Context context = this;
        if (currentLevel < (caffeineMeter.getMax() / 2)){
            //Changing colour to green if caffeine level is lower than half of max of meter
            caffeineMeter.getProgressDrawable().setColorFilter(Color.parseColor("#4CAF50"), PorterDuff.Mode.SRC_IN);

            //If android version is over above lollipop then change colour of statusbar and navbar too too
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = ((Activity) context).getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#4CAF50"));
            }
        } else if(currentLevel > (caffeineMeter.getMax() / 2) && currentLevel < caffeineMeter.getMax()){
            //Changing colour to orange if caffeine level is higher than half max of meter
            caffeineMeter.getProgressDrawable().setColorFilter(Color.parseColor("#FF9800"), PorterDuff.Mode.SRC_IN);

            //If android version is over above lollipop then change colour of statusbar and navbar too too
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = ((Activity) context).getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#FF9800"));
            }
        } else {
            //Changing colour to red if caffeine level is too high
            caffeineMeter.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

            //If android version is over above lollipop then change colour of statusbar and navbar too too
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = ((Activity) context).getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.RED);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Setting progress bar to caffeine level from shared prefs
        caffeineMeter.setProgress(sharedPreferences.getInt("cafLevel", 0));
    }

    public void addDrink(final String drinkId){
        final Integer cafContent = getCafContent(drinkId);

        currentLevel =  Integer.parseInt(levelNum.getText().toString());
        oldLevel = currentLevel;

        currentLevel += cafContent;

        //Adding current level to sharedPreferences
        sharedPreferences.edit().putInt("cafLevel", currentLevel).apply();
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

        checkColour(currentLevel, caffeineMeter);
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

    public double todayCaf(Integer dailyCaffeine){
//        Date date = new GregorianCalendar(2017, 2, 3).getTime();
        Date date = new Date(sharedPreferences.getLong("startDate", 0));
        Date todayDate = new Date(System.currentTimeMillis());
        long diff = todayDate.getTime() - date.getTime();
        long difference = TimeUnit.MILLISECONDS.toDays(TimeUnit.DAYS.convert(diff, TimeUnit.DAYS));

        double multiplier = dailyCaffeine * 0.10;
        return (dailyCaffeine - (multiplier * difference));
    }
}
