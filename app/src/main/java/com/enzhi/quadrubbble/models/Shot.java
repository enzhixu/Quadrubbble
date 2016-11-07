package com.enzhi.quadrubbble.models;

import java.util.Date;
import java.util.Map;

/**
 * Created by enzhi on 9/18/2016.
 */
public class Shot {

    public static final String IMAGE_NORMAL = "normal";
    public static final String IMAGE_HIDPI = "hidpi";

    public String id;
    public String title;
    public String description;
    public String html_url;

    public int width;
    public int height;
    public Map<String, String> images;
    public boolean animated;

    public int views_count;
    public int likes_count;
    public int buckets_count;

    public Date created_at;

    public User user;

    public boolean liked;
    public boolean bucketed;
    public Shot(int view_count, int like_count, int bucket_count){
        this.views_count = view_count;
        this.likes_count = like_count;
        this.buckets_count = bucket_count;
    }

    public String getImageUrl(){
        if (images == null){
            return null;
        }
        return images.containsKey(IMAGE_HIDPI) && (images.get(IMAGE_HIDPI) != null)
                ? images.get(IMAGE_HIDPI)
                : images.get(IMAGE_NORMAL);
    }
}
