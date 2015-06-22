package com.diandi.demo.sync;

import android.content.Context;

import com.diandi.demo.CustomApplication;
import com.diandi.demo.model.User;

import cn.bmob.v3.BmobUser;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2014-11-29  .
 * *********    Time : 11:46 .
 * *********    Project name : Diandi1.18 .
 * *********    Version : 1.0
 * *********    Copyright @ 2014, klob, All Rights Reserved
 * *******************************************************************************
 */
public class UserHelper {
    public static User getCurrentUser(Context context) {
        User user = BmobUser.getCurrentUser(context, User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

    public static User getCurrentUser() {
        User user = BmobUser.getCurrentUser(CustomApplication.getInstance(), User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

    public static String getUserId() {
        return getCurrentUser().getObjectId();
    }
}
