package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mumtaz Hassan on 7/9/2016.
 */
public class UserprofileModel implements Serializable {
    public String id = "",
            name = "",
            username = "",
            cover_image = "",
            profile_image = "",
            followers_count = "",
            followings_count="",
            address="",
            email="",
            f_name="",
            l_name="";


    public boolean followed_by = false,
            favourited_by = false;

    public ArrayList<DishModel> dishes = new ArrayList<DishModel>();

    public ArrayList<FollowerModel> Followlist = new ArrayList<FollowerModel>();
}
