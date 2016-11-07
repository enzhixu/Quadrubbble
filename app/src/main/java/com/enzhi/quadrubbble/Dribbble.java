package com.enzhi.quadrubbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.enzhi.quadrubbble.models.Bucket;
import com.enzhi.quadrubbble.models.Follow;
import com.enzhi.quadrubbble.models.Like;
import com.enzhi.quadrubbble.models.Shot;
import com.enzhi.quadrubbble.models.User;
import com.enzhi.quadrubbble.utils.ModelUtils;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by enzhi on 9/14/2016.
 */
public class Dribbble {

    private static final String API_URL = "https://api.dribbble.com/v1/";
    private static final String SP_AUTH = "auth";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";
    private static final String USER_END_POINT = API_URL + "user";
    private static final String USERS_END_POINT = API_URL + "users";
    private static final String SHOT_END_POINT = API_URL + "shots";
    private static final String BUCKETS_END_POINT = API_URL + "buckets";
    private static final String KEY_SHOT_ID = "shot_id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NAME = "name";
    private static final TypeToken<User> USER_TYPE = new TypeToken<User>() {};
    private static final TypeToken<List<User>> USERLIST_TYPE = new TypeToken<List<User>>() {};
    private static final TypeToken<List<Follow>> FOLLOWLIST_TYPE = new TypeToken<List<Follow>>() {
    };

