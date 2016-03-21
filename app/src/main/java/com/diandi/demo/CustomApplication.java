package com.diandi.demo;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.diandi.demo.R;
import com.diandi.demo.io.ACache;
import com.diandi.demo.model.diandi.DianDi;
import com.diandi.demo.util.ActivityManagerUtils;
import com.diandi.demo.util.CollectionUtils;
import com.diandi.demo.util.ImageLoadOptions;
import com.diandi.demo.util.L;
import com.diandi.demo.util.SharePreferenceUtil;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobGeoPoint;

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
public class CustomApplication extends Application {

    public final static String TAG = "CustomApplication";
    public final static String PREFERENCE_NAME = "_sharedinfo";
    public static CustomApplication mInstance;
    public static BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度
    public final String PREF_LONGTITUDE = "longtitude";// 经度
    public final String PREF_LATITUDE = "latitude";// 纬度
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public SharePreferenceUtil mSpUtil;  // 单例模式，才能及时返回数据
    public NotificationManager mNotificationManager;
    public MediaPlayer mMediaPlayer;
    private DianDi currentDianDi;
    private String longtitude = "";
    private String latitude = "";
    private ACache mACache;
    private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

    public static CustomApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BmobChat.DEBUG_MODE = true;
        mInstance = this;
        init();
    }

    public ACache getCache() {
        if (mACache == null) {
            return ACache.get(getApplicationContext());
        } else {
            return mACache;
        }
    }

    public DianDi getCurrentDianDi() {
        return currentDianDi;
    }

    public void setCurrentDianDi(DianDi dianDi) {
        this.currentDianDi = dianDi;
    }

    public void addActivity(Activity ac) {
        ActivityManagerUtils.getInstance().addActivity(ac);
    }

    public void exit() {
        ActivityManagerUtils.getInstance().removeAllActivity();
    }

    public Activity getTopActivity() {
        return ActivityManagerUtils.getInstance().getTopActivity();
    }


    private void init() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        ImageLoadOptions.initImageLoader(getApplicationContext());
        // 若用户登陆过，则先从好友数据库中取出好友list存入内存中
        if (BmobUserManager.getInstance(getApplicationContext())
                .getCurrentUser() != null) {
            // 获取本地好友user list到内存,方便以后获取好友list
            contactList = CollectionUtils.list2map(BmobDB.create(
                    getApplicationContext()).getContactList());
        }
        initBaidu();
    }

    /**
     * 初始化百度相关sdk initBaidumap
     */
    private void initBaidu() {
        // 初始化地图Sdk
        SDKInitializer.initialize(this);
        // 初始化定位sdk
        initBaiduLocClient();
    }

    /**
     * 初始化百度定位sdk
     */
    private void initBaiduLocClient() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mLocationClient.start();
    }

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null) {
            String currentId = BmobUserManager.getInstance(
                    getApplicationContext()).getCurrentUserObjectId();
            String sharedName = PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this, sharedName);
        }
        return mSpUtil;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        return mMediaPlayer;
    }

    /**
     * 获取经度
     *
     * @return
     */
    public String getLongtitude() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        longtitude = preferences.getString(PREF_LONGTITUDE, "");
        return longtitude;
    }

    public void setLongtitude(String lon) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
            longtitude = lon;
        }
    }

    /**
     * 获取纬度
     */
    public String getLatitude() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        latitude = preferences.getString(PREF_LATITUDE, "");
        return latitude;
    }

    /**
     * 设置维度
     */
    public void setLatitude(String lat) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_LATITUDE, lat).commit()) {
            latitude = lat;
        }
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void logout() {
        BmobUserManager.getInstance(getApplicationContext()).logout();
        setContactList(null);
        setLatitude(null);
        setLongtitude(null);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            L.e(TAG,"getProvince   " + location.getProvince()+"    getCity     " +location.getCity());
            double latitude = location.getLatitude();
            double longtitude = location.getLongitude();
            if (lastPoint != null && lastPoint.getLatitude() == location.getLatitude()
                    && lastPoint.getLongitude() == location.getLongitude()) {
                BmobLog.i("两次获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
                mLocationClient.stop();
                return;
            }
            lastPoint = new BmobGeoPoint(longtitude, latitude);
        }
    }

}
