package com.example.meteovilles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CityAdapter extends BaseAdapter {

    private Context context;
    private List<City> cities;
    private LayoutInflater inflater;

    public CityAdapter(Context context, List<City> cities) {
        this.context = context;
        this.cities = cities;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(cities.get(position).toString());

        return convertView;
    }

    public void updateCities(List<City> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }
}
