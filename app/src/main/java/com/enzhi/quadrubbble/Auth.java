package com.enzhi.quadrubbble;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by enzhi on 9/13/2016.
 */
public class Auth {
    public static final int REQ_CODE = 100;

    private static final String KEY_CODE = "code";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String CLIENT_ID = "cbea30040f4049fdcdf0c82d1a60080507eaf7fb83340ed3205bdca125e40bb9";
    private static final String CLIENT_SECRET = "ab54accf371c6aa8f3b5e8e2b32039de90111e69e2a085b802639ebe9085c1bf";

    private static final String SCOPE = "public+write";
    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";
    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";

    public static final String REDIRECT_URI = "http://www.dribbbo.com";

    static String getAuthrizeURL(){
        String url = Uri.parse(URI_AUTHORIZE)       //為何不直接用string
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .build()
                .toString();
        // fix encode issue
        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;
        return url;
    }

    public static void openAuthActivity(Activity activity){
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.KEY_URL, getAuthrizeURL());
        activity.startActivityForResult(intent, REQ_CODE);
    }

    public static String fetchAccessToken(String authCode) throws IOException{
        OkHttpClient client = new OkHttpClient();

        String body = KEY_CLIENT_ID + "=" + CLIENT_ID;
        body += "&" + KEY_CLIENT_SECRET + "=" + CLIENT_SECRET;
        body += "&" + KEY_CODE + "=" + authCode;
        body += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;

        RequestBody postBody = new FormBody.Builder()
                .add(KEY_CLIENT_ID, CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_CODE, authCode)
                .add(KEY_REDIRECT_URI, REDIRECT_URI)
                .build();

        //RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(URI_TOKEN)
                .post(postBody)
                .build();
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        try{
            JSONObject obj = new JSONObject(responseString);
            return obj.getString(KEY_ACCESS_TOKEN);
        }catch (JSONException e){
            e.printStackTrace();
            return "";
        }
    }
}
