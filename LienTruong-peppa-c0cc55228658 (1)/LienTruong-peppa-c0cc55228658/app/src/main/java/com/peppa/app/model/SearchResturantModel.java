package com.peppa.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rup barad on 22-09-2016.
 */
public class SearchResturantModel implements Serializable {

    public String Restid = "",
             strRestName = "";

    public ArrayList<RestaurantLocationModel> RestLocationlist = new ArrayList<RestaurantLocationModel>();


}
