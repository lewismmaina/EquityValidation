package com.example.equityvalidation;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;


public class SessionHandler {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_AGENTCODE = "agentcode";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_BRANCH = "branch";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    public void loginUser(String agentcode, String fullName) {
        mEditor.putString(KEY_AGENTCODE, agentcode);
        mEditor.putString(KEY_FULL_NAME, fullName);
        Date date = new Date();

        //expiry after 14 days
        long millis = date.getTime() + (14 * 24 * 60 * 60 * 1000);
        mEditor.putLong(KEY_EXPIRES, millis);
        mEditor.commit();
    }

    public boolean isLoggedIn() {
        Date currentDate = new Date();

        long millis = mPreferences.getLong(KEY_EXPIRES, 0);

        if (millis == 0) {
            return false;
        }
        Date expiryDate = new Date(millis);


        return currentDate.before(expiryDate);
    }
    //method where specified details can be accessed anywhere
    public User getUserDetails() {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setAgentcode(mPreferences.getString(KEY_AGENTCODE, KEY_EMPTY));
        user.setFullName(mPreferences.getString(KEY_FULL_NAME, KEY_EMPTY));
        user.setBranch(mPreferences.getString(KEY_BRANCH, KEY_EMPTY));
        user.setCountry(mPreferences.getString(KEY_COUNTRY, KEY_EMPTY));
        user.setSessionExpiryDate(new Date(mPreferences.getLong(KEY_EXPIRES, 0)));

        return user;
    }
    //log out the user
    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }

}
