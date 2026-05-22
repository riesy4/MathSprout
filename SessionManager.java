package com.example.mathsprout;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MathSproutSession";
    private static final String KEY_CHILD_ID = "childId";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveChildId(String childId) {
        editor.putString(KEY_CHILD_ID, childId);
        editor.apply();
    }

    public String getChildId() {
        return pref.getString(KEY_CHILD_ID, null);
    }

    public void clearChildId() {
        editor.remove(KEY_CHILD_ID);
        editor.apply();
    }
}
