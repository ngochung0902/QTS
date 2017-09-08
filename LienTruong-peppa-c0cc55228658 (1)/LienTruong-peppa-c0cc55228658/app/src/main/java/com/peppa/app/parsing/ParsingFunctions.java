package com.peppa.app.parsing;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.peppa.app.R;
import com.peppa.app.model.DishCommentModel;
import com.peppa.app.model.DishImageArray;
import com.peppa.app.model.DishModel;
import com.peppa.app.model.FollowerModel;
import com.peppa.app.model.RestaurantModel;
import com.peppa.app.model.SearchModel;
import com.peppa.app.model.UserModel;
import com.peppa.app.model.UserprofileModel;
import com.peppa.app.utility.Generalfunction;

public class ParsingFunctions {

    //Parse Model - User model ( Navigation, Setting activity and setting fragment )
    public static UserModel parseUserModel(String murl,Context context){
        UserModel postOBJ = new UserModel();
        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl).getJSONObject("user");
                postOBJ.un_rated=new JSONObject(murl).getString("un_rated_dishes");

                postOBJ.id           = String.valueOf(posOBJ.getInt("id"));
                postOBJ.first_name   = posOBJ.getString("first_name");
                postOBJ.last_name    = posOBJ.getString("last_name");
                postOBJ.email        = posOBJ.getString("email");
                postOBJ.username     = posOBJ.getString("username");
                postOBJ.coverPhoto   = posOBJ.getString("cover_image");
                postOBJ.profilePhoto = posOBJ.getString("profile_image");

                //Log.d(Constant.TAG, "Useriamge: +parseUserModel: "+posOBJ.getString("profile_image"));