    private static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};
    private static final TypeToken<List<Shot>> SHOTLIST_TYPE = new TypeToken<List<Shot>>() {
    };
    private static final TypeToken<List<Like>> LIKELIST_TYPE = new TypeToken<List<Like>>() {
    };
    private static final TypeToken<List<Bucket>> BUCKETLIST_TYPE = new TypeToken<List<Bucket>>() {
    };
    private static OkHttpClient client = new OkHttpClient();
    public static final int COUNT_PER_LOAD = 12;

    static String accessToken;
    private static User user;

    public static void init(Context context) {
        accessToken = loadAccessToken(context);
        user = loadUser(context);
    }

    private static Response makeRequest(Request request) {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            System.out.println("Web request failed.");
            e.printStackTrace();
            return null;
        }
    }

    private static Response makeGetRequest(String url) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken) //bearer後的空格必須有！！！
                .url(url)
                .build();
        return makeRequest(request);
       /* try {
            return client.newCall(request).execute();
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }*/
    }

    private static Response makePostRequest(String url, RequestBody requestBody) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken) //bearer後的空格必須有！！！
                .url(url)
                .post(requestBody)
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            ;
            return null;
        }
    }

    private static Response makePutRequest(String url, RequestBody requestBody) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken) //bearer後的空格必須有！！！
                .url(url)
                .put(requestBody)
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            ;
            return null;
        }
    }

    private static Response makeDeleteRequest(String url) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken) //bearer後的空格必須有！！！
                .url(url)
                .delete()
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            ;
            return null;
        }
    }

    private static Response makeDeleteRequest(String url, RequestBody requestBody) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken) //bearer後的空格必須有！！！
                .url(url)
                .delete(requestBody)
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            ;
            return null;
        }
    }

    private static <T> T parseResponse(Response response, TypeToken<T> typeToken) throws IOException {
        String responseString;
        if (response == null) {
            responseString = "";
        } else {
            responseString = response.body().string();
        }
        return ModelUtils.toObject(responseString, typeToken);
    }

    public static boolean isLoggedIn() {
        return accessToken != null;
    }

    public static void login(Context context, String accessToken) throws IOException {
        Dribbble.accessToken = accessToken;
        storeAccessToken(context, accessToken);
        user = getUser();
        storeUser(context, user);
    }

    public static void logout(Context context) {
        storeAccessToken(context, null);
        storeUser(context, null);
        accessToken = null;
        user = null;
    }

    public static void storeAccessToken(Context context, String accessToken) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public static String loadAccessToken(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public static User getUser() throws IOException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    public static User getOtherUser(String userId) throws IOException {
        return parseResponse(makeGetRequest(USERS_END_POINT + "/" + userId), USER_TYPE);
    }



    public static User getCurrentUser() {
        return user;
    }

    public static void storeUser(Context context, User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    public static User loadUser(Context context) {
        return ModelUtils.read(context, KEY_USER, USER_TYPE);
    }

    public static List<Shot> getShots(int page) throws IOException, JsonSyntaxException {
        String url = SHOT_END_POINT + "?page=" + page;
        return parseResponse(makeGetRequest(url), SHOTLIST_TYPE);
    }

    public static List<Like> getLikes(int page) throws IOException, JsonSyntaxException {
        String url = USER_END_POINT + "/likes?page=" + page;
        return parseResponse(makeGetRequest(url), LIKELIST_TYPE);
    }

    public static List<Shot> getLikedShots(int page) throws IOException, JsonSyntaxException {
        List<Like> likes = getLikes(page);
        List<Shot> likedShots = new ArrayList<>();
        for (Like like : likes) {
            likedShots.add(like.shot);
        }
        return likedShots;
    }

    public static boolean checkLike(String shotId) {
        try {
            String likeUrl = SHOT_END_POINT + "/" + shotId + "/like";
            String res = makeGetRequest(likeUrl).body().string();
            return !res.equals("");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void like(String shotId) {
        try {
            String likeUrl = SHOT_END_POINT + "/" + shotId + "/like";
            RequestBody postBody = new FormBody.Builder().build();
            makePostRequest(likeUrl, postBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unLike(String shotId) {
        try {
            String likeUrl = SHOT_END_POINT + "/" + shotId + "/like";
            makeDeleteRequest(likeUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Bucket> getBuckets(int page) throws IOException, JsonSyntaxException {
        String url = USER_END_POINT + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKETLIST_TYPE);
    }

    public static List<Shot> getBucketShots(String bucketId, int page) throws IOException, JsonSyntaxException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots?page=" + page;
        return parseResponse(makeGetRequest(url), SHOTLIST_TYPE);
    }

    public static List<Bucket> getShotBuckets(String shotId, int page) throws IOException, JsonSyntaxException {
        String url = SHOT_END_POINT + "/" + shotId + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKETLIST_TYPE);
    }

    public static List<Bucket> getShotBuckets(String shotId) throws IOException, JsonSyntaxException {
        String url = SHOT_END_POINT + "/" + shotId + "/buckets";
        return parseResponse(makeGetRequest(url), BUCKETLIST_TYPE);
    }

    public static Set<String> getShotBucketsId(String shotId) throws IOException, JsonSyntaxException {
        List<Bucket> buckets = getShotBuckets(shotId);
        Set<String> shotBucketsId = new HashSet<>();
        for (Bucket bucket : buckets) {
            shotBucketsId.add(bucket.id);
        }
        return shotBucketsId;
    }

    public static void addBucketShot(@NonNull String bucketId,
                                     @NonNull String shotId) throws IOException, JsonSyntaxException{
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .build();
        Response response = makePutRequest(url, formBody);
    }

    public static void removeBucketShot(@NonNull String bucketId,
                                        @NonNull String shotId) throws IOException, JsonSyntaxException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makeDeleteRequest(url, formBody);
    }

    public static Bucket newBucket(@NonNull String name,
                                   @NonNull String description) throws IOException, JsonSyntaxException {
        FormBody formBody = new FormBody.Builder()
                .add(KEY_NAME, name)
                .add(KEY_DESCRIPTION, description)
                .build();
        return parseResponse(makePostRequest(BUCKETS_END_POINT, formBody), BUCKET_TYPE);
    }

    public static List<Shot> getUserShots(String userId, int page) throws IOException, JsonSyntaxException {
        String url = USERS_END_POINT + "/" + userId + "/shots?page=" + page;
        return parseResponse(makeGetRequest(url), SHOTLIST_TYPE);
    }

    public static List<Follow> getFollows(int page) throws IOException, JsonSyntaxException {
        String url = USER_END_POINT + "/following?page=" + page;
        return parseResponse(makeGetRequest(url), FOLLOWLIST_TYPE);
    }

    public static List<User> getFollowees(int page) throws IOException, JsonSyntaxException {
        List<Follow> follows = getFollows(page);
        List<User> followees = new ArrayList<>();
        for (Follow follow : follows) {
            followees.add(follow.followee);
        }
        return followees;
    }

    public static boolean checkFollowing(@NonNull String userId) throws IOException, JsonSyntaxException{
        String url = USER_END_POINT  + "/following/" + userId;
        return makeGetRequest(url).code() == 204;
    }

    public static void follow(@NonNull String userId) throws IOException, JsonSyntaxException{
        String url = USERS_END_POINT + "/" + userId + "/follow";
        FormBody formBody = new FormBody.Builder()
                .build();
        Response response = makePutRequest(url, formBody);
    }

    public static void unfollow(@NonNull String userId) throws IOException, JsonSyntaxException{
        String url = USERS_END_POINT + "/" + userId + "/follow";
        FormBody formBody = new FormBody.Builder()
                .build();

        Response response = makeDeleteRequest(url, formBody);
    }
}
