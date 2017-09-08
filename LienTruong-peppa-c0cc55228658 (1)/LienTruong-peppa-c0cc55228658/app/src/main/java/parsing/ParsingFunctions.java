package parsing;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.DishCommentModel;
import model.DishModel;
import model.FollowerMModel;
import model.FollowerModel;
import model.RestaurantModel;
import model.SearchModel;
import model.UserModel;
import model.UserprofileModel;
import utility.Generalfunction;

public class ParsingFunctions {

    public static UserModel parseUserModel(String murl,Context context){
        UserModel postOBJ = new UserModel();
        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl).getJSONObject("user");

                postOBJ.id = posOBJ.getString("id");
                postOBJ.first_name = posOBJ.getString("first_name");
                postOBJ.last_name  = posOBJ.getString("last_name");
                postOBJ.email = posOBJ.getString("email");
                postOBJ.username = posOBJ.getString("username");

                Generalfunction.SaveUserDetail(context,posOBJ.getString("first_name"),posOBJ.getString("last_name"),posOBJ.getString("username"),posOBJ.getString("id"),
                        posOBJ.getString("email"));


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //Log.d("Count", postOBJ.dishes.size() + "");
        return postOBJ;
    }

/* **************************** Restaurant Profile **************************************** */

    public static RestaurantModel parseRestaurantModel(String murl){
        RestaurantModel postOBJ = new RestaurantModel();
        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl).getJSONObject("restaurant");

                postOBJ.id = posOBJ.getString("id");
                postOBJ.name = posOBJ.getString("name");
                postOBJ.address  = posOBJ.getString("address");
                postOBJ.latitude = posOBJ.getString("latitude");
                postOBJ.longitude = posOBJ.getString("longitude");
                postOBJ.phone_number = posOBJ.getString("phone_number");
                postOBJ.favourited_by = posOBJ.getBoolean("favourited_by");
                postOBJ.followed_by = posOBJ.getBoolean("followed_by");
                postOBJ.followers_count = posOBJ.getString("followers_count");
                postOBJ.rating = posOBJ.getString("rating");
                postOBJ.votes=posOBJ.getString("votes");
                JSONArray dishesArray = posOBJ.getJSONArray("dishes");

                for (int j = 0; j < dishesArray.length(); j++) {
                    DishModel DishOBJ = new DishModel();
                    JSONObject object = dishesArray.getJSONObject(j);

                    DishOBJ.id = object.getString("id");
                    DishOBJ.rest_id = object.getString("restaurant_id");
                    DishOBJ.name = object.getString("name");
                    DishOBJ.description = object.getString("description");
                    DishOBJ.price = object.getString("price");
                    DishOBJ.favourited_by = object.getBoolean("favourited_by");
                    DishOBJ.average_rating  = object.getString("average_rating");
                    DishOBJ.rest_phone_number = posOBJ.getString("phone_number");
                    DishOBJ.rest_latitude = posOBJ.getString("latitude");
                    DishOBJ.rest_longitude = posOBJ.getString("longitude");
                    DishOBJ.rest_name = posOBJ.getString("name");
                    DishOBJ.rest_address  = posOBJ.getString("address");

                    postOBJ.dishes.add(DishOBJ);

                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //Log.d("Count", postOBJ.dishes.size() + "");
        return postOBJ;
    }

