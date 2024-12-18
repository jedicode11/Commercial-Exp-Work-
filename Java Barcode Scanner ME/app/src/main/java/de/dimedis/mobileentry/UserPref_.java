package de.dimedis.mobileentry;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPref_ implements UserPref {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public UserPref_(Context context) {
        SharedPreferences pref = context.getSharedPreferences("UserPref", 0);
        this.pref = pref;
        this.editor = pref.edit();
    }

    @Override
    public String userSuid() {
        return null;
    }

    @Override
    public String userName() {
        return null;
    }

    @Override
    public String userFullName() {
        return null;
    }

    @Override
    public String userPrefs() {
        return null;
    }

    @Override
    public String usersBorder() {
        return null;
    }

    @Override
    public String listUserFunctions() {
        return null;
    }

    @Override
    public String userSession() {
        return null;
    }
}
