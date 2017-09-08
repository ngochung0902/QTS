package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mumtaz Hassan on 7/9/2016.
 */
public class RestaurantModel implements Serializable {
    public String id = "",
            name = "",
            address = "",
            phone_number = "",
            latitude = "",
            longitude = "",
            followers_count = "",
            rating = "",
            restaurant_image="",
            votes="0";

    public boolean followed_by = false,
            favourited_by = false;

    public ArrayList<DishModel> dishes = new ArrayList<DishModel>();

    public ArrayList<FollowerModel> Followlist = new ArrayList<FollowerModel>();
}
