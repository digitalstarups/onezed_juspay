package com.onezed.GlobalVariable;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "kesowa_shared_pref";
    public static final String TOKEN = "token";
    public static final String PROFILE_ID = "pID";
    public static final String uPass = "rt_pass";
    public static final String STORED_PIN = "stored_pin";
    public static final String TENANT_ID = "tenant_id";
    public static final String USER_NAME = "user_name";
    public static final String PHONE_NO = "phone_no";
    public static final String IS_LOGIN = "isLogIn";
    public static final String PHOTO_URL = "photo_url";
    public static final String PROFILE_IMAGE_URL = "profile_image_url";

    public static final String USER_ID = "user_id"; // Add this line



    Context mContext;
    SharedPreferences prefManager;
    SharedPreferences.Editor editor;

    public SharedPrefManager(Context mContext) {
        this.mContext = mContext;
        prefManager = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = prefManager.edit();
    }
    public void saveUserPin(String userPin){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(STORED_PIN, userPin);
        editor.commit();
    }
    public void saveUserPass(String userPass){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(uPass, userPass);
        editor.commit();
    }

    public void saveUserDetails(String userId) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(PROFILE_ID, userId);
        editor.commit();
    }
    public void saveUserId(int userId) {
        editor.putInt(USER_ID, userId);
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }
    public int getUserId() {
        return prefManager.getInt(USER_ID, -1); // Returns -1 if userId is not found
    }
    public void saveImageUrl(String imageUrl) {
        editor.putString(PROFILE_IMAGE_URL, imageUrl);
        editor.commit();
    }
    public String getProfileImageUrl() {
        return prefManager.getString(PROFILE_IMAGE_URL, null); // Returns null if imageUrl is not found
    }


    public boolean checkLogIn() {
        boolean loginStatus = prefManager.getBoolean(IS_LOGIN, false);
        return loginStatus;
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> userDetails = new HashMap<String, String>();
        userDetails.put(TOKEN, prefManager.getString(TOKEN, null));
        userDetails.put(PROFILE_ID, prefManager.getString(PROFILE_ID, null));
        userDetails.put(uPass, prefManager.getString(uPass, null));
        userDetails.put(STORED_PIN, prefManager.getString(STORED_PIN, null));
        userDetails.put(TENANT_ID, prefManager.getString(TENANT_ID, null));
        userDetails.put(USER_NAME, prefManager.getString(USER_NAME, null));
        userDetails.put(PHONE_NO, prefManager.getString(PHONE_NO, null));
        userDetails.put(PHOTO_URL, prefManager.getString(PHOTO_URL, null));
        userDetails.put(USER_ID, String.valueOf(prefManager.getInt(USER_ID, -1))); // Convert int to String
        return userDetails;
    }

    public void clear() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}