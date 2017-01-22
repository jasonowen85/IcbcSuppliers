package com.grgbanking.supplier.config.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.grgbanking.supplier.DemoCache;

/**
 * Created by hzxuwen on 2015/4/13.
 */
public class Preferences {
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_USER_ROLE = "userrole";
    private static final String KEY_USER_ROLEIDS = "userroleids";
    private static final String KEY_USER_ROLENAMES = "userrolenames";

    public static void clear() {
        removeString(KEY_USER_ACCOUNT);
        removeString(KEY_USER_ID);
        removeString(KEY_USER_ROLE);
    }

    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    public static void saveUserid(String userid) {
        saveString(KEY_USER_ID, userid);
    }

    public static String getUserid() {
        return getString(KEY_USER_ID);
    }

    public static void saveUserRole(String userrole) {
        saveString(KEY_USER_ROLE, userrole);
    }

    public static String getUserRole() {
        return getString(KEY_USER_ROLE);
    }


    public static void saveUserRoleids(String userroleids) {
        saveString(KEY_USER_ROLEIDS, userroleids);
    }

    public static String getUserRoleids() {
        return getString(KEY_USER_ROLEIDS);
    }

    public static void saveUserRolenames(String userrolenames) {
        saveString(KEY_USER_ROLENAMES, userrolenames);
    }

    public static String getUserRolenames() {
        return getString(KEY_USER_ROLENAMES);
    }


    private static void removeString(String key) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        editor.commit();
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    static SharedPreferences getSharedPreferences() {
        return DemoCache.getContext().getSharedPreferences("Demo", Context.MODE_PRIVATE);
    }
}
