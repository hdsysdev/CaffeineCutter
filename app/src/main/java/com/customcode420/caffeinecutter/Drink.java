package com.customcode420.caffeinecutter;


import android.database.Cursor;
import android.database.SQLException;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;

import io.realm.Realm;

public class Drink {
    private Integer oldLevel;
    private Integer currentLevel;
    private TextView levelNum;
    private ProgressBarAnimation animation;
    private DrinksDatabaseHelper dbHelper;
    private ProgressBar caffeineMeter;
    private HistoryAdapter historyAdapter;
    private Realm realm;

    //Using constructor to pass relevant variables from MainActivity
    public Drink(Integer oldLevel, Integer currentLevel, TextView levelNum,
                 ProgressBarAnimation animation, DrinksDatabaseHelper dbHelper,
                 ProgressBar caffeineMeter, HistoryAdapter historyAdapter, Realm realm) {
        this.oldLevel = oldLevel;
        this.currentLevel = currentLevel;
        this.levelNum = levelNum;
        this.animation = animation;
        this.caffeineMeter = caffeineMeter;
        this.historyAdapter = historyAdapter;
        this.dbHelper = dbHelper;
        this.realm = realm;
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
    public void addDrink(final String drinkId){
        final Integer cafContent = getCafContent(drinkId);

        currentLevel += cafContent;
        levelNum.setText(currentLevel.toString());
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

    public Integer getCurrentLevel() {
        return currentLevel;
    }
}
