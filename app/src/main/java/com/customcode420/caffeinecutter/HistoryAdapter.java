package com.customcode420.caffeinecutter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class HistoryAdapter extends BaseAdapter {

    Context context;
    private Realm realm = Realm.getDefaultInstance();
    private static LayoutInflater inflater = null;
    final RealmResults<History> results = realm.where(History.class).findAll();

    public HistoryAdapter(Context context) {
        this.context = context;
        //Setting inflater var to inflater system service.
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);


        //Finding views in row items
        TextView contentNum = (TextView) vi.findViewById(R.id.contentNum);
        TextView drinkName = (TextView) vi.findViewById(R.id.drinkName);
        TextView timeView = (TextView) vi.findViewById(R.id.timeTextView);
        //Putting History items from realm into RealmResults
        final RealmResults<History> results = realm.where(History.class).findAll();


        contentNum.setText(results.get(position).getCafContent().toString());
        drinkName.setText(results.get(position).getName());
        timeView.setText(results.get(position).getTime());

        return vi;
    }
}