                Generalfunction.SaveUserDetail(context,posOBJ.getString("first_name"),posOBJ.getString("last_name"),posOBJ.getString("username"),posOBJ.getString("id"),
                        posOBJ.getString("email"),posOBJ.getString("profile_image"),"");
//                Generalfunction.SaveUserDetail(context,posOBJ.getString("first_name"),posOBJ.getString("last_name"),posOBJ.getString("username"),posOBJ.getString("id"),
//                        posOBJ.getString("email"),"","");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return postOBJ;
    }



    //Parse Model - Restaurant profile
    public static RestaurantModel parseRestaurantModel(String murl,Context mcontext,String strfilterdata){
        RestaurantModel postOBJ = new RestaurantModel();
        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl).getJSONObject("restaurant");
                postOBJ.un_rated=new JSONObject(murl).getString("un_rated_dishes");

                //Restaurant Detail
                postOBJ.id              = posOBJ.getString("id");
                postOBJ.name            = posOBJ.getString("name");
                postOBJ.address         = posOBJ.getString("address");
                postOBJ.latitude        = posOBJ.getString("latitude");
                postOBJ.longitude       = posOBJ.getString("longitude");

                postOBJ.phone_number    = Generalfunction.Isnull(posOBJ.getString("phone_number"));
                postOBJ.favourited_by   = posOBJ.getBoolean("favourited_by");
                postOBJ.followed_by     = posOBJ.getBoolean("followed_by");
                postOBJ.followers_count = posOBJ.getString("followers_count");
                postOBJ.rating          = Generalfunction.IsnullRating(posOBJ.getString("rating"));
                postOBJ.votes           = posOBJ.getString("votes");

                if(Generalfunction.isEmptyCheck(Generalfunction.Isnull(strfilterdata))) {
                    Generalfunction.SaveFilterMin_MaxPrice(mcontext,posOBJ.getInt("min_price"),posOBJ.getInt("max_price"));
                }

                //Restaurant dish list and detail
                JSONArray dishesArray = posOBJ.getJSONArray("dishes");

                for (int j = 0; j < dishesArray.length(); j++) {

                    DishModel DishOBJ = new DishModel();
                    JSONObject object = dishesArray.getJSONObject(j);

                    DishOBJ.id          = object.getString("id");
                    DishOBJ.rest_id     = object.getString("restaurant_id");
                    DishOBJ.name        = object.getString("name");
                    DishOBJ.description = object.getString("description");

                    String price=Generalfunction.Isnull(object.getString("price"));
                    if(Generalfunction.isEmptyCheck(price)){
                        price="0";
                    }

                    DishOBJ.price           = price;
                    DishOBJ.favourited_by   = object.getBoolean("favourite");
                    DishOBJ.average_rating  = Generalfunction.IsnullRating(object.getString("average_rating"));

                    DishOBJ.rest_phone_number = Generalfunction.Isnull(posOBJ.getString("phone_number"));
                    DishOBJ.rest_latitude     = posOBJ.getString("latitude");
                    DishOBJ.rest_longitude    = posOBJ.getString("longitude");
                    DishOBJ.rest_name         = posOBJ.getString("name");
                    DishOBJ.rest_address      = posOBJ.getString("address");
                    DishOBJ.votes             = Generalfunction.Isnull(posOBJ.getString("votes"));

                    DishOBJ.distance = Generalfunction.CalculateDistance(Generalfunction.Isnull(posOBJ.getString("latitude")),Generalfunction.Isnull(posOBJ.getString("longitude")),mcontext);


                    //image array
                    try {
                        JSONObject objectimage = object.getJSONObject("image");
                        DishOBJ.image          = objectimage.getString("image_url");

                    }catch(Exception e){   //e.printStackTrace();
                    }
                    postOBJ.dishes.add(DishOBJ);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postOBJ;
    }


    //Parse Model - Dish detail
    public static DishModel parseDishModel(String murl){
        DishModel postOBJ = new DishModel();
        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl).getJSONObject("dish");
               postOBJ.un_rated=new JSONObject(murl).getString("un_rated_dishes");

                //Dish Detail
                postOBJ.id          = posOBJ.getString("id");
                postOBJ.name        = Generalfunction.Isnull(posOBJ.getString("name"));
                postOBJ.rest_id     = posOBJ.getString("restaurant_id");
                postOBJ.description = Generalfunction.Isnull(posOBJ.getString("description"));

                String price = Generalfunction.Isnull(posOBJ.getString("price"));
                if(Generalfunction.isEmptyCheck(price)){
                    price="0";
                }

                postOBJ.price         = price;
                postOBJ.rest_name     = Generalfunction.Isnull(posOBJ.getString("restaurant_name"));
                postOBJ.favourited_by = posOBJ.getBoolean("favourited_by");
                postOBJ.average_rating= Generalfunction.IsnullRating(posOBJ.getString("average_rating"));
                postOBJ.votes         = Generalfunction.Isnull(posOBJ.getString("votes"));

                //restaurant detail
                JSONObject posOBJRest = posOBJ.getJSONObject("restaurant");

                postOBJ.rest_id           = posOBJRest.getString("id");
                postOBJ.rest_name         = Generalfunction.Isnull(posOBJRest.getString("name"));
                postOBJ.rest_address      = Generalfunction.Isnull(posOBJRest.getString("address"));
                postOBJ.rest_phone_number = Generalfunction.Isnull(posOBJRest.getString("phone_number"));
                postOBJ.rest_latitude     = Generalfunction.Isnull(posOBJRest.getString("latitude"));
                postOBJ.rest_longitude    = Generalfunction.Isnull(posOBJRest.getString("longitude"));

                //Dish Comment
                JSONArray dishCommentsArray = posOBJ.getJSONArray("comments");

                for (int j = 0; j < dishCommentsArray.length(); j++) {

                    DishCommentModel DishOBJ = new DishCommentModel();
                    JSONObject object = dishCommentsArray.getJSONObject(j);

                    DishOBJ.userComment=Generalfunction.Isnull(object.getString("comment"));

                    JSONObject objUser=object.getJSONObject("user");    //user detail which give comment
                    DishOBJ.userId            = objUser.getString("id");
                    DishOBJ.userpProfileImage = objUser.getString("profile_image");
                    DishOBJ.user_Name         = Generalfunction.Isnull(objUser.getString("name"));
                    DishOBJ.userRatingforDish = Generalfunction.IsnullRating(objUser.getString("rating"));
                    DishOBJ.favourited_by     = objUser.getBoolean("favourite");
                    DishOBJ.userFirstname     = Generalfunction.Isnull(objUser.getString("first_name"));
                    DishOBJ.userLastname      = Generalfunction.Isnull(objUser.getString("last_name"));
                    postOBJ.dishCommentlist.add(DishOBJ);

                }

                //Dish images
                JSONArray dishImageArray=posOBJ.getJSONArray("images");
                if(dishImageArray != null){
                    for(int k=0; k<dishImageArray.length(); k++) {
                        JSONObject object_dishImage = dishImageArray.getJSONObject(k);

                        postOBJ.image                = object_dishImage.getString("image_url");
                        DishImageArray dish_image    = new DishImageArray();
                        dish_image.image_id          = object_dishImage.getString("id");
                        dish_image.dish_image_url    = object_dishImage.getString("image_url");
                        dish_image.user_id           = object_dishImage.getString("user_id");
                        dish_image.imagepost_username= object_dishImage.getString("user_name");
                        postOBJ.dishImagelist.add(dish_image);
                    }
                }

                //Dish Tag
                JSONArray dishTagArray=posOBJ.getJSONArray("tags");
                if(dishTagArray != null) {
                    for (int l = 0; l < dishTagArray.length(); l++) {
                        String strTag = dishTagArray.getString(l).toString();
                        postOBJ.dishTaglist.add(strTag);
                    }
                }

            } catch (JSONException e) {}
        }

        return postOBJ;
    }


    //Parse Model - Dish comments(Dish detail) pagination
    public static ArrayList<DishCommentModel> parseDishComment(String murl){

        DishModel postOBJ = new DishModel();
        ArrayList<DishCommentModel> Dishcommentlist=new ArrayList<>();
        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl).getJSONObject("dish");

                //Dish Comment
                JSONArray dishCommentsArray = posOBJ.getJSONArray("comments");

                for (int j = 0; j < dishCommentsArray.length(); j++) {

                    DishCommentModel DishOBJ = new DishCommentModel();
                    JSONObject object = dishCommentsArray.getJSONObject(j);

                    DishOBJ.userComment=Generalfunction.Isnull(object.getString("comment"));

                    JSONObject objUser=object.getJSONObject("user");    //user detail which give comment
                    DishOBJ.userId            = objUser.getString("id");
                    DishOBJ.userpProfileImage = objUser.getString("profile_image");
                    DishOBJ.user_Name         = Generalfunction.Isnull(objUser.getString("name"));
                    DishOBJ.userRatingforDish = Generalfunction.IsnullRating(objUser.getString("rating"));
                    DishOBJ.favourited_by     = objUser.getBoolean("favourite");
                    DishOBJ.userFirstname     = Generalfunction.Isnull(objUser.getString("first_name"));
                    DishOBJ.userLastname      = Generalfunction.Isnull(objUser.getString("last_name"));
                    postOBJ.dishCommentlist.add(DishOBJ);

                }
            } catch (JSONException e) { }
        }

        return Dishcommentlist;
    }


    //Parse Model - Result
    public static SearchModel parseSearchOBJ(String murl,Context mcontext,String strfilterdata){
        SearchModel postModel = new SearchModel();
        if(murl != null)
        {
            try {

                try {
                    JSONObject jsonObject = new JSONObject(murl);
                    if (murl.toLowerCase().contains("sorry")) {
                        if (jsonObject.has("success")) {
                            if (jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                                postModel.Message = jsonObject.getString("message");
                            } else {
                                postModel.Message = jsonObject.getString("message");
                            }
                        }
                    }

                    if(Generalfunction.isEmptyCheck(Generalfunction.Isnull(strfilterdata))) {
                        Generalfunction.SaveFilterMin_MaxPrice(mcontext, jsonObject.getInt("min_price"), jsonObject.getInt("max_price"));
                    }

                }catch(Exception e){}

                JSONArray postArray   = new JSONObject(murl).getJSONArray("restaurants");
                JSONArray dishesArray = new JSONObject(murl).getJSONArray("dishes");
                JSONObject posOBJ = null;

                //Search - Restaurants
                if(postArray != null){
                    for (int i=0; i<postArray.length(); i ++ ) {

                        posOBJ = postArray.getJSONObject(i);
                        RestaurantModel postOBJ = new RestaurantModel();

                        postOBJ.id            = Generalfunction.Isnull(posOBJ.getString("id"));
                        postOBJ.name          =  Generalfunction.Isnull(posOBJ.getString("name"));
                        postOBJ.address       =  Generalfunction.Isnull(posOBJ.getString("address"));
                        postOBJ.latitude      =  Generalfunction.Isnull(posOBJ.getString("latitude"));
                        postOBJ.longitude     =  Generalfunction.Isnull(posOBJ.getString("longitude"));

                        postOBJ.phone_number  = Generalfunction.Isnull(posOBJ.getString("phone_number"));
                        postOBJ.favourited_by = posOBJ.getBoolean("favourited_by");
                        postOBJ.followed_by   = posOBJ.getBoolean("followed_by");
                        postOBJ.rating        =  Generalfunction.IsnullRating(posOBJ.getString("rating"));

                        postModel.restaurants.add(postOBJ);
                    }
                }

                //Search - Dishes
                if(dishesArray != null){
                    for (int j = 0; j < dishesArray.length(); j++) {
                        DishModel DishOBJ = new DishModel();
                        JSONObject object = dishesArray.getJSONObject(j);

                        DishOBJ.id            =  Generalfunction.Isnull(object.getString("id"));
                        DishOBJ.name          =  Generalfunction.Isnull(object.getString("name"));
                        DishOBJ.description   =  Generalfunction.Isnull(object.getString("description"));

                        String price=Generalfunction.Isnull(object.getString("price"));
                        if(Generalfunction.isEmptyCheck(price)){
                            price="0";
                        }
                        DishOBJ.price         = price;

                        DishOBJ.favourited_by = object.getBoolean("favourited_by");
                        DishOBJ.average_rating=  Generalfunction.IsnullRating(object.getString("average_rating"));
                        DishOBJ.votes= Generalfunction.Isnull(object.getString("votes"));

                        //Restaurant details
                        JSONObject ObjRest=object.getJSONObject("restaurant");

                        DishOBJ.rest_id           = ObjRest.getString("id");
                        DishOBJ.rest_name         = Generalfunction.Isnull(ObjRest.getString("name"));
                        DishOBJ.rest_address      = Generalfunction.Isnull(ObjRest.getString("address"));
                        DishOBJ.rest_latitude     = Generalfunction.Isnull(ObjRest.getString("latitude"));
                        DishOBJ.rest_longitude    = Generalfunction.Isnull(ObjRest.getString("longitude"));
                        DishOBJ.rest_phone_number = Generalfunction.Isnull(ObjRest.getString("phone_number"));

                        //Dish image
                        JSONArray dishImageArray=object.getJSONArray("images");
                        if(dishImageArray != null){
                            for(int k=0; k<dishImageArray.length(); k++) {
                                JSONObject object_dishImage = dishImageArray.getJSONObject(k);
                                if(k==0) {
                                    DishOBJ.image = Generalfunction.Isnull(object_dishImage.getString("image_url"));
                                }
                                DishImageArray dish_image=new DishImageArray();
                                dish_image.user_id        = Generalfunction.Isnull(object_dishImage.getString("id"));
                                dish_image.dish_image_url = Generalfunction.Isnull(object_dishImage.getString("image_url"));
                                DishOBJ.dishImagelist.add(dish_image);
                            }
                        }

                        postModel.dishes.add(DishOBJ);

                    }
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postModel;
    }


    //Parse Model - Restaurants (Favourite fragment)
    public static ArrayList<RestaurantModel> parseRestaurantArray(String murl){
        ArrayList<RestaurantModel> postModel = new ArrayList<RestaurantModel>();
        if(murl != null)
        {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("restaurants");
                JSONObject posOBJ = null;



                if (postArray != null) {
                    for (int i = 0; i < postArray.length(); i++) {
                        RestaurantModel postOBJ = new RestaurantModel();
                        posOBJ = postArray.getJSONObject(i);


                        postOBJ.id            = posOBJ.getString("id");
                        postOBJ.name          = posOBJ.getString("name");
                        postOBJ.address       = posOBJ.getString("address");
                        postOBJ.latitude      = posOBJ.getString("latitude");
                        postOBJ.longitude     = posOBJ.getString("longitude");

                        postOBJ.phone_number  = Generalfunction.Isnull(posOBJ.getString("phone_number"));
                        postOBJ.favourited_by = posOBJ.getBoolean("favourited_by");
                        postOBJ.followed_by   = posOBJ.getBoolean("followed_by");
                        postOBJ.rating        = Generalfunction.IsnullRating(posOBJ.getString("rating"));

                        postModel.add(postOBJ);
                    }
                }

            } catch (JSONException e) {}
        }

        return postModel;
    }
    public static RestaurantModel parseDishModel1(String murl){
        RestaurantModel postOBJ = new RestaurantModel();
        if(murl != null)
        {
            try {

                postOBJ.un_rated=new JSONObject(murl).getString("un_rated_dishes");


            } catch (JSONException e) {}
        }

        return postOBJ;
    }



    //Parse Model - Dishes (Favourite fragment)
    public static ArrayList<DishModel> parseDishArray(String murl,Context mcontext,String strfilterdata){
        ArrayList<DishModel> postModel = new ArrayList<DishModel>();
        if(murl != null)
        {
            try {
                JSONArray dishesArray = new JSONObject(murl).getJSONArray("dishes");

                JSONObject posOBJ = null;
                JSONObject jsonObject=new JSONObject(murl);


                if(Generalfunction.isEmptyCheck(Generalfunction.Isnull(strfilterdata))) {
                    Generalfunction.SaveFilterMin_MaxPrice(mcontext, jsonObject.getInt("min_price"), jsonObject.getInt("max_price"));
                }

                if (dishesArray != null) {
                    for (int j = 0; j < dishesArray.length(); j++) {
                       DishModel DishOBJ = new DishModel();
                        JSONObject object = dishesArray.getJSONObject(j);

                        DishOBJ.id          = object.getString("id");
                        DishOBJ.name        = object.getString("name");
                        DishOBJ.rest_id     = object.getString("restaurant_id");
                        DishOBJ.description = object.getString("description");
                        try{
                        DishOBJ.votes=object.getString("votes");}catch(Exception e){}

                        String price=Generalfunction.Isnull(object.getString("price"));
                        if(Generalfunction.isEmptyCheck(price)){
                            price="0";
                        }
                        DishOBJ.price = price;

                        DishOBJ.favourited_by  = object.getBoolean("favourited_by");
                        DishOBJ.average_rating = Generalfunction.IsnullRating(object.getString("average_rating"));

                        //Restaurant details
                        try {
                            JSONObject ObjRest = object.getJSONObject("restaurant");

                            DishOBJ.rest_id           = ObjRest.getString("id");
                            DishOBJ.rest_name         = ObjRest.getString("name");
                            DishOBJ.rest_address      = ObjRest.getString("address");
                            DishOBJ.rest_latitude     = Generalfunction.Isnull(ObjRest.getString("latitude"));
                            DishOBJ.rest_longitude    = Generalfunction.Isnull(ObjRest.getString("longitude"));
                            DishOBJ.rest_phone_number = Generalfunction.Isnull(ObjRest.getString("phone_number"));
                            DishOBJ.distance          = "0";//Generalfunction.CalculateDistance(Generalfunction.Isnull(ObjRest.getString("latitude")),Generalfunction.Isnull(ObjRest.getString("longitude")),mcontext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            //image array
                            JSONArray dishImageArray = object.getJSONArray("images");
                            if (dishImageArray != null) {
                                for (int k = 0; k < dishImageArray.length(); k++) {
                                    JSONObject object_dishImage = dishImageArray.getJSONObject(k);

                                    DishOBJ.image     = object_dishImage.getString("image_url");
                                    DishImageArray dish_image = new DishImageArray();
                                    dish_image.user_id        = object_dishImage.getString("id");
                                    dish_image.dish_image_url = object_dishImage.getString("image_url");
                                    DishOBJ.dishImagelist.add(dish_image);
                                }
                            }
                        }catch(Exception e){}
                        postModel.add(DishOBJ);
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
               // e.printStackTrace();
            }
        }
        return postModel;
    }




   //Parse Model - User profile
    public static UserprofileModel parseUserprofileModel(String murl,Context mcontext,String strFilterdata){

        UserprofileModel postOBJ = new UserprofileModel();
        int num=1;
        String s= String.valueOf(num);

        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl);

                postOBJ.id            = posOBJ.getString("id");
                postOBJ.name          = posOBJ.getString("name");
                postOBJ.username      = posOBJ.getString("username");
                postOBJ.cover_image   = posOBJ.getString("cover_image");
                postOBJ.profile_image = posOBJ.getString("profile_image");
                postOBJ.un_rated_dishes=posOBJ.getString("un_rated_dishes");

                if(Generalfunction.isEmptyCheck(Generalfunction.Isnull(strFilterdata))) {
                    Generalfunction.SaveFilterMin_MaxPrice(mcontext, posOBJ.getInt("min_price"), posOBJ.getInt("max_price"));
                }

                postOBJ.followers_count    = posOBJ.getString("followers");
                try {
                  postOBJ.followings_count = posOBJ.getString("followings");
                } catch(Exception e){}

                try{
                    postOBJ.followed_by = posOBJ.getBoolean("follows");
                }catch(Exception e){}


                //User Dishes
                JSONArray dishesArray = posOBJ.getJSONArray("dishes");

                if(dishesArray != null) {
                    for (int j = 0; j < dishesArray.length(); j++) {
                        DishModel DishOBJ = new DishModel();
                        JSONObject object = dishesArray.getJSONObject(j);

                        DishOBJ.id   = object.getString("id");
                        DishOBJ.name = Generalfunction.Isnull(object.getString("name"));

                        DishOBJ.average_rating = Generalfunction.IsnullRating(object.getString("average_rating"));
                        String rating  = Generalfunction.IsnullRating(object.getString("rating"));

                        DishOBJ.rating         = rating ;
                        DishOBJ.favourited_by  = object.getBoolean("favourite");
                        DishOBJ.rest_name      = Generalfunction.Isnull(object.getString("restaurant_name"));

                        String price=Generalfunction.Isnull(object.getString("price"));
                        if(Generalfunction.isEmptyCheck(price)){
                            price="0";
                        }

                        DishOBJ.price = price;
                        String str    = Generalfunction.IsnullRating(object.getString("rating"));

                        //Re-rate
                        boolean flagrrerate = object.getBoolean("reposted");
                        Log.e("rating",""+flagrrerate);
                        DishOBJ.IsReposted = flagrrerate;

//                        if (Generalfunction.isCompare(str,"0")) {
//                            DishOBJ.IsRatingGiven = false;     // IT display Rate this dish image
//                        }

                        if(Double.parseDouble(str)<1){
                            DishOBJ.IsRatingGiven = false;
                        }

                        else {
                            if(flagrrerate){
                                DishOBJ.IsRatingGiven = false;
                            }
                            else{
                                DishOBJ.IsRatingGiven = true;
                            }
                        }

                        //Restaurant detail of dish
                        JSONObject posOBJRest = object.getJSONObject("restaurant");
                        DishOBJ.rest_id       = posOBJRest.getString("id");
                        DishOBJ.rest_name     = posOBJRest.getString("name");
                        DishOBJ.rest_address  = posOBJRest.getString("address");

                        DishOBJ.rest_phone_number = Generalfunction.Isnull(posOBJRest.getString("phone_number"));
                        DishOBJ.rest_latitude     = posOBJRest.getString("latitude");
                        DishOBJ.rest_longitude    = posOBJRest.getString("longitude");
                        DishOBJ.distance          = Generalfunction.CalculateDistance(Generalfunction.Isnull(posOBJRest.getString("latitude")),Generalfunction.Isnull(posOBJRest.getString("longitude")),mcontext);

                        //Tag array
                        JSONArray dishTagArray=object.getJSONArray("tags");
                        if(dishTagArray != null) {
                            for (int l = 0; l < dishTagArray.length(); l++) {
                                String strTag=dishTagArray.getString(l).toString();
                                DishOBJ.dishTaglist.add(strTag);
                            }
                        }

                        try {
                            try {
                                DishOBJ.DishComment = Generalfunction.Isnull(object.getString("comment"));
                            }catch(Exception e){}

                            //image array
                            JSONObject dishImageObject = object.getJSONObject("image");
                            if(dishImageObject != null){
                            DishOBJ.image   = dishImageObject.getString("image_url");
                            DishOBJ.imageId = dishImageObject.getString("id");
                            }

                        }catch(Exception e){ }

                        postOBJ.dishes.add(DishOBJ);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return postOBJ;
    }


    //Parse Model - Restaurant fragment (Follow Activity)
    public static RestaurantModel parseRestaurantFollowers(String murl){
        RestaurantModel postModel = new RestaurantModel();
        if(murl != null)
        {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("restaurants");
                JSONObject posOBJ = null;

                if(postArray != null) {
                    for (int i = 0; i < postArray.length(); i++) {

                        posOBJ = postArray.getJSONObject(i);

                        FollowerModel postOBJ = new FollowerModel();

                        postOBJ.Rest_id        = posOBJ.getString("id");
                        postOBJ.Rest_name      = posOBJ.getString("name");
                        postOBJ.Rest_address   = posOBJ.getString("address");
                        postOBJ.Rest_latitude  = posOBJ.getString("latitude");
                        postOBJ.Rest_longitude = posOBJ.getString("longitude");

                        postOBJ.Rest_phone_no       = Generalfunction.Isnull(posOBJ.getString("phone_number"));
                        postOBJ.isRest_follow       = posOBJ.getBoolean("followed_by");
                        postOBJ.Rest_rating         = Generalfunction.IsnullRating(posOBJ.getString("rating"));
                        postOBJ.Rest_followerscount = posOBJ.getString("followers_count");

                        postModel.Followlist.add(postOBJ);
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postModel;
    }

    //Parse Model - people follow fragment (Follow Activity)
    public static ArrayList<FollowerModel> parseUserFollow(String murl) {

        UserprofileModel postModel = new UserprofileModel();
        ArrayList<FollowerModel> Followlist = new ArrayList<FollowerModel>();

        if (murl != null) {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("users");
                JSONObject posOBJ = null;

                if(postArray != null) {
                    for (int i = 0; i < postArray.length(); i++) {

                        posOBJ = postArray.getJSONObject(i);
                        FollowerModel postOBJ = new FollowerModel();

                        postOBJ.id             = posOBJ.getString("id");
                        postOBJ.username       = posOBJ.getString("username");
                        postOBJ.firstname      = posOBJ.getString("first_name");
                        postOBJ.lastname       = posOBJ.getString("last_name");
                        postOBJ.email          = posOBJ.getString("email");
                        postOBJ.isfollow       = posOBJ.getBoolean("follows");
                        postOBJ.isfollowing    = posOBJ.getBoolean("following");
                        postOBJ.profileimage   = posOBJ.getString("profile_image");
                        postOBJ.followersCount = posOBJ.getString("followers");

                        postModel.Followlist.add(postOBJ);
                        Followlist.add(postOBJ);
                    }
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return Followlist;
    }


    //Parse Model - people follow fragment for other user profile (Follow Activity)
    public static UserprofileModel parseUserFollow_Otheruser(String murl) {
        UserprofileModel postModel = new UserprofileModel();
        if (murl != null) {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("followers");
                JSONObject posOBJ = null;

                if(postArray != null) {

                    for (int i = 0; i < postArray.length(); i++) {

                        posOBJ = postArray.getJSONObject(i);
                        FollowerModel postOBJ = new FollowerModel();

                        postOBJ.id = posOBJ.getString("id");
                        postOBJ.username = posOBJ.getString("username");
                        postOBJ.firstname = posOBJ.getString("first_name");
                        postOBJ.lastname = posOBJ.getString("last_name");
                        postOBJ.email = posOBJ.getString("email");
                        postOBJ.profileimage = posOBJ.getString("profile_image");
                        postOBJ.followersCount = posOBJ.getString("followers");
                        postOBJ.isfollow = posOBJ.getBoolean("follows");

                        postModel.Followlist.add(postOBJ);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return postModel;
    }


    public static String CheckToken(String Response,Context context){
        try {
            JSONObject jsonObject = new JSONObject(Response);
            if (Response.toLowerCase().contains("error")) {
                String strTokenexpire = context.getResources().getString(R.string.tokendenied);
                if (Generalfunction.isCompare(jsonObject.getString("error"),strTokenexpire)) {
                    Response = strTokenexpire;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return Response;
    }

}
