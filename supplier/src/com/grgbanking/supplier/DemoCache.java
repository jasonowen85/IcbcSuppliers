package com.grgbanking.supplier;

import android.content.Context;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * Created by jezhee on 2/20/15.
 */
public class DemoCache {

    private static Context context;

    private static String account;
    private static String userid;
    private static String userrole;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
        userid = null;
        userrole = null;
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        DemoCache.account = account;
        NimUIKit.setAccount(account);
    }


    public static String getUserid() {
        return userid;
    }

    public static void setUserid(String userid) {
        DemoCache.userid = userid;
    }

    public static String getUserRole() {
        if (userrole == null) {
            return "";
        } else
            return userrole;
    }

    public static void setUserRole(String userrole) {
        DemoCache.userrole = userrole;
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        DemoCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DemoCache.context = context.getApplicationContext();
    }
}