/* **************************** Dish Detail **************************************** */

    public static DishModel parseDishModel(String murl){
        DishModel postOBJ = new DishModel();
        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl).getJSONObject("dish");

                //Dish Detail
                postOBJ.id = posOBJ.getString("id");
                postOBJ.name = posOBJ.getString("name");
                postOBJ.rest_id  = posOBJ.getString("restaurant_id");
                postOBJ.description = posOBJ.getString("description");
                postOBJ.price = posOBJ.getString("price");
                postOBJ.rest_name = posOBJ.getString("restaurant_name");
                postOBJ.favourited_by = posOBJ.getBoolean("favourited_by");
                postOBJ.average_rating = posOBJ.getString("average_rating");
                postOBJ.votes=posOBJ.getString("votes");

                //restaurant detail of dish
                JSONObject posOBJRest = posOBJ.getJSONObject("restaurant");
                postOBJ.rest_id=posOBJRest.getString("id");
                postOBJ.rest_name=posOBJRest.getString("name");
                postOBJ.rest_address=posOBJRest.getString("address");
                postOBJ.rest_phone_number=posOBJRest.getString("phone_number");
                postOBJ.rest_latitude=posOBJRest.getString("latitude");
                postOBJ.rest_longitude=posOBJRest.getString("longitude");

                //comment list for dish
                JSONArray dishCommentsArray = posOBJ.getJSONArray("comments");

                for (int j = 0; j < dishCommentsArray.length(); j++) {

                    DishCommentModel DishOBJ = new DishCommentModel();
                    JSONObject object = dishCommentsArray.getJSONObject(j);

                    DishOBJ.userComment=object.getString("comment");

                    //user detail which give comment
                    JSONObject objUser=object.getJSONObject("user");

                    DishOBJ.userId=objUser.getString("id");
                    DishOBJ.userpProfileImage=objUser.getString("profile_image");
                    DishOBJ.user_Name=objUser.getString("name");
                    DishOBJ.userRatingforDish=objUser.getString("rating");
                    DishOBJ.favourited_by=objUser.getBoolean("favourite");

                    postOBJ.dishCommentlist.add(DishOBJ);


                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //Log.d("Count", postOBJ.dishes.size() + "");
        return postOBJ;
    }

    public static ArrayList<FollowerMModel> parseFollowModel(String murl){
        ArrayList<FollowerMModel> postModel = new ArrayList<FollowerMModel>();
        if(murl != null)
        {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("followers");
                JSONObject posOBJ = null;

                for (int i=0; i<postArray.length(); i ++ ) {

                    posOBJ = postArray.getJSONObject(i);
                    FollowerMModel postOBJ = new FollowerMModel();

                    //postOBJ.followers = posOBJ.getString("id");
                    postOBJ.name = posOBJ.getString("username");

                    postModel.add(postOBJ);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postModel;
    }




    public static SearchModel parseSearchOBJ(String murl){
        SearchModel postModel = new SearchModel();
        if(murl != null)
        {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("restaurants");
                JSONArray dishesArray = new JSONObject(murl).getJSONArray("dishes");
                JSONObject posOBJ = null;

                for (int i=0; i<postArray.length(); i ++ ) {

                    posOBJ = postArray.getJSONObject(i);
                    RestaurantModel postOBJ = new RestaurantModel();

                    postOBJ.id = posOBJ.getString("id");
                    postOBJ.name = posOBJ.getString("name");
                    postOBJ.address  = posOBJ.getString("address");
                    postOBJ.latitude = posOBJ.getString("latitude");
                    postOBJ.longitude = posOBJ.getString("longitude");
                    postOBJ.phone_number = posOBJ.getString("phone_number");
                    postOBJ.favourited_by = posOBJ.getBoolean("favourited_by");
                    postOBJ.followed_by = posOBJ.getBoolean("followed_by");
                    postOBJ.rating = posOBJ.getString("rating");

                    postModel.restaurants.add(postOBJ);
                }

                for (int j = 0; j < dishesArray.length(); j++) {
                    DishModel DishOBJ = new DishModel();
                    JSONObject object = dishesArray.getJSONObject(j);

                    DishOBJ.id = object.getString("id");
                    DishOBJ.name = object.getString("name");
                    DishOBJ.description = object.getString("description");
                    DishOBJ.price = object.getString("price");
                    DishOBJ.favourited_by = object.getBoolean("favourited_by");
                    DishOBJ.average_rating  = object.getString("average_rating");
                    DishOBJ.votes=object.getString("votes");

                    //Restaurant details
                    JSONObject ObjRest=object.getJSONObject("restaurant");

                    DishOBJ.rest_id = ObjRest.getString("id");
                    DishOBJ.rest_name = ObjRest.getString("name");
                    DishOBJ.rest_address = ObjRest.getString("address");
                    DishOBJ.rest_latitude = ObjRest.getString("latitude");
                    DishOBJ.rest_longitude = ObjRest.getString("longitude");
                    DishOBJ.rest_phone_number = ObjRest.getString("phone_number");

                    postModel.dishes.add(DishOBJ);

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postModel;
    }

    public static ArrayList<RestaurantModel> parseRestaurantArray(String murl){
        ArrayList<RestaurantModel> postModel = new ArrayList<RestaurantModel>();
        if(murl != null)
        {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("restaurants");
                JSONObject posOBJ = null;

                for (int i=0; i<postArray.length(); i ++ ) {

                    posOBJ = postArray.getJSONObject(i);
                    RestaurantModel postOBJ = new RestaurantModel();

                    postOBJ.id = posOBJ.getString("id");
                    postOBJ.name = posOBJ.getString("name");
                    postOBJ.address  = posOBJ.getString("address");
                    postOBJ.latitude = posOBJ.getString("latitude");
                    postOBJ.longitude = posOBJ.getString("longitude");
                    postOBJ.phone_number = posOBJ.getString("phone_number");
                    postOBJ.favourited_by = posOBJ.getBoolean("favourited_by");
                    postOBJ.followed_by = posOBJ.getBoolean("followed_by");
                    postOBJ.rating = posOBJ.getString("rating");

                    postModel.add(postOBJ);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postModel;
    }

    public static ArrayList<DishModel> parseDishArray(String murl){
        ArrayList<DishModel> postModel = new ArrayList<DishModel>();
        if(murl != null)
        {
            try {
                JSONArray dishesArray = new JSONObject(murl).getJSONArray("dishes");
                JSONObject posOBJ = null;

                for (int j = 0; j < dishesArray.length(); j++) {
                    DishModel DishOBJ = new DishModel();
                    JSONObject object = dishesArray.getJSONObject(j);

                    DishOBJ.id = object.getString("id");
                    DishOBJ.name = object.getString("name");
                    DishOBJ.rest_id = object.getString("restaurant_id");
                    DishOBJ.description = object.getString("description");
                    DishOBJ.price = object.getString("price");
                    DishOBJ.favourited_by = object.getBoolean("favourited_by");
                    DishOBJ.average_rating  = object.getString("average_rating");

                    //Restaurant details
                    try {
                        JSONObject ObjRest = object.getJSONObject("restaurant");

                        DishOBJ.rest_id = ObjRest.getString("id");
                        DishOBJ.rest_name = ObjRest.getString("name");
                        DishOBJ.rest_address = ObjRest.getString("address");
                        DishOBJ.rest_latitude = ObjRest.getString("latitude");
                        DishOBJ.rest_longitude = ObjRest.getString("longitude");
                        DishOBJ.rest_phone_number = ObjRest.getString("phone_number");
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    /*DishOBJ.phone_number = posOBJ.getString("phone_number");
                    DishOBJ.latitude = posOBJ.getString("latitude");
                    DishOBJ.longitude = posOBJ.getString("longitude");
                    DishOBJ.restaurant_name = posOBJ.getString("name");
                    DishOBJ.address  = posOBJ.getString("address");*/

                    postModel.add(DishOBJ);

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postModel;
    }


    public static UserprofileModel parseUserprofileModel(String murl){

        UserprofileModel postOBJ = new UserprofileModel();

        if(murl != null)
        {
            try {
                JSONObject posOBJ = new JSONObject(murl);

                postOBJ.id = posOBJ.getString("id");
                postOBJ.name = posOBJ.getString("name");
                postOBJ.username = posOBJ.getString("username");
                postOBJ.cover_image = posOBJ.getString("cover_image");
                postOBJ.profile_image = posOBJ.getString("profile_image");

                postOBJ.followers_count = posOBJ.getString("followers");

                try {
                    postOBJ.followings_count = posOBJ.getString("followings");
                }
                catch(Exception e){}

                try{
                    postOBJ.followed_by = posOBJ.getBoolean("follows");
                }catch(Exception e){}

                JSONArray dishesArray = posOBJ.getJSONArray("dishes");

                for (int j = 0; j < dishesArray.length(); j++) {
                    DishModel DishOBJ = new DishModel();
                    JSONObject object = dishesArray.getJSONObject(j);

                    DishOBJ.id = object.getString("id");
                    DishOBJ.name = object.getString("name");
                    DishOBJ.image = object.getString("image");
                    DishOBJ.average_rating=object.getString("average_rating");
                    DishOBJ.rating = object.getString("rating");
                    DishOBJ.favourited_by=object.getBoolean("favourite");
                    DishOBJ.rest_name = object.getString("restaurant_name");
                    DishOBJ.price = object.getString("price");
                    DishOBJ.image=object.getString("image");

                    String str=object.getString("rating");

                    if(str == null || str == "" || str=="null"){
                        DishOBJ.IsRatingGiven=false;
                    }
                    else{
                        DishOBJ.IsRatingGiven=true;
                    }

                    postOBJ.dishes.add(DishOBJ);

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.d("JsonException", "parseUserprofileModel: "+e.getMessage());
                e.printStackTrace();
            }
        }
        Log.d("Count", postOBJ.dishes.size() + "");
        return postOBJ;
    }

    public static SearchModel parseRestaurantFollowers(String murl){
        SearchModel postModel = new SearchModel();
        if(murl != null)
        {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("restaurants");
                JSONObject posOBJ = null;

                for (int i=0; i<postArray.length(); i ++ ) {

                    posOBJ = postArray.getJSONObject(i);
                    RestaurantModel postOBJ = new RestaurantModel();

                    postOBJ.id = posOBJ.getString("id");
                    postOBJ.name = posOBJ.getString("name");
                    postOBJ.address  = posOBJ.getString("address");
                    postOBJ.latitude = posOBJ.getString("latitude");
                    postOBJ.longitude = posOBJ.getString("longitude");
                    postOBJ.phone_number = posOBJ.getString("phone_number");
                    postOBJ.favourited_by = posOBJ.getBoolean("favourited_by");
                    postOBJ.followed_by = posOBJ.getBoolean("followed_by");
                    postOBJ.rating = posOBJ.getString("rating");
                    postOBJ.followers_count=posOBJ.getString("followers_count");

                    postModel.restaurants.add(postOBJ);
                }



            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postModel;
    }

    public static UserprofileModel parseUserFollow(String murl) {
        UserprofileModel postModel = new UserprofileModel();
        if (murl != null) {
            try {
                JSONArray postArray = new JSONObject(murl).getJSONArray("users");
                JSONObject posOBJ = null;

                for (int i = 0; i < postArray.length(); i++) {

                    posOBJ = postArray.getJSONObject(i);
                    FollowerModel postOBJ = new FollowerModel();

                    postOBJ.id = posOBJ.getString("id");
                    postOBJ.username = posOBJ.getString("username");
                    postOBJ.firstname = posOBJ.getString("first_name");
                    postOBJ.lastname = posOBJ.getString("last_name");
                    postOBJ.email = posOBJ.getString("email");
                    postOBJ.isfollow = posOBJ.getBoolean("follows");

                    postModel.Followlist.add(postOBJ);
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postModel;
    }


}
