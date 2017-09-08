package com.peppa.app.model;

import java.io.Serializable;

/**
 * Created by Rup barad on 20-09-2016.
 */
public class DishCommentModel implements Serializable {

    public String userpProfileImage ="",
                             userId = "",
                          user_Name = "",
                        userComment = "",
                  userRatingforDish = "",
                      userFirstname = "",
                       userLastname = "" ;

    public boolean favourited_by    = false;

}
