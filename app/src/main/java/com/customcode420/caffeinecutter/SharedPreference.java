package com.customcode420.caffeinecutter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;


public class SharedPreference {

    public static final String PREFS_NAME = "CAFFEINE_APP";
    public static final String FAVORITES = "Drink_Favorite";

    public SharedPreference() {
        super();
    }

    // These four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<Drink> favorites) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, Drink drink) {
        List<Drink> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Drink>();
        favorites.add(drink);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, Drink drink) {
        ArrayList<Drink> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(drink);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<Drink> getFavorites(Context context) {
        SharedPreferences settings;
        List<Drink> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Drink[] favoriteItems = gson.fromJson(jsonFavorites,
                    Drink[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Drink>(favorites);
        } else
            return null;

        return (ArrayList<Drink>) favorites;
    }
}