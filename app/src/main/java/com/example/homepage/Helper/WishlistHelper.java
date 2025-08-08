package com.example.homepage.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.homepage.Domain.PopularDomain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WishlistHelper {
    private static final String PREF_NAME = "WishlistPrefs";
    private static final String WISHLIST_KEY = "wishlist_items";

    public static void addToWishlist(Context context, PopularDomain item) {
        List<PopularDomain> wishlist = getWishlist(context);
        wishlist.add(item);
        saveWishlist(context, wishlist);
    }

    public static void removeFromWishlist(Context context, String title) {
        List<PopularDomain> wishlist = getWishlist(context);
        for (int i = 0; i < wishlist.size(); i++) {
            if (wishlist.get(i).getTitle().equals(title)) {
                wishlist.remove(i);
                break;
            }
        }
        saveWishlist(context, wishlist);
    }

    public static List<PopularDomain> getWishlist(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(WISHLIST_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<PopularDomain>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static boolean isInWishlist(Context context, String title) {
        List<PopularDomain> wishlist = getWishlist(context);
        for (PopularDomain item : wishlist) {
            if (item.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public static void saveWishlist(Context context, List<PopularDomain> wishlist) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(wishlist);
        editor.putString(WISHLIST_KEY, json);
        editor.apply();
    }
}