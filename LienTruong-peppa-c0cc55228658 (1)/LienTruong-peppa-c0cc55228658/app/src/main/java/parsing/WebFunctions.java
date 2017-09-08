package parsing;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import utility.Constant;
import utility.Generalfunction;

/*import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;*/
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by Mumtaz Hassan on 6/19/2016.
 */


//        [7/7/2016 2:28:29 AM] Bilal Maqsood: https://peppa.herokuapp.com/restaurants/ POST ——> for creating a restaurant
//        [7/7/2016 2:28:51 AM] Bilal Maqsood: https://peppa.herokuapp.com/restaurants/1 PUT ——> For updating a restaurant
//        [7/7/2016 2:29:09 AM] Bilal Maqsood: https://peppa.herokuapp.com/restaurants/1 DELETE —> For deleting a restaurant
//        [7/7/2016 2:29:29 AM] Bilal Maqsood: https://peppa.herokuapp.com/restaurants/1 GET —> For showing a restaurant

public class WebFunctions {

    public static  Context context;
    static String TAG="Webfunction";

    public WebFunctions(Context context){
        this.context = context.getApplicationContext();
    }

/* ************************************   Create Account ********************************************** */

    // Register account with simple
    public static String registerUser(String fname, String lname, String username, String email, String password){
        String url = Constant.signUpURL;
        String urlParameters = "user[first_name]=" + fname + "&user[last_name]=" + lname + "&user[username]=" + username +
                "&user[email]=" + email + "&user[password]=" + password;
        if (!Constant.isProductionBuild) {
            Log.d("Sign Up url", url);
        }
        String result = getPostResponse(url, urlParameters);
        Log.d("result", result);

        return result;
    }

    // Register account with facebook
    public static String registerUserProvider(String fname, String lname, String email, String provider_id){
        String url = Constant.providerSignURL;
        String urlParameters = "first_name=" + fname + "&last_name=" + lname + "&email=" + email
                + "&uid=" + provider_id + "&provider=facebook";
        if (!Constant.isProductionBuild) {
            Log.d("Faceboook Sign Up url", url);
        }
        String result = getPostResponse(url, urlParameters);
        Log.d("result", result);

        return result;
    }

