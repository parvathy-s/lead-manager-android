package com.example.leadmanager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    public SessionManagement(Context context){
        sharedPreferences= context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void saveSession(UserResponse userResponse){
        String usr = userResponse.getUsername();
        editor.putString(SESSION_KEY,usr).commit();
    }
    public String getSession()
    {
        return sharedPreferences.getString(SESSION_KEY,"null");
    }

    public void removeSession(){
        editor.putString(SESSION_KEY,"null").commit();
    }
}
