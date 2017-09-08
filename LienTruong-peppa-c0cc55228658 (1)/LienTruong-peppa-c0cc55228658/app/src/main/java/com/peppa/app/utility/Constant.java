package com.peppa.app.utility;

import android.os.Environment;

import java.io.File;

public class Constant {

    public static boolean isProductionBuild = true;
    public static int ImageSize=1024*2 ;

    //Map server api key
    public static String google_map_server_API_KEY="AIzaSyDtNKXx-dRazTn4yxkl7aKNeCd6L6RweRk" ;     // use in google places autocomplete
    //Map server api key
    //Google Map apis
    private static final String googleMapApiBaseUrl = "https://maps.googleapis.com/maps/api";
    //Restricted to australia only
    public static final String googleMapAutocompleteUrl = googleMapApiBaseUrl + "/place/autocomplete/json?components=country:au&key=" + google_map_server_API_KEY;
    public static final String googleMapPlaceDetailsUrl = googleMapApiBaseUrl + "/place/details/json?key=" + google_map_server_API_KEY;
    public static final String googleMapGeocodeUrl = googleMapApiBaseUrl + "/geocode/json";
//Call API URL

    // Published url
//     public static String globalURL = "https://peppa.herokuapp.com/api/v1/";

    //Local
    public static String globalURL = "http://203.193.173.114:4000/api/v1/";  // A

    public static String globalDishURL = globalURL + "dishes/";
    public static String globalDishpostURL = globalURL + "dishes";
    public static String globalRestURL = globalURL + "restaurants/";
    public static String globalRestsURL = globalURL + "restaurants";
    public static String globalUserURL = globalURL + "users/";
    public static String globalUsersURL = globalURL + "users";

    public static String signUpURL = globalURL + "signup";
    public static String signInURL = globalURL + "signin";
    public static String providerSignURL = globalURL + "authentications";
    public static String resetPassURL = globalURL + "forget_password";
    public static String getuserURL = globalUserURL + "show";
    public static String getdelURL = globalUserURL + "delete_dish_from_profile";
    public static String globalDishURL1 = globalURL + "users/rating_view_image";

    public static String globalemailURL1 = globalURL + "resend_activation_email";
    //Facebook post API
    public static String Peppa_Website="http://www.peppa.io/";
    public static String getuserURL1 = globalUserURL + "users/home_page";


// intent constant

    //search and searchresult
    public static String Search="search";
    public static String isMyprofile="isMyprofile";
    public static String OtheruserId="OtheruserId";

    public static String ID="ID";
    public static String SerchLocation="Location";


// Action constant

    //Display log
    public static String TAG="Peppa";

    //Display image with picasso
    public static int case1 = 1;
    public static int case2 = 2;

    //Update image
    public static int case_imageupload_profile = 8;
    public static int case_imageupload_cover = 9;

    //Save image - sometime save image in this path
    public static final String Camerapath_Image=Environment.getExternalStorageDirectory() + File.separator + "FoodApp" + File.separator;

    // instagram: share image only possible with file path so we save bitmap to file
    public static final String Filepath_Image =  Environment.getExternalStorageDirectory().getAbsolutePath() +"/Peppa";


//Display - Placeholder image
    public static String Ph_restaurant_coverimage="Ph_restaurant_coverimage";
    public static String Ph_user_coverimage="Ph_user_coverimage";
    public static String Ph_userprofilepic="Ph_userprofilepic";
    public static String Ph_dish="Ph_dish";


//Take photo constant
  public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

// Android M permission constant

    public static final int PERMISSION_REQUEST_CODE_Camera = 1;
    public static final int PERMISSION_ALL = 2;
    public static final int PERMISSION_REQUEST_CODE_Location = 3;
    public static final int CallPERMISSION_REQUEST_CODE = 4;
    public static final int PERMISSION_REQUEST_CODE_STORAGE = 5;

    public static final String CAMERA    = "Camera";
    public static final String LOCATION  = "Location";
    public static final String PHONE     = "Phone";
    public static final String STORAGE   = "Storage";


// Shared preference constant


    //user detail
    public static String loginUserfirstName = "loginUserfirstName";
    public static String loginUserlastName = "loginUserlastName";
    public static String loginUserName = "loginUserName";
    public static String loginUserID = "loginUserID";
    public static String loginUserEmail = "loginUserEmail";
    public static String loginUserProfilePhoto = "loginUserProfilePhoto";
    public static String loginUserCoverPhoto = "loginUserCoverPhoto";

    //Login user detail for check access token
    public static String loginUserpwd="loginUserpwd";

    //Location
    public static String CurrentLatitude="CurrentLatitude";
    public static String CurrentLongitude="CurrentLongitude";

