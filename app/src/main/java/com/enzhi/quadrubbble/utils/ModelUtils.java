package com.enzhi.quadrubbble.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Created by enzhi on 9/14/2016.
 */
public class ModelUtils {

    private static String PREF_NAME = "models";
    private static Gson gson = new Gson();

    public static void save(Context context, String key, Object object){

        //getApplicationContext() 怎麼回事？？？？
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String jsonObject = gson.toJson(object);
        editor.putString(key, jsonObject).apply();
    }
    public static <T>T read(Context context, String key, TypeToken<T> typeToken){
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonObject = sp.getString(key, "");
        try{
            return gson.fromJson(jsonObject, typeToken.getType());
        }catch (JsonSyntaxException e){
            e.printStackTrace();
            return null;
        }
    }

    public static <T>T toObject (String json, TypeToken<T> typeToken){
        return gson.fromJson(json, typeToken.getType());
    }
    public static String toString(Object object){
        return gson.toJson(object);         //和老師不一樣，估計有錯誤
    }
}
