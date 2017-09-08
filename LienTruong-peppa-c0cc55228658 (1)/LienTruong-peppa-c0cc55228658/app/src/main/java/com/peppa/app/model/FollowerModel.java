package com.peppa.app.model;

import java.io.Serializable;

/**
 * Created by Mumtaz Hassan on 8/27/2016.
 */
public class FollowerModel implements Serializable {

    //People list

    public String username = "",
                 firstname = "",
                  lastname = "",
                     email = "",
            followersCount = "",
              profileimage = "",
                        id = "";

    public boolean isfollow = false,
                isfollowing = false;

    //Restaurant list
    public String Rest_id  = "",
                 Rest_name = "",
              Rest_address = "",
             Rest_latitude = "",
            Rest_longitude = "",
             Rest_phone_no = "",
               Rest_rating = "",
       Rest_followerscount = "";

    public boolean isRest_follow = false;

}