    //Post activity
    public static String Isquickpost="Isquickpost";
    public static String Quick_RestId="Quick_RestId";
    public static String Quick_RestName="Quick_RestName";
    public static String Quick_DishName="Quick_DishName";
    public static String Quick_DishPrice="Quick_DishPrice";
    public static String Quick_DishID="Quick_DishID";
    public static String IsPost_Nextdish="IsPost_Nextdish";
    public static String Quick_RestAddress="Quick_RestAddress";
    public static String Quick_Dishimage="Quick_Dishimage";

    //Result/Favourite
    public static String Location_Mapview="Location_Mapview";
    public static String Location_Search="Location_Search";

    //Followers/Following
    public static String SelectedID_for_Follow="SelectedID_for_Follow";       // My profile="", for Other user it take some int number (it used to find User profile and other user profile)
    public static String IsFollow_User_Following="IsFollow_User_Following";
    public static String IsFollow_followers="IsFollow_followers";
    public static String IsResturant_Userprofile="IsResturant_Userprofile"; //true then Restaurant and false then user profile

    //Screen refresh
    public static String IsScreenRefresh="IsScreenRefresh";

    // Filter
    public static String Filter_sortby="Filter_sortby";
    public static String Filter_isshowfavorite="Filter_isshowfavorite";
    public static String Filter_priceMax="Filter_priceMax";
    public static String Filter_priceMin="Filter_priceMin";
    public static String Filter_Rating="Filter_Rating";
    public static String Filter_isTag_entry="Filter_isTag_entry";
    public static String Filter_isTag_main="Filter_isTag_main";
    public static String Filter_isTag_dessert="Filter_isTag_dessert";
    public static String Filter_isTag_drink="Filter_isTag_drink";
    public static String Filter_isTag_vegeterian="Filter_isTag_vegeterian";
    public static String Filter_isTag_vegen="Filter_isTag_vegen";
    public static String Filter_isTag_gluten="Filter_isTag_gluten";

    public static String Filter_selecteddone="Filter_selecteddone";
    public static String Filter_selecteddone1="Filter_selecteddone1";
    public static String Filter_selectedDistance="Filter_selectedDistance";
    public static String Filter_Done="Filter_Done";

    public static String Filter_MaxPrice="Filter_MaxPrice";
    public static String Filter_MinPrice="Filter_MinPrice";

    //search
    public static String Search_Latitude="Search_Latitude";
    public static String Search_Longitude="Search_Longitude";

    //Follow
    public static String Filter_FollowRefresh="Filter_FollowRefresh";   //following and following refresh when user follow restaurant/user

    //Post and Quick post
    public static String Post_PreviousScreen="Post_PreviousScreen";

    //Screen name
    public static String Screen_RP="Screen_RP";
    public static String Screen_UP="Screen_UP";
    public static String Screen_result="Screen_result";
    public static String Search_location_with_search="Search_location_with_search";
    public static String Screen_Previous="Screen_Previous";
    public static String Screen_DishDetail="Screen_DishDetail";

    //login facebook
    public static String Login_facebook="login_with_facebook";
    public static String Login_facebook_uid="Login_facebook_uid";

    //use for delete images from phone related app
    public static String bitmapToimage="bitmapToimage";

    //Post dish then true it halp's to update ui after quick post
    public static String IsPost_Dish="IsPost_Dish";

    public static String Filter_FavouriteRefresh="Filter_FavouriteRefresh";   //Favorite
    public static String Peppa_RestID="Peppa_RestID";

   /* My user profile refresh after update details of setting */

    //It help' sin this senario -> user update detail from Myuserprofile - setting - updated details - Back on my user profile
    public static String Setting_Refresh="Setting_Refresh";   //only for my user profile

    //user update detail from Seeting - Myuserprofile - setting - updated detils - Back on my user profile - back
    public static String Settingfragment_Refresh="Settingfragment_Refresh";  //only for setting fragment


//Pagination limit

    //Pagination API veriable
    public static String page="page=";
    public static String limit="limit=";

    public static int Datalist_limit = 15;

    //Result
    public static int Result_limit = Datalist_limit;
    //Restaurant profile
    public static int RP_dish_limit =Datalist_limit;
    //Following- Restaurant profile
    public static int Following_load_limit = Datalist_limit;
    //Following user
    public static int Following_User_load_limit = Datalist_limit;  //Datalist_limit;
    //Followers- people
    public static int Followers_load_limit = Datalist_limit;
    //Favorite
    public static int Favorite_limit = Datalist_limit;
    //Dish detail comments
    public static int Dish_Comment_limit = Datalist_limit;

// intent constant
    //Followers - people - tital change
    public static String Followers_Name="Followers_Name";

 }
