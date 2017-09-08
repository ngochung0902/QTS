package com.peppa.app.Adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.peppa.app.model.PlacePredictionModel;
import com.peppa.app.parsing.WebFunctions;

import java.util.ArrayList;

/**
 * Created by Rup barad on 28-12-2016.
 */

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
    private ArrayList<PlacePredictionModel> resultList = new ArrayList<>();
    private Filter mFilter;

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public GooglePlacesAutocompleteAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public PlacePredictionModel getItem(int index) {
        return resultList.get(index);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = WebFunctions.autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }
        return mFilter;
    }
}

