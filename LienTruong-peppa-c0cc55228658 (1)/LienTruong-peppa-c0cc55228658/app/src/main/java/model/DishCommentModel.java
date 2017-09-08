package model;

import java.io.Serializable;

/**
 * Created by AtharvaSystem on 20-09-2016.
 */
public class DishCommentModel implements Serializable {

    public String userpProfileImage="",
           userId="",
           user_Name="",
           userComment="",
           userRatingforDish="";

    public boolean favourited_by = false;

}