    // Reset password
    public static String resetPassword(String email){
        String url = Constant.resetPassURL;
        String urlParameters = "email=" + email;
        if (!Constant.isProductionBuild) {
            Log.d("Reset url", url);
        }
        String result = getPostResponse(url,urlParameters);
        Log.d("result", result);
        JSONObject jsonObject;
        String response = "";
        try {
            jsonObject = new JSONObject(result);
            response = jsonObject.getString("message");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    // Login with simple
    public static String logInUser(String username,  String password){
        String url = Constant.signInURL;
        String urlParameters = "user[username]=" + username + "&user[password]=" + password;
        if (!Constant.isProductionBuild) {
        }
            Log.d("Log In url", url);
        String result = getPostResponse(url,urlParameters);
        Log.d("result", result);

        return result;
    }

    // login with facebook
    public static String logInUserFacebook(String provider_id){
        String url = Constant.providerSignURL;
        String urlParameters = "uid=" + provider_id + "&provider=facebook";
        if (!Constant.isProductionBuild) {
            Log.d("Facebook Log In url", url);
        }
        String result = getPostResponse(url, urlParameters);
        Log.d("result", result);

        return result;
    }

/* *************  get login user detail which is call in Navigation ***********  */

    // get user
    public static String getUser(String token){
        String url = Constant.getuserURL;
        if (!Constant.isProductionBuild) {
            Log.d("User url", url);
        }
        return getAuthResponse(url, token);
    }

/* *************** search Result for Restaurant and dishes **************************************/

    public static String getRestaurant(String id,  String token){
        String url = Constant.globalRestURL + id;
        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }

    public static String getSearchResult(String search, String sort, String direction, String token){
        String url = Constant.globalURL + "search?search="  + search/* + "&sort=" + sort + "&direction=" + direction*/;
        if (!Constant.isProductionBuild) {
            Log.d("Search url", url);
        }
        return getAuthResponse(url, token);
    }

 /* ******************** Restaurant profile ************************************************/

    public static String getRestaurantprofile(String id,  String token){

        String url=Constant.globalRestURL+id;
        // String url = Constant.globalRestURL + id;
        Log.d(TAG, "getuser profile: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }


    // Follow or Unfollow and Favourite and unfavourite Restaurant
    public static String follow_Favourite_Restaurant(String id, String access_token, String type){
        String url = Constant.globalRestURL + id + "/" + type;
        String urlParameters = "";
        if (!Constant.isProductionBuild) {
            Log.d("Log In url", url);
        }
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Log.d("result", result);
        String Message= "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if(jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                }
                else{
                    Message=jsonObject.getString("message");
                }
            }
            else{
                Message=jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }

 /********************  quick post which beacome as a dish of restaurant ******************** */

     /*get resturant name (Login user profile And Other user profile)*/
     public static String getRestaurantlist(String search,  String token){

          String url = Constant.globalRestURL + "search_by_name?search="+search;
         Log.d(TAG, "get Restaurant list: ");
         if (!Constant.isProductionBuild) {
             Log.d("Restaurant url", url);
         }

         String result=getAuthResponse(url, token);

         JSONObject jsonObject;
         Log.d("result", result);
         String Message= "";
         try {
             jsonObject = new JSONObject(result);
             Message=jsonObject.getString("message");

             if(!Message.toLowerCase().contains("sorry")){
                 Message=result;
             }

         } catch (JSONException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }

         return Message;
     }

    public static String getRestaurantDishlist(String search,  String token){

        String url = Constant.globalDishURL + "search_by_name?search="+search;
        Log.d(TAG, "get Restaurant list: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }

        String result=getAuthResponse(url, token);

        JSONObject jsonObject;
        Log.d("result", result);
        String Message= "";
        try {
            jsonObject = new JSONObject(result);
            Message=jsonObject.getString("message");

            if(Message.toLowerCase().contains("sorry")){
                Message = Message;
            }
            else{
                Message=result;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }

    public static String Post(String access_token, String restaurantid, String dishname, String dishprice,String dishDescription, String Photourl){

        Log.d(TAG, "Post: token: "+access_token+" id: "+restaurantid+" dishname: "+dishname+" price: "+dishprice+" Description: "+dishDescription+ " photo: "+Photourl);

        String url = Constant.globalDishpostURL;
        String urlParameters = "dish[name]=" + dishname + "&dish[price]=" + dishprice + "&dish[description]=" + dishDescription +
                "&dish[restaurant_id]=" + restaurantid ;

        if (!Constant.isProductionBuild) {
            Log.d("Sign Up url", url);
        }
        String result =getPostAuthResponse(url, urlParameters,access_token);
        Log.d("result", result);

        return result;

    }

  /* **************************** favorite or unfavorite dish ************************** */

    public static String FavouriteDish(String id, String access_token, String type){
        String url = Constant.globalDishURL + id + "/" + type;
        String urlParameters = "";
        if (!Constant.isProductionBuild) {
            Log.d("Log In url", url);
        }
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Log.d("result", result);
        String Message= "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if(jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                }
                else{
                    Message=jsonObject.getString("message");
                }
            }
            else{
                Message=jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }

 /***************** update user detail ***********************************************/

    public static String updateregisterUser(String accesstoken,String id, String first_name, String last_name, String username, String email){
        String url = Constant.globalURL+"users/"+id;
        String urlParameters = "user[first_name]=" + first_name + "&user[last_name]=" + last_name + "&user[username]=" + username +
                "&user[email]=" + email ;
        if (!Constant.isProductionBuild) {
        }
        Log.d("Log In url", url);
        String result = getPutAuthResponse(url,urlParameters,accesstoken);
        Log.d("result", result);
        JSONObject jsonObject;
        Log.d("result", result);
        String Message= "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if(jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                }
                else{
                    Message=jsonObject.getString("message");
                }
            }
            else{
                Message=jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Message;
    }

    public static String updateregisterPwd(String accesstoken, String oldpwd, String newpwd){
        String url = Constant.globalURL+"users/update_password";

        String urlParameters = "user[current_password]=" + oldpwd + "&user[password]=" + newpwd  ;
        if (!Constant.isProductionBuild) {
        }
        Log.d("Log In url", url);
        String result = getPutAuthResponse(url,urlParameters,accesstoken);
        Log.d("result", result);
        JSONObject jsonObject;
        Log.d("result", result);
        String Message= "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if(jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                }
                else if(jsonObject.getString("success").equalsIgnoreCase("No")) {
                    JSONArray arry=jsonObject.getJSONArray("errors");
                    for(int i=0;i<arry.length(); i ++ ) {
                        Message=arry.getString(i);
                        Log.d(TAG, "updateregisterPwd: "+Message);
                    }
                    if(Generalfunction.isEmptyCheck(Message)){
                        Message=jsonObject.getString("message");
                    }
                }
                else{
                    Message=jsonObject.getString("message");
                }
            }
            else{
                Message=jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Message;
    }


    // rate dish
    public static Boolean rateDish(String dish_id, String weight, String access_token,String comment){
        String url = Constant.globalDishURL + dish_id + "/ratings/";
        String urlParameters = "rating[weight]=" + weight+ "&comment="+comment;

        if (!Constant.isProductionBuild) {
            Log.d("Rate Dish url", url);
        }
        Log.d(TAG, "rateDish:Token: "+access_token);
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Log.d("result", result);
        Boolean response = false;
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                response = true;
            } else {
                response = false;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    //
    public static Boolean FollowRest(String id, String access_token, String type){
        String url = Constant.globalRestURL + id + "/" + type;
        String urlParameters = "";
        if (!Constant.isProductionBuild) {
            Log.d("Log In url", url);
        }
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Log.d("result", result);
        Boolean response = false;
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                response = true;
            } else {
                response = false;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }





    public static String getDish(String id,  String token){
        String url = Constant.globalDishURL + id;
        if (!Constant.isProductionBuild) {
            Log.d("Dish url", url);
        }
        return getAuthResponse(url, token);
    }

    public static String getFollowers_Favourites(String token, String type){
        String url = Constant.globalUserURL + type;
        if (!Constant.isProductionBuild) {
            Log.d("Favourite Rest url", url);
        }
        //followers
        return getAuthResponse(url, token);
    }

    public static String getFavouritesDishes(String token){
        String url = Constant.globalUserURL + "favourited_dishes";
        if (!Constant.isProductionBuild) {
            Log.d("Favourite Dishes url", url);
        }
        //followers
        return getAuthResponse(url, token);
    }

    public static String getFollowers(String token, String id){
        String url = Constant.globalRestURL + id + "/followers";
        if (!Constant.isProductionBuild) {
            Log.d("Follower url", url);
        }
        //followers
        return getAuthResponse(url, token);
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    /*Atharva system */

    /*get user profile (Login user profile And Other user profile)*/
    public static String getUserprofile(String id,  String token){

        String url="profile";
        if(Generalfunction.isEmptyCheck(id)){
            url = Constant.globalUserURL+url;
        }
        else{
            url=Constant.globalUserURL+id+"/"+url;
        }
        // String url = Constant.globalRestURL + id;
        Log.d(TAG, "getuser profile: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }


    // Favourite dish
    public static String Favourite_UnfavouriteDish(String id, String access_token, String type){
        String url = Constant.globalDishURL + id + "/" + type;
        String urlParameters = "";
        if (!Constant.isProductionBuild) {
            Log.d("Log In url", url);
        }
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Log.d("result", result);
        return result;
    }

    public static String getRestaurantlist( String token){
        String url = Constant.globalRestsURL ;
        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }

    public static String getUserlist( String token){
        String url = Constant.globalUsersURL ;
        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }


    public static String getFollw(String id,  String token,String type){

        String url=Constant.globalUserURL+id+"/"+type;

        // String url = Constant.globalRestURL + id;
        Log.d(TAG, "getuser profile: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }


    public static String FollowUser(String id, String access_token, String type){
        String url = Constant.globalUserURL + id + "/" + type;
        String urlParameters = "";
        if (!Constant.isProductionBuild) {
            Log.d("Log In url", url);
        }
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Log.d("result", result);
        String Message= "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if(jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                }
                else{
                    Message=jsonObject.getString("message");
                }
            }
            else{
                Message=jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }


    /* --------------------------    Response Functions ------------------------------ */



    @TargetApi(19)
    public static String getPostResponse(String urlString, String urlParameters){
        Log.d("params", urlParameters);
        String result = "ERROR";
        //urlParameters = "users[first_name]=faiza&users[last_name]=anwar&users[username]=pepp&users[email]=tests@test.test&users[password]=1234asdf";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        URL url;
        HttpURLConnection conn = null;

        try {
            urlString = urlString.replaceAll(" ", "%20");
            url = new URL( urlString );
            conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try(
                    DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                    wr.write( postData );
            }
            Log.d("Message - Code", conn.getResponseMessage() + " - " + conn.getResponseCode());
            InputStream in;
            if (conn.getResponseCode() == 200) {
                in = conn.getInputStream();
                result = convertStreamToString(in);
                Log.d("Result",result);
            } else {
                in = conn.getErrorStream();
                result = convertStreamToString(in);
                Log.d("Result",result);
                //result = "error";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "error";
            Log.d("Exception", "malformgetPostResponse: "+e);
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
            Log.d("Exception", "IOexceptionResponse: "+e);
        }

        return result;
    }

    @TargetApi(19)
    public static String getPostAuthResponse(String urlString, String urlParameters, String accessToken){
        Log.d("params", urlParameters);
        String  result = "error";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        URL url;
        HttpURLConnection conn = null;

        try {
            urlString = urlString.replaceAll(" ", "%20");
            url = new URL( urlString );
            conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setRequestProperty("Authorization", "Token token=" + accessToken);
            conn.setUseCaches(false);
            try(
                    DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write( postData );
            }
            Log.d("Message - Code", conn.getResponseMessage() + " - " + conn.getResponseCode());
            InputStream in;
            if (conn.getResponseCode() == 200) {
                in = conn.getInputStream();
                result = convertStreamToString(in);
                Log.d("Result",result);
            } else {
                in = conn.getErrorStream();
                result = convertStreamToString(in);
                Log.d("Result",result);
                //result = "error";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "error";
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
        }

        return result;
    }

    public static String getAuthResponse(String urlString, String accessToken) {
        String result = "ERROR";
        Log.d("accessToken", accessToken);
        URL url;
        HttpURLConnection httpUrlConnection = null;

        try {
            urlString = urlString.replaceAll(" ", "%20");
            url = new URL(urlString);
            Log.d(TAG, "Url : "+url);
            Log.d(TAG, "AccessToken: "+accessToken);

            httpUrlConnection = (HttpURLConnection) url.openConnection();

            //httpUrlConnection.setUseCaches(false);
            //httpUrlConnection.setRequestProperty("User-Agent", "MyAgent");
            httpUrlConnection.setConnectTimeout(30000);
            httpUrlConnection.setReadTimeout(30000);

            String authorization="token:" + accessToken;
            byte[] data = authorization.getBytes("UTF-8");
            String encodedAuth="Basic "+ Base64.encodeToString(data, Base64.DEFAULT);

            //httpUrlConnection.addRequestProperty("AUTHORIZATION", encodedAuth);
            //httpUrlConnection.addRequestProperty("Content-Type", "application/json");
            httpUrlConnection.setRequestProperty("Authorization", "Token token=" + accessToken);
            httpUrlConnection.setRequestMethod("GET");


            httpUrlConnection.connect();

            InputStream in;
            //Log.d("Header" , httpUrlConnection.getHeaderFields() + "  -  " +  httpUrlConnection.getResponseCode() + "");
            if (httpUrlConnection.getResponseCode() == 200) {
                in = httpUrlConnection.getInputStream();
                result = convertStreamToString(in);
                Log.d("Result",result);
            } else {
                in = httpUrlConnection.getErrorStream();
                result = convertStreamToString(in);
                Log.d("Result",result);
                result = "error";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "error";
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
        }

        // return JSON
        return result;

    }

    @TargetApi(19)
    public static String getPutAuthResponse(String urlString, String urlParameters, String accessToken){
        Log.d("params", urlParameters);
        String  result = "error";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        URL url;
        HttpURLConnection conn = null;

        try {
            urlString = urlString.replaceAll(" ", "%20");
            url = new URL( urlString );
            conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setRequestProperty("Authorization", "Token token=" + accessToken);
            conn.setUseCaches(false);
            try(
                    DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write( postData );
            }
            Log.d("Message - Code", conn.getResponseMessage() + " - " + conn.getResponseCode());
            InputStream in;
            if (conn.getResponseCode() == 200) {
                in = conn.getInputStream();
                result = convertStreamToString(in);
                Log.d("Result",result);
            } else {
                in = conn.getErrorStream();
                result = convertStreamToString(in);
                Log.d("Result",result);
                result = "error";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "error";
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
        }

        return result;
    }


}
