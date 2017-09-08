package com.peppa.app.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.peppa.app.model.SearchDishModel;
import com.peppa.app.parsing.WebFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class DishAutoCompleteAdapter extends ArrayAdapter implements Filterable {
    private ArrayList<SearchDishModel> dishModels = new ArrayList<>();
    private Filter mFilter;
    private String token;
    @Override
    public int getCount() {
        return dishModels.size();
    }

    @Override
    public String getItem(int index) {
        if (dishModels.size() > 0 && index <= dishModels.size()) {
            return String.valueOf(dishModels.get(index).strDishName);
        } else {
            return "";
        }
    }
    public SearchDishModel getItemDish(int position) {
        return dishModels.get(position);
    }

    public DishAutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        token = prefs.getString("token", "");
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
                        dishModels = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = dishModels;
                        filterResults.count = dishModels.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults results) {
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

    private ArrayList<SearchDishModel> autocomplete(String s) {
        String rest = WebFunctions.getRestaurantDishlist(s, token);
        ArrayList<SearchDishModel> dishModels = new ArrayList<>();
        Log.e("RestaurantAdd", "rest:" +rest);
        if (!rest.toLowerCase().contains("sorry")) {
            try {
                JSONObject posOBJ = new JSONObject(rest);
                JSONArray dishesArr = posOBJ.getJSONArray("dishes");

                for (int j = 0; j < dishesArr.length(); j++) {
                    SearchDishModel DishOBJ = new SearchDishModel();
                    JSONObject object = dishesArr.getJSONObject(j);

                    DishOBJ.id = object.getString("id");
                    DishOBJ.strDishName = object.getString("name");
                    DishOBJ.strDishprice = object.getString("price");

                    dishModels.add(DishOBJ);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dishModels;
    }
}