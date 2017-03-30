package com.customcode420.caffeinecutter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class DrinkSelection extends AppCompatActivity {

    DrinksDatabaseHelper dbHelper = new DrinksDatabaseHelper(this);
    ArrayList<String> nameArray = new ArrayList<>();
    ArrayList<Integer> cafContentArray = new ArrayList<>();
    ArrayList<String>  drinkIdArray = new ArrayList<>();
    private Realm realm;
    private Context context;
    final Intent returnIntent = new Intent();

    private Toast toast;
    ArrayList<Drink> drinkList = new ArrayList<>();
    private SharedPreference sharedPrefs = new SharedPreference();

    private ArrayList<ToggleButton> buttonList = new ArrayList<>();

    public DrinkSelection(){

    }

    public DrinkSelection(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getBaseContext();

        //Declaring toast for favIds
        toast = Toast.makeText(getBaseContext(), "You already have 4 favourites", Toast.LENGTH_SHORT);

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
            Drink drink = new Drink(cursor.getPosition(), cursor.getString(cursor.getColumnIndex("drinkId")),
                    cursor.getString(cursor.getColumnIndex("drinkName")),
                    cursor.getInt(cursor.getColumnIndex("cafContent")));
            drinkList.add(drink);
        }

        //Creating Adapter for listview
        CustomAdapter customAdapter = new CustomAdapter(context, drinkList);

        ListView listView = (ListView) findViewById(R.id.drinkSelectList);
        listView.setAdapter(customAdapter);

        //Passing Data back to main activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Passing drink to intent so I can retreive it in the main activity
                returnIntent.putExtra("drinkId", drinkList.get(position).getDrinkId());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK, returnIntent);

        super.onBackPressed();
    }

    public class CustomAdapter extends ArrayAdapter<Drink> {

        public CustomAdapter(Context context, ArrayList<Drink> arrayList) {
            super(context, 0, drinkList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.selection_list_item, parent, false);
            }
            // Lookup view for data population
            TextView drinkName = (TextView) convertView.findViewById(R.id.drinkSelectName);
            TextView cafContent = (TextView) convertView.findViewById(R.id.drinkContent);
            final ToggleButton favButton = (ToggleButton) convertView.findViewById(R.id.favButton);

            final List<Drink> favorites = sharedPrefs.getFavorites(context);

            //Turning on toggle button if favourites list is not null and contains the relevant drink object
            if (favorites != null && favorites.contains(drinkList.get(position)))
                favButton.setChecked(true);

            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Drink> favorites = sharedPrefs.getFavorites(context);
                    //Adding favourites based off if toggle button is checked or not
                    if (favButton.isChecked()){
                        if (favorites != null && favorites.size() <= 3){

                            //Adding drink object from drinkList to sharedPrefs
                            if (!favorites.contains(drinkList.get(position)))
                                sharedPrefs.addFavorite(context, drinkList.get(position));

                        } else if(favorites == null) {

                            sharedPrefs.addFavorite(context, drinkList.get(position));
                        } else {

                            favButton.setChecked(false);
                            toast.show();
                        }
                    } else {
                        if (favorites != null && favorites.contains(drinkList.get(position)))
                            sharedPrefs.removeFavorite(context, drinkList.get(position));

                    }
                }
            });


            // Populate the data into the template view using the data object
            drinkName.setText(drinkList.get(position).getDrinkName());
            cafContent.setText("Caffeine Content: " +
                    drinkList.get(position).getCafContent() + " mg");
            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public int getCount() {
            return drinkList.size();
        }

        @Override
        public Drink getItem(int position) {
            return drinkList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
    //Method to check if current drink exists in Shared Prefs
    public boolean checkFavoriteItem(Drink checkDrink) {
        boolean check = false;
        List<Drink> favorites = sharedPrefs.getFavorites(context);
        if (favorites != null) {
            for (Drink drink : favorites) {
                if (drink.equals(checkDrink)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

}
