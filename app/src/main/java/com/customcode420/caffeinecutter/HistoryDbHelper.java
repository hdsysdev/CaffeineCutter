/*
package com.customcode420.caffeinecutter;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.customcode420.caffeinecutter.HistoryDatabaseContract.*;

public class HistoryDbHelper extends SQLiteOpenHelper{
    //Assigning variable settings
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "History.db";

    //Declaring constants to use later for queries
    private static final String TEXT_TYPE = " TEXT";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String TIME_TYPE = " TIME";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " +
                    FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CONTENT + FLOAT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TIME + TIME_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HistoryDatabaseContract.FeedEntry.TABLE_NAME;

    public HistoryDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
*/
