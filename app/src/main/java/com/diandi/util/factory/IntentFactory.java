package com.diandi.util.factory;

import android.app.Activity;
import android.content.Intent;

/**
 * ************************************************************
 * *********    User : SuLinger(462679107@qq.com) .
 * *********    Date : 2014-09-21  .
 * *********    Time:  2014-09-21  .
 * *********    Project name :DianDi1.1.5 .
 * *********    Copyright @ 2014, SuLinger, All Rights Reserved
 * *************************************************************
 */


public class IntentFactory {

    public void startAnimActivity(Activity activity, Class<?> cla) {
        activity.startActivity(new Intent(activity, cla));
        OverridePendingFactory.in(activity);
    }


    public void startAnimActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        OverridePendingFactory.in(activity);
    }
}
