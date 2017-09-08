package com.peppa.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mumtaz Hassan on 7/16/2016.
 */
public class DishModel implements Serializable {

    public String id = "",             //Dish ID
                name = "",             //Dish Name
               image = "",             //Dish images
              rating = "",             //Dish rating of user
         description = "",             //Dish Description
               votes = "",             // Dish Votes
               price = "",             // Dish price
      average_rating = "",             // Avrage rating of dish

             rest_id = "",             // Restaurant Id
        rest_address = "",             //Restaurant address
   rest_phone_number = "",             // Restaurant phone number
       rest_latitude = "",             // Restaurant latitude
      rest_longitude = "",             // Restaurant longitude
           rest_name = "",
            distance = "",un_rated=""
    ;
    //Restaurant name


    public String imageId = "",
            DishComment   = "";              //Dish image id


    public  boolean IsRatingGiven  = false; //My user profile time is Check It give rating of Dish
    public boolean favourited_by   = false;

    public  boolean IsReposted     = false;     //My user profile time is Check It give rating of Dish

    public boolean MarkerclickDish = false;


    public ArrayList<DishCommentModel> dishCommentlist = new ArrayList<DishCommentModel>();

    public ArrayList<DishImageArray> dishImagelist     = new ArrayList<DishImageArray>();

    public ArrayList<String> dishTaglist               = new ArrayList<String>();


}
