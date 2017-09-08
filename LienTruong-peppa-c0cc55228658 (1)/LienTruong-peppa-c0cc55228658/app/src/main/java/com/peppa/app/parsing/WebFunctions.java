package com.peppa.app.parsing;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.Log;

import com.android.internal.http.multipart.MultipartEntity;
import com.peppa.app.model.AddressModel;
import com.peppa.app.model.PlacePredictionModel;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

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
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by Mumtaz Hassan on 6/19/2016.
 * //Edit/update by Rup Barad
 */


public class WebFunctions {

    public static Context context;
    static String TAG = "Webfunction";

    public WebFunctions(Context context) {
        this.context = context.getApplicationContext();
    }


 /* --------------------------    Response Functions start ------------------------------ */

    //Get - POST Method Response without token
    @TargetApi(19)
    public static String getPostResponse(String urlString, String urlParameters) {
        Log.d("params", urlParameters);
        String result = "ERROR";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        URL url;
        HttpURLConnection conn = null;

        try {
            urlString = urlString.replaceAll(" ", "%20");
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try (
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
            Log.d("Message - Code", conn.getResponseMessage() + " - " + conn.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(conn));

            Log.d("Result", result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "error";
            Log.d("Exception", "malformgetPostResponse: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
            Log.d("Exception", "IOexceptionResponse: " + e);
        }

        return result;
    }


    //Get - POST Response without token
    @TargetApi(19)
    public static String getPostAuthResponse(String urlString, String urlParameters, String accessToken) {
        Log.d("params", urlParameters);
        String result = "error";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        URL url;
        HttpURLConnection conn = null;

        try {
            urlString = urlString.replaceAll(" ", "%20");
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setRequestProperty("Authorization", "Token token=" + accessToken);
            conn.setUseCaches(false);
            try (
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
            Generalfunction.DisplayLog(conn.getResponseMessage() + " - " + conn.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(conn));

            Generalfunction.DisplayLog(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "error";
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
        }
        return result;
    }


    //Get - GET Method Response with token
    public static String getAuthResponse(String urlString, String accessToken) {
        String result = "ERROR";
        Log.d("accessToken", accessToken);
        URL url;
        HttpURLConnection httpUrlConnection = null;

        try {
            urlString = urlString.replaceAll(" ", "%20");
            url = new URL(urlString);
            Log.d(TAG, "Url : " + url);
            Log.d(TAG, "AccessToken: " + accessToken);

            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(30000);
            httpUrlConnection.setReadTimeout(30000);

            httpUrlConnection.setRequestProperty("Authorization", "Token token=" + accessToken);
            httpUrlConnection.setRequestMethod("GET");

            httpUrlConnection.connect();

            Generalfunction.DisplayLog(httpUrlConnection.getResponseMessage() + " - " + httpUrlConnection.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(httpUrlConnection));

            Generalfunction.DisplayLog(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "error";
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
        }

        Log.d(TAG, "getAuthResponse: " + result);

        return result;

    }


    //Get - PUT Method Response with token
    @TargetApi(19)
    public static String getPutAuthResponse(String urlString, String urlParameters, String accessToken) {
        Log.d("params", urlParameters);
        String result = "error";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        URL url;
        HttpURLConnection conn = null;

        try {
            urlString = urlString.replaceAll(" ", "%20");
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setRequestProperty("Authorization", "Token token=" + accessToken);
            conn.setUseCaches(false);
            try (
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            Generalfunction.DisplayLog(conn.getResponseMessage() + " - " + conn.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(conn));

            Generalfunction.DisplayLog(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "error";
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
        }

        return result;
    }


    //Call API - Post Dish
    public static String PostDish_Image(String access_token, String restaurantid, String dishname, String dishprice, String dishid, String restaurantName, String images, String Address, String PhoneNo) {

        String Uploadurl = Constant.globalDishpostURL;
        String Requestmode = "POST";

        String result = "error";
        String image_content_type = "image/jpeg";
        JSONObject jsonObject;
        JSONObject Response;
        try {
            jsonObject = new JSONObject();

            //Dish Detail
            JSONObject jsonDish = new JSONObject();
            jsonDish.put("name", dishname);
            jsonDish.put("price", dishprice);
            if (!Generalfunction.isEmptyCheck(restaurantid)) {
                jsonDish.put("restaurant_id", restaurantid);
            }

            jsonObject.put("dish", jsonDish);
            if (!Generalfunction.isEmptyCheck(restaurantName)) {
                jsonObject.put("restaurant_name", restaurantName);
            }

            if (!Generalfunction.isEmptyCheck(Address)) {
                jsonObject.put("address", Address);
            } else {
                if (!Generalfunction.isEmptyCheck(dishid)) {
                    jsonObject.put("dish_id", dishid);
                }
            }
            if (!Generalfunction.isEmptyCheck(PhoneNo)) {
                jsonObject.put("phone_number", PhoneNo);
            }

            if (!Generalfunction.isEmptyCheck(images)) {
                jsonObject.put("image_content_type", image_content_type);
                jsonObject.put("image", images);
            }

            String data = jsonObject.toString();

            Log.d(TAG, "PostDish_Image: " + data);

            URL url = new URL(Uploadurl);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod(Requestmode);

            connection.setFixedLengthStreamingMode(data.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Authorization", "Token token=" + access_token);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);

            Log.d("Vicky", "PostDish_Image :Data to php = " + data);

            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            Generalfunction.DisplayLog(connection.getResponseMessage() + " - " + connection.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(connection));

            Generalfunction.DisplayLog(result);

            //Response = new JSONObject(result);
            connection.disconnect();

        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    //Call API - Upload user photo (profile photo/cover photo)
    public static String PostUserImage(String access_token, String id, String images, String type) {

        String result = "error";
        String Uploadurl = Constant.globalDishURL + id + "/images";   // id as a Dish id
        String Requestmode = "POST";


        if (type.equalsIgnoreCase(String.valueOf(Constant.case_imageupload_cover))) {
            Uploadurl = Constant.globalUserURL + id + "/images/cover";
            Requestmode = "PUT";
        }
        if (type.equalsIgnoreCase(String.valueOf(Constant.case_imageupload_profile))) {
            Uploadurl = Constant.globalUserURL + id + "/images/profile";
            Requestmode = "PUT";
        }
        JSONObject jsonObject;
        JSONObject Response;
        String image_content_type = "image/jpeg";
        try {
            jsonObject = new JSONObject();
            jsonObject.put("image_content_type", image_content_type);
            jsonObject.put("image", images);
            String data = jsonObject.toString();

            URL url = new URL(Uploadurl);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod(Requestmode);

            connection.setFixedLengthStreamingMode(data.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Authorization", "Token token=" + access_token);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);

            Log.d(Constant.page, "Data to php = " + data);

            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            Generalfunction.DisplayLog(connection.getResponseMessage() + " - " + connection.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(connection));

            Generalfunction.DisplayLog("Response from php = " + result);
            /*InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            result = sb.toString();*/
            connection.disconnect();
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }



    /*
    webfunction method of response
     */


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


    private static InputStream HttpConnectionResultInputstream(HttpURLConnection httpConn) {
        InputStream _is = null;
        try {
            if (httpConn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                _is = httpConn.getInputStream();
            } else {
                /* error from server */
                _is = httpConn.getErrorStream();
            }
        } catch (Exception e) {
        }

        return _is;
    }

    /* --------------------------    Response Functions End ------------------------------ */


    /* ----------------------------- Call API start -------------------------------------- */

    /*
     Create Account related
     */

    // Register account with simple
    public static String registerUser(String fname, String lname, String username, String email, String password) {
        String url = Constant.signUpURL;
        String result = null;
        JSONObject jsonObject2;
        String Requestmode = "POST";

        try {
            jsonObject2 = new JSONObject();




            jsonObject2.put("first_name", fname);
            jsonObject2.put("last_name", lname);
            jsonObject2.put("username", username);
            jsonObject2.put("email", email);
            jsonObject2.put("password", password);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("user",jsonObject2);

            String data3 = jsonObject1.toString();



            URL url1 = new URL(url);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url1.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod(Requestmode);

            connection.setFixedLengthStreamingMode(data3.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//
//            connection.setRequestProperty("Authorization", "Token token=" + access_token);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data3);

            Log.d(Constant.page, "Data to php = " + data3);

            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            Generalfunction.DisplayLog(connection.getResponseMessage() + " - " + connection.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(connection));

            Generalfunction.DisplayLog("Response from php = " + result);

            connection.disconnect();
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

//        String urlParameters = "user[first_name]=" + fname + "&user[last_name]=" + lname + "&user[username]=" + username +
//                "&user[email]=" + email + "&user[password]=" + password;
//        if (!Constant.isProductionBuild) {
//            Log.d("Sign Up url", url);
//        }
//        String result = getPostResponse(url, urlParameters);
//        Log.d("result", result);
//
//        return result;
   // }

    // Register account with facebook
    public static String registerUserProvider(String fname, String lname, String email, String provider_id, Context context) {
        String url = Constant.providerSignURL;
        String urlParameters = "first_name=" + fname + "&last_name=" + lname + "&email=" + email
                + "&uid=" + provider_id + "&provider=facebook";
        if (!Constant.isProductionBuild) {
            Log.d("Faceboook Sign Up url", url);
        }

        if (!Generalfunction.isEmptyCheck(provider_id)) {
            GlobalVar.setMyStringPref(context, Constant.Login_facebook_uid, provider_id);
        }

        String result = getPostResponse(url, urlParameters);
        Log.d("result", result);

        return result;
    }

    // Reset password
    public static String resetPassword(String email) {
        String url = Constant.resetPassURL;
        String urlParameters = "email=" + email;
        if (!Constant.isProductionBuild) {
            Log.d("Reset url", url);
        }
        String result = getPostResponse(url, urlParameters);
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
    public static String logInUser(String username, String password) {
        String url = Constant.signInURL;
        String result = null;
        JSONObject jsonObject2;
        String Requestmode = "POST";
        try {
            jsonObject2 = new JSONObject();





            jsonObject2.put("username", username);

            jsonObject2.put("password", password);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("user",jsonObject2);

            String data3 = jsonObject1.toString();



            URL url1 = new URL(url);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url1.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod(Requestmode);

            connection.setFixedLengthStreamingMode(data3.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//
//            connection.setRequestProperty("Authorization", "Token token=" + access_token);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data3);

            Log.d(Constant.page, "Data to php = " + data3);

            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            Generalfunction.DisplayLog(connection.getResponseMessage() + " - " + connection.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(connection));

            Generalfunction.DisplayLog("Response from php = " + result);

            connection.disconnect();
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
//        String urlParameters = "user[username]=" + username + "&user[password]=" + password;
//        if (!Constant.isProductionBuild) {
//        }
//        Log.d("Log In url", url);
//        String result = getPostResponse(url, urlParameters);
//        Log.d("result", result);
//
//        return result;
    }

    // login with facebook
    public static String logInUserFacebook(String provider_id) {
        String url = Constant.providerSignURL;
        String urlParameters = "uid=" + provider_id + "&provider=facebook";
        if (!Constant.isProductionBuild) {
            Log.d("Facebook Log In url", url);
        }
        String result = getPostResponse(url, urlParameters);
        Log.d("result", result);

        return result;
    }


    // get user (get login user detail which is call in Navigation)
    public static String getUser(String token) {
        String url = Constant.getuserURL;
        if (!Constant.isProductionBuild) {
            Log.d("User url", url);
        }
        return getAuthResponse(url, token);
    }

    public static String getUser1(String token) {
        String url = Constant.getuserURL1;
        if (!Constant.isProductionBuild) {
            Log.d("User url", url);
        }
        return getAuthResponse(url, token);
    }
    /*
    search fragment
     */

    //Call API - Get location
    public static String getLocationlist(String search, String token) {

        String url = Constant.globalRestURL + "suburbs?keyword=" + search;
        Log.d(TAG, "get Restaurant list: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }

        String result = getAuthResponse(url, token);

        return result;
    }


    /*
    Result activity related
     */


    //Call API - Search Result for Restaurant and dishes using search
    public static String getSearchResult(String search, String strFilterData, String token, Context context, String location, int dish_page) {

        String url = ""; // Constant.globalURL + "search?search="  + search;//+"&latitude="+ latitude+ "&longitude="+longitude;
        url = Constant.globalURL + "search?location=" + location + "&search=" + search;

        if (!Generalfunction.isEmptyCheck(strFilterData)) {
            url = url + "&" + strFilterData;
        }

        //pagination
        if (!Generalfunction.isEmptyCheck(String.valueOf(dish_page))) {
            url = url + "&" + Constant.page + dish_page + "&" + Constant.limit + Constant.Result_limit;
        }

        if (!Constant.isProductionBuild) {
            Log.d("Search url", url);
        }
        return getAuthResponse(url, token);
    }

    /*
    Restaurant profile
     */

    //Call API - Get Restaurant Profile
    public static String getRestaurantprofile(String id, String token, String strFilterData, int dish_page) {

        String url = Constant.globalRestURL + id;

        //pagination
        if (!Generalfunction.isEmptyCheck(String.valueOf(dish_page))) {
            url = url + "?" + Constant.page + dish_page + "&" + Constant.limit + Constant.RP_dish_limit;
        }

        if (!Generalfunction.isEmptyCheck(strFilterData)) {
            url = url + "&" + strFilterData;
        }

        //Log.d(TAG, "getuser profile: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }

        return getAuthResponse(url, token);
    }


    /*
     Restaurant profile and restaurant fragment
     */


    //Call API - Follow or Unfollow and Favourite and unfavourite Restaurant
    public static String follow_Favourite_Restaurant(String id, String access_token, String type) {
        String url = Constant.globalRestURL + id + "/" + type;
        String urlParameters = "";
        if (!Constant.isProductionBuild) {
            Log.d("Log In url", url);
        }
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Log.d("result", result);
        String Message = "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if (jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                } else {
                    Message = jsonObject.getString("message");
                }
            } else {
                Message = jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }


    /*
    post (quick post) as a dish of restaurant
     */


    //Call API - get resturant name
    public static String getRestaurantlist(String search, String token) {

        String url = Constant.globalRestURL + "search_by_name?search=" + search;
        Log.d(TAG, "get Restaurant list: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }

        String result = getAuthResponse(url, token);

        JSONObject jsonObject;
        Log.d("result", result);
        String Message = "";
        try {
            jsonObject = new JSONObject(result);
            Message = jsonObject.getString("message");

            if (!Message.toLowerCase().contains("sorry")) {
                Message = result;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }

    //Call API - get Dish for restaurant
    public static String getRestaurantDishlist(String search, String token) {
        String url = Constant.globalDishURL + "search_by_name?search=" + search;
        Log.d(TAG, "get Restaurant list: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }

        String result = getAuthResponse(url, token);

        JSONObject jsonObject;
        Log.d("result", result);
        String Message = "";
        try {
            jsonObject = new JSONObject(result);
            Message = jsonObject.getString("message");

            if (Message.toLowerCase().contains("sorry")) {
                Message = Message;
            } else {
                Message = result;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Message;
    }

    //Call API - get Dish for restaurant
    public static String getRestaurantDishlist(String search, String token, String strRestId) {

        String url = Constant.globalDishURL + "search_by_name?search=" + search + "&restaurant_name=" + strRestId;
        Log.d(TAG, "get Restaurant list: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }

        String result = getAuthResponse(url, token);

        JSONObject jsonObject;
        Log.d("result", result);
        String Message = "";
        try {
            jsonObject = new JSONObject(result);
            Message = jsonObject.getString("message");

            if (Message.toLowerCase().contains("sorry")) {
                Message = Message;
            } else {
                Message = result;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }
    

    /*
    Dish Detail
     */

    //Call API - Get Dish detail
    public static String getDish(String id, String token, int pageNo,String other,String view) {
        String url = null;
        if(view!=null) {
            if (other != null) {
                url = Constant.globalDishURL + id + "?" + Constant.page + pageNo + "&" + Constant.limit + Constant.Dish_Comment_limit + "&user_id=" + other + "&view=" + view;
            } else {
                url = Constant.globalDishURL + id + "?" + Constant.page + pageNo + "&" + Constant.limit + Constant.Dish_Comment_limit+ "&view=" + view;
            }
        }
        if (!Constant.isProductionBuild) {
            Generalfunction.DisplayLog("Dish url" + url);
        }
        return getAuthResponse(url, token);
    }
    public static String getDish1(String id, String token) {
        String url = null;


            url = Constant.globalDishURL1 +"?" +"id="+ id ;


        if (!Constant.isProductionBuild) {
            Generalfunction.DisplayLog("Dish url" + url);
        }
        return getAuthResponse(url, token);
    }

    //Call API - Favorite/Un-favorite dish
    public static String FavouriteDish(String id, String access_token, String type) {
        String url = Constant.globalDishURL + id + "/" + type;
        String urlParameters = "";
        if (!Constant.isProductionBuild) {
            Generalfunction.DisplayLog("Log In url" + url);
        }
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Generalfunction.DisplayLog("result " + result);

        String Message = "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if (jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                } else {
                    Message = jsonObject.getString("message");
                }
            } else {
                Message = jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }


    /*
    Update user detail by
    setting activity
     setting fragment
     */


    //Call API - Update user information
    public static String updateregisterUser(String accesstoken, String id, String first_name, String last_name, String username, String email) {
        String url = Constant.globalURL + "users/" + id;
        String result = null;
        JSONObject jsonObject2;
        String Requestmode = "PUT";
        try {
            jsonObject2 = new JSONObject();

            jsonObject2.put("first_name", first_name);

            jsonObject2.put("last_name", last_name);
            jsonObject2.put("username",username);
            jsonObject2.put("email",email);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("user",jsonObject2);

            String data3 = jsonObject1.toString();



            URL url1 = new URL(url);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url1.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod(Requestmode);

            connection.setFixedLengthStreamingMode(data3.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//
            connection.setRequestProperty("Authorization", "Token token=" + accesstoken);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data3);

            Log.d(Constant.page, "Data to php = " + data3);

            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            Generalfunction.DisplayLog(connection.getResponseMessage() + " - " + connection.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(connection));

            Generalfunction.DisplayLog("Response from php = " + result);

            connection.disconnect();
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        String urlParameters = "user[first_name]=" + first_name.trim() + "&user[last_name]=" + last_name.trim() + "&user[username]=" + username.trim() +
//                "&user[email]=" + email.trim();
//        if (!Constant.isProductionBuild) {
//        }
//        Log.d("Log In url", url);
//        String result = getPutAuthResponse(url, urlParameters, accesstoken);
//        Log.d("result", result);
//        JSONObject jsonObject;
//        Log.d("result", result);
//        String Message = "";
//        try {
//            jsonObject = new JSONObject(result);
//            if (jsonObject.has("success")) {
//                if (jsonObject.getString("success").equalsIgnoreCase("Yes")) {
//                    Message = "true";
//                } else {
//                    Message = jsonObject.getString("message");
//                }
//            } else {
//                Message = jsonObject.getString("message");
//            }
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return result;
    }

    public static String VerifiedUser(String token) {
        String url = Constant.globalURL + "is_active_user";

        //followers
        return getAuthResponse(url, token);
    }






    //Call API - Update user register password
    public static String updateregisterPwd(String accesstoken, String oldpwd, String newpwd) {
        String url = Constant.globalURL + "users/update_password";

        String urlParameters = "user[current_password]=" + oldpwd.trim() + "&user[password]=" + newpwd.trim();
        if (!Constant.isProductionBuild) {
        }
        Log.d("Log In url", url);
        String result = getPutAuthResponse(url, urlParameters, accesstoken);
        Log.d("result", result);
        JSONObject jsonObject;
        Log.d("result", result);
        String Message = "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if (jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                } else if (jsonObject.getString("success").equalsIgnoreCase("No")) {
                    JSONArray arry = jsonObject.getJSONArray("errors");
                    for (int i = 0; i < arry.length(); i++) {
                        Message = arry.getString(i);
                        Log.d(TAG, "updateregisterPwd: " + Message);
                    }
                    if (Generalfunction.isEmptyCheck(Message)) {
                        Message = jsonObject.getString("message");
                    }
                } else {
                    Message = jsonObject.getString("message");
                }
            } else {
                Message = jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /*
     Rate
     */

    //Call API - Rate Image update
    public static String RateDish_Image(String dish_id, String weight, String access_token, String comment, ArrayList<String> Taglist, String dish_name, String dish_price, String images, String image_id) {

        String Uploadurl = Constant.globalDishURL + dish_id.trim() + "/ratings/";
        String Requestmode = "POST";

        String result = "error";
        String image_content_type = "image/jpeg";
        JSONObject jsonObject;
        JSONObject Response;

        try {
            jsonObject = new JSONObject();

            jsonObject.put("comment", comment.trim());

            JSONArray jsonArray = new JSONArray();
            if (Taglist.size() > 0) {
                for (int i = 0; i < Taglist.size(); i++) {
                    jsonArray.put(Taglist.get(i).toString().trim());
                }
            }

            jsonObject.put("tags", jsonArray);

            //Rating
            JSONObject jsonRating = new JSONObject(); // we need another object to store the address

            // We add the object to the main object
            jsonRating.put("weight", weight.trim());
            jsonObject.put("rating", jsonRating);

            //Dish Detail
            JSONObject jsonDish = new JSONObject();
            boolean flag = false;
            if (!Generalfunction.isEmptyCheck(dish_name)) {
                jsonDish.put("name", dish_name);
                flag = true;
            }
            if (!Generalfunction.isEmptyCheck(dish_price)) {
                jsonDish.put("price", dish_price);
                flag = true;
            }

            if (flag) {
                jsonObject.put("dish", jsonDish);
            }

            if (!Generalfunction.isEmptyCheck(images)) {
                jsonObject.put("image_content_type", image_content_type);
                jsonObject.put("image_id", image_id);
                jsonObject.put("image", images);
            }

            String data = jsonObject.toString();

            Log.d(TAG, "RateDish_Image: URL " + Uploadurl);
            Log.d(TAG, "RateDish_Image: " + data);

            URL url = new URL(Uploadurl);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod(Requestmode);

            connection.setFixedLengthStreamingMode(data.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Authorization", "Token token=" + access_token);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);


            Log.d("Vicky", "PostDish_Image :Data to php = " + data);
            //Generalfunction.DisplayLog(result);
            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            result = sb.toString();
            Log.d("Vicky", "PostDish_Image : Response from php = " + result);
            //Response = new JSONObject(result);
            connection.disconnect();

        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }


    /*
    Favorite fragment
     */

    //Call API - Get favourite restaurant API
    public static String getFavouritesRestaurants(String token, String type, int dish_page) {
        String url = Constant.globalUserURL + type.trim() + "?" + Constant.page + dish_page + "&" + Constant.limit + Constant.Favorite_limit;

        if (!Constant.isProductionBuild) {
            Log.d("Favourite Rest url", url);
        }
        //followers
        return getAuthResponse(url, token);
    }


    //Call API - Get favourite Dishes API
    public static String getFavouritesDishes(String token, boolean Isfilter, String strFilterData, int dish_page) {

        String url = Constant.globalUserURL + "favourited_dishes" + "?" + Constant.page + dish_page + "&" + Constant.limit + Constant.Favorite_limit;

        if (Isfilter) {
            url = url + "&" + strFilterData; //url = Constant.globalUserURL + "favourited_dishes?"+strFilterData;
        }

        if (!Constant.isProductionBuild) {
            Log.d("Favourite Dishes url", url);
        }
        //followers
        return getAuthResponse(url, token);
    }


    /*
    User profile
     */

    //Call API - Get User profile
    public static String getUserprofile(String id, String token, String strFilterData) {

        String url = "profile";

            if (Generalfunction.isEmptyCheck(id)) {
                if(strFilterData.equals("null")) {
                url = Constant.globalUserURL + url ;     //Login user profile as a my user profile
            }
                else {
                    url = Constant.globalUserURL + url+"?"+strFilterData;
                }
        }

        else {
                if(strFilterData.equals("null")) {
                    url = Constant.globalUserURL + id + "/" + url ;     //Login user profile as a my user profile
                }
                else {
                    url = Constant.globalUserURL + id + "/" + url + "?" + strFilterData;
                }
                //Other user profile
        }

        Log.d(TAG, "getuser profile: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }



    /*
      Restaurant fragment (all restaurant list)
     */

    //Call API - Get Restaurant list
    public static String getRestaurantlist(String token, int current_page) {

        String url = Constant.globalRestsURL;

        url = url + "?" + Constant.page + current_page +
                "&" + Constant.limit + Constant.Following_load_limit;

        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }

    //Call API - Get User list
    public static String getSearchRestaurantlist(String token, int current_page, String strSearchword) {
        String url = Constant.globalRestURL + Constant.Search;

        url = url + "?" + Constant.Search + "=" + strSearchword + "&" + Constant.page + current_page +
                "&" + Constant.limit + Constant.Following_User_load_limit;

        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("recycle Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }

    /*
    people follow fragment
     */

    //Call API - Get User list
    public static String getUserlist(String token, int current_page) {
        String url = Constant.globalUsersURL;

        url = url + "?" + Constant.page + current_page +
                "&" + Constant.limit + Constant.Following_User_load_limit;

        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("recycle Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }


    //Call API - Get User list
    public static String getSearchUserlist(String token, int current_page, String strSearchword) {
        String url = Constant.globalUserURL + Constant.Search;

        url = url + "?" + Constant.Search + "=" + strSearchword + "&" + Constant.page + current_page +
                "&" + Constant.limit + Constant.Following_User_load_limit;

        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("recycle Restaurant url", url);
        }
        return getAuthResponse(url, token);
    }

    //Call API - Get User followers
    public static String getUserlist_otheruserprofile(String token, String userid, Context thisContext, int current_page) {

        String url = Constant.globalUserURL + userid.trim() + "/followers";

        url = url + "?" + Constant.page + current_page + "&" + Constant.limit + Constant.Followers_load_limit;

        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url ", "People fragment   " + url);
        }
        return getAuthResponse(url, token);
    }


    //Call API - Get Restaurant followers
    public static String getRestaurantFollowers(String token, String RestaurantId, int current_page) {

        String url = Constant.globalRestURL + RestaurantId.trim() + "/followers";

        url = url + "?" + Constant.page + current_page +
                "&" + Constant.limit + Constant.Followers_load_limit;

        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url ", "People fragment   " + url);
        }
        return getAuthResponse(url, token);
    }
    public static String post_email(String token) {

        String url = Constant.globalemailURL1 ;


        Log.d(TAG, "getRestaurant: ");
        if (!Constant.isProductionBuild) {
            Log.d("Restaurant url ", "People fragment   " + url);
        }
        return getAuthResponse(url, token);
    }



    //Call API- Follow user (people follow fragment and user profile screen)
    public static String FollowUser(String id, String access_token, String type) {
        String url = Constant.globalUserURL + id + "/" + type;
        String urlParameters = "";
        if (!Constant.isProductionBuild) {
            Log.d("Log In url", url);
        }
        String result = getPostAuthResponse(url, urlParameters, access_token);
        JSONObject jsonObject;
        Log.d("result", result);
        String Message = "";
        try {
            jsonObject = new JSONObject(result);
            if (jsonObject.has("success")) {
                if (jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                    Message = "true";
                } else {
                    Message = jsonObject.getString("message");
                }
            } else {
                Message = jsonObject.getString("message");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Message;
    }


    /* ----------------------------- Call API end -------------------------------------- */


    /* ----------------------------- GOOGLE APIS ---------------------------------------*/
    public static ArrayList<PlacePredictionModel> autocomplete(String input) {
        ArrayList<PlacePredictionModel> resultList = new ArrayList<>();

        String resultJson = "";
        URL url;
        HttpURLConnection httpUrlConnection = null;
        try {
            String urlString = Constant.googleMapAutocompleteUrl + "&input=" + URLEncoder.encode(input, "utf8");
            url = new URL(urlString);
            Log.d(TAG, "Url : " + url);

            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(30000);
            httpUrlConnection.setReadTimeout(30000);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();
            resultJson = convertStreamToString(HttpConnectionResultInputstream(httpUrlConnection));
            Generalfunction.DisplayLog(resultJson);
        } catch (Exception e) {
            e.printStackTrace();
            return resultList;
        }

        //Parse
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(resultJson);
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                PlacePredictionModel prediction = new PlacePredictionModel();
                JSONObject predictionJson = predsJsonArray.getJSONObject(i);
                prediction.description = predictionJson.getString("description");
                prediction.place_id = predictionJson.getString("place_id");

                try {
                    JSONObject structuredFormattingJson = predictionJson.getJSONObject("structured_formatting");
                    prediction.mainText = structuredFormattingJson.getString("main_text");
                    prediction.secondaryText = structuredFormattingJson.getString("secondary_text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                resultList.add(prediction);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public static AddressModel getPlaceDetails(String placeid) {
        AddressModel placeDetailsModel = null;

        String resultJson = "";
        URL url;
        HttpURLConnection httpUrlConnection = null;
        try {
            String urlString = Constant.googleMapPlaceDetailsUrl + "&placeid=" + URLEncoder.encode(placeid, "utf8");
            url = new URL(urlString);
            Log.d(TAG, "Url : " + url);

            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(30000);
            httpUrlConnection.setReadTimeout(30000);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();
            resultJson = convertStreamToString(HttpConnectionResultInputstream(httpUrlConnection));
            Generalfunction.DisplayLog(resultJson);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //Parse
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(resultJson);
            JSONObject placeDetailsJson = jsonObj.getJSONObject("result");
            Log.e("placeid",""+placeDetailsJson);
            placeDetailsModel = new AddressModel();
            placeDetailsModel.formattedAddress = placeDetailsJson.getString("formatted_address");

            try {
                JSONObject locationJson = placeDetailsJson.getJSONObject("geometry").getJSONObject("location");
                placeDetailsModel.latitude = locationJson.getDouble("lat");
                placeDetailsModel.longitude = locationJson.getDouble("lng");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return placeDetailsModel;
    }

    public static AddressModel getAddressFromLocation(String latlng) {
        AddressModel placeDetailsModel = null;
        String resultJson = "";
        URL url;
        HttpURLConnection httpUrlConnection = null;
        try {
            String urlString = Constant.googleMapGeocodeUrl + "?latlng=" + URLEncoder.encode(latlng, "utf8");
            url = new URL(urlString);
            Log.d(TAG, "Url : " + url);

            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(30000);
            httpUrlConnection.setReadTimeout(30000);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();
            resultJson = convertStreamToString(HttpConnectionResultInputstream(httpUrlConnection));
            Generalfunction.DisplayLog(resultJson);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //Parse
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(resultJson);
            JSONArray addressJsonArray = jsonObj.getJSONArray("results");

            if (addressJsonArray != null && addressJsonArray.length() > 0) {
                JSONObject addressJson = addressJsonArray.getJSONObject(0);

                placeDetailsModel = new AddressModel();
                placeDetailsModel.formattedAddress = addressJson.getString("formatted_address");

                try {
                    JSONObject locationJson = addressJson.getJSONObject("geometry").getJSONObject("location");
                    placeDetailsModel.latitude = locationJson.getDouble("lat");
                    placeDetailsModel.longitude = locationJson.getDouble("lng");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return placeDetailsModel;
    }
    //Call API - Post Dish
//    public static String Post_Dish(String access_token, String restaurantName, String restaurantAddress, String googlePlaceId,
//                                   double lat, double lng, String dishName, String dishPrice, String photoBase64) {
//        String url = Constant.globalDishpostURL;
//        String urlParameters = ""
//                + "dish[name]=" + dishName
//                + "&dish[price]=" + dishPrice
//                + "&[restaurant][name]=" + restaurantName
//                + "&[restaurant][address]=" + restaurantAddress
//                + "&[restaurant][latitude]=" + lat
//                + "&[restaurant][longitude]=" + lng
//                + "&[restaurant][google_place_id]=" + googlePlaceId
//                + "&image_content_type=image/jpeg" + ""
//                + "&image=" + photoBase64;
//
//        Log.d("post params", urlParameters);
//
//        if (!Constant.isProductionBuild) {
//            Log.d("Post url", url);
//        }
//
//        String result = getPostAuthResponse(url, urlParameters, access_token);
//        Log.d("result", result);
//        return result;
//    }
//
//
//}

    public static String Post_Dish(String access_token, String restaurantName, String restaurantAddress, String googlePlaceId,
                                   double lat, double lng, String dishName, String dishPrice, String photoBase64,String quickpost) {


        String result = "error";
        // String Uploadurl = Constant.globalDishURL + id + "/images";   // id as a Dish id
        String Requestmode = "POST";


        JSONObject jsonObject;
        JSONObject Response;
        String image_content_type = "image/jpeg";
        String url1 = Constant.globalDishpostURL;
        String data3;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("name", dishName);
            jsonObject.put("price", dishPrice);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", restaurantName);
            jsonObject1.put("address", restaurantAddress);
            jsonObject1.put("latitude", lat);
            jsonObject1.put("longitude", lng);
            jsonObject1.put("google_place_id", googlePlaceId);
            JSONObject jsonObject2 = new JSONObject();

            jsonObject2.put("dish", jsonObject);

            jsonObject2.put("restaurant", jsonObject1);

            jsonObject2.put("image_content_type", image_content_type);
            jsonObject2.put("image", photoBase64);
            jsonObject2.put("quick_post",quickpost);

            data3 = jsonObject2.toString();


            URL url = new URL(url1);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod(Requestmode);

//            //connection.setFixedLengthStreamingMode(data3.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//
            connection.setRequestProperty("Authorization", "Token token=" + access_token);
            connection.setRequestProperty("Content-Length", Integer.toString(data3.getBytes().length));

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data3);

            Log.d(Constant.page, "Data to php = " + data3);

            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            Generalfunction.DisplayLog(connection.getResponseMessage() + " - " + connection.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(connection));

            Generalfunction.DisplayLog("Response from php = " + result);
//
            connection.disconnect();
        }
            catch (JSONException ex) {
            ex.printStackTrace();
        }

            catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }
    public static String Post_Dishdel(String access_token,String id ) {


        String result = "error";
        // String Uploadurl = Constant.globalDishURL + id + "/images";   // id as a Dish id
        String Requestmode = "POST";


        JSONObject jsonObject2;
        JSONObject Response;

        String url1 = Constant.getdelURL;
        try {
            jsonObject2 = new JSONObject();



            jsonObject2.put("dish_id", id);
//            jsonObject2.put("image_id",image_id);
            String data3 = jsonObject2.toString();

            Log.e("data", data3);

            URL url = new URL(url1);
            HttpURLConnection connection = null;

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod(Requestmode);

            connection.setFixedLengthStreamingMode(data3.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//
            connection.setRequestProperty("Authorization", "Token token=" + access_token);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data3);

            Log.d(Constant.page, "Data to php = " + data3);

            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            Generalfunction.DisplayLog(connection.getResponseMessage() + " - " + connection.getResponseCode());

            result = convertStreamToString(HttpConnectionResultInputstream(connection));

            Generalfunction.DisplayLog("Response from php = " + result);

            connection.disconnect();
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }
}
