package com.example.pavinaveen.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {

    ArrayList<Places> customers, tempCustomer, suggestions;

    public CustomAdapter(Context context, ArrayList<Places> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.customers = objects;
        this.tempCustomer = new ArrayList<Places>(objects);
        this.suggestions = new ArrayList<Places>(objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Places places = (Places) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.autocompleteitem, parent, false);
        }
        TextView txtCustomer = (TextView) convertView.findViewById(R.id.tvCustomer);
        txtCustomer.setText(places.getPlace());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Places places = (Places) resultValue;
            return places.getPlace();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Places places : tempCustomer) {
                    if (places.getPlace().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(places);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            ArrayList<Places> c = (ArrayList<Places>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Places cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }

        }
    };
}
