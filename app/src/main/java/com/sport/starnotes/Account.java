package com.sport.starnotes;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Account {
    public static int clicked_note_id;
    public static Boolean tapped_on_item = false;
    public static Boolean created_or_edited = false;

    public static int uuidCounter() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("uuidCounter", 0);
    }

    public static void updateUuidCounter() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("uuidCounter",uuidCounter()+1);
        editor.apply();
    }

    public static String theme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("theme", "normal");
    }

    public static void setTheme(String theme) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("theme",theme);
        editor.apply();
    }

    public static Boolean syncWatch() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("syncWatch", true);
    }

    public static void setSyncWatch(Boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("syncWatch",b);
        editor.apply();
    }

    public static String order_type() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("order_type", "edited");
    }

    public static void setOrder_type(String type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("order_type",type);
        editor.apply();
    }
}
