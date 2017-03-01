package com.customcode420.caffeinecutter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;

public class DrinkSelection extends AppCompatActivity {

    DrinksDatabaseHelper dbHelper = new DrinksDatabaseHelper(this);
    ArrayList<String> nameArray = new ArrayList<>();
    ArrayList<Integer> cafContentArray = new ArrayList<>();
    ArrayList<String>  drinkIdArray = new ArrayList<>();
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialising Realm
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

        //Creating cursor to get data from db
        final Cursor cursor = dbHelper.queryDb("SELECT * FROM drinks;");
        cursor.moveToFirst();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            nameArray.add(cursor.getString(cursor.getColumnIndex("drinkName")));
            cafContentArray.add(cursor.getInt(cursor.getColumnIndex("cafContent")));
            drinkIdArray.add(cursor.getString(cursor.getColumnIndex("drinkId")));
        }


        //Creating Adapter for listview
        CustomAdapter customAdapter = new CustomAdapter(this, nameArray, cafContentArray);

        ListView listView = (ListView) findViewById(R.id.drinkSelectList);
        listView.setAdapter(customAdapter);

        //Passing Data back to main activity
        final Intent returnIntent = new Intent();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnIntent.putExtra("drinkId", drinkIdArray.get(position));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    public class CustomAdapter extends ArrayAdapter<String> {

        public CustomAdapter(Context context,
                             ArrayList<String> names,
                             ArrayList<Integer>cafContent) {
            super(context, 0, names);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.selection_list_item, parent, false);
            }
            // Lookup view for data population
            TextView drinkName = (TextView) convertView.findViewById(R.id.drinkSelectName);
            TextView cafContent = (TextView) convertView.findViewById(R.id.drinkContent);
            ImageView favButton = (ImageView) convertView.findViewById(R.id.)
            // Populate the data into the template view using the data object
            drinkName.setText(nameArray.get(position));
            cafContent.setText("Caffeine Content: " +
                    cafContentArray.get(position).toString() + " mg");
            // Return the completed view to render on screen
            return convertView;
        }
    }

}
