package com.customcode420.caffeinecutter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistoryAdapter extends BaseAdapter {
    //Declaring and initialising some necessary variables
    Context context;
    private Realm realm = Realm.getDefaultInstance();
    private static LayoutInflater inflater = null;
    final RealmResults<History> results = realm.where(History.class).findAll();

    //Constructor for the HistoryAdapter class
    public HistoryAdapter(Context context) {
        this.context = context;
        //Setting inflater var to inflater system service.
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    //Overriding some standard adapter methods
    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    //Overriding get view function
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        //Finding views in row items
        TextView contentNum = (TextView) convertView.findViewById(R.id.contentNum);
        TextView drinkId = (TextView) convertView.findViewById(R.id.drinkName);
        TextView timeView = (TextView) convertView.findViewById(R.id.timeTextView);
        //Putting History items from realm into RealmResults
        final RealmResults<History> results = realm.where(History.class).findAll();

        contentNum.setText(results.get(position).getCafContent().toString());
        drinkId.setText(results.get(position).getName());
        timeView.setText(results.get(position).getTime());

        return convertView;
    }
}
