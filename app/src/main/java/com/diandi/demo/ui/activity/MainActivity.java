package com.diandi.demo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diandi.demo.CustomApplication;
import com.diandi.demo.R;
import com.diandi.demo.receiver.MyMessageReceiver;
import com.diandi.dragmenu.DragLayout;
import com.diandi.demo.model.User;
import com.diandi.demo.sync.UserHelper;
import com.diandi.demo.ui.fragment.ContactFragment;
import com.diandi.demo.ui.fragment.DiandiFragment;
import com.diandi.demo.ui.fragment.RecentFragment;
import com.diandi.demo.util.OverridePendingUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.fb.FeedbackAgent;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

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
public class MainActivity extends ActivityBase implements EventListener, View.OnClickListener {
    public final static int INFOR_REFREFLASH = 100;
    private static DragLayout mDragLayout;
    private static long firstTime;
    private ImageView iv_recent_tips, iv_contact_tips, iv_diandi_tips;//消息提示
    private Button mDiandiBtn, mRecentBtn, mContanctBtn;
    private View currentButton;
    private LinearLayout mUserLayout;
    private LinearLayout mFeedbackLayout;
    private LinearLayout mAboutLayout;
    private LinearLayout mSettingLayout;
    private ImageView mUserIconImg;
    private TextView mUserNameText;
    private DiandiFragment diandiFragment;
    private ContactFragment contactFragment;
    private RecentFragment recentFragment;
    private int currentTabIndex;
    private View.OnClickListener diandiOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            diandiFragment = new DiandiFragment();
            ft.replace(R.id.fragment_container, diandiFragment, TAG);
            ft.commit();
            setButton(view);
            currentTabIndex = 0;
        }
    };

    private View.OnClickListener recentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (recentFragment == null)
                recentFragment = new RecentFragment();
            ft.replace(R.id.fragment_container, recentFragment, TAG);
            ft.commit();
            setButton(view);
            currentTabIndex = 1;

        }
    };
    private View.OnClickListener contanctOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (contactFragment == null)
                contactFragment = new ContactFragment();
            ft.replace(R.id.fragment_container, contactFragment, TAG);
            ft.commit();
            setButton(view);
            currentTabIndex = 2;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
    }

    @Override
    void findView() {
        setContentView(R.layout.activity_main);
        mDragLayout = (DragLayout) findViewById(R.id.main_drag);
        mUserLayout = (LinearLayout) findViewById(R.id.activity_main_user_layout);
        mFeedbackLayout = (LinearLayout) findViewById(R.id.activity_main_feedback_layout);
        mAboutLayout = (LinearLayout) findViewById(R.id.activity_main_about_layout);
        mSettingLayout = (LinearLayout) findViewById(R.id.activity_main_setting_layout);
        mUserIconImg = (ImageView) findViewById(R.id.activity_main_user_avatar_img);
        mUserNameText = (TextView) findViewById(R.id.activity_main_user_name_text);
        mDiandiBtn = (Button) findViewById(R.id.btn_diandi);
        mRecentBtn = (Button) findViewById(R.id.btn_message);
        mContanctBtn = (Button) findViewById(R.id.btn_contract);
        iv_diandi_tips = (ImageView) findViewById(R.id.iv_diandi_tips);
        iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
        iv_contact_tips = (ImageView) findViewById(R.id.iv_contact_tips);
    }

    public DragLayout getDragLayout() {
        return mDragLayout;
    }

    @Override
    void initView() {
        bindEvent();
        mDiandiBtn.performClick();

    }

    @Override
    void bindEvent() {
        mDragLayout.setDragListener(new DragLayout.DragListener() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onClose() {
            }

            @Override
            public void onDrag(float percent) {
                ViewHelper.setAlpha(diandiFragment.getUserAvatarImg(), 1 - percent);
            }
        });
        mDiandiBtn.setOnClickListener(diandiOnClickListener);
        mRecentBtn.setOnClickListener(recentOnClickListener);
        mContanctBtn.setOnClickListener(contanctOnClickListener);
        mUserLayout.setOnClickListener(this);
        mFeedbackLayout.setOnClickListener(this);
        mAboutLayout.setOnClickListener(this);
        mSettingLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_user_layout:
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("from", "me");
                startActivityForResult(intent, INFOR_REFREFLASH);
                OverridePendingUtil.in(MainActivity.this);
                break;
            case R.id.activity_main_feedback_layout:
                FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
                agent.startFeedbackActivity();
                OverridePendingUtil.in(MainActivity.this);
                break;
            case R.id.activity_main_about_layout:
                startAnimActivity(AboutActivity.class);
                break;
            case R.id.activity_main_setting_layout:
                startAnimActivity(SettingActivity.class);
                break;
        }
    }

    private void setButton(View v) {
        if (currentButton != null && currentButton.getId() != v.getId()) {
            currentButton.setEnabled(true);
            currentButton.setSelected(false);
        }
        v.setEnabled(false);
        v.setSelected(true);
        currentButton = v;
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = UserHelper.getCurrentUser();
        if (user != null) {
            ImageLoader.getInstance().displayImage(user.getAvatar(), mUserIconImg, new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.default_head_cry)
                    .showImageForEmptyUri(R.drawable.default_head_cry)
                    .showImageOnFail(R.drawable.default_head_cry)
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(90))
                    .build());
            mUserNameText.setText(user.getNick());
        }
        //小圆点提示
        if (BmobDB.create(this).hasUnReadMsg()) {
            iv_recent_tips.setVisibility(View.VISIBLE);
        } else {
            iv_recent_tips.setVisibility(View.GONE);
        }
        if (BmobDB.create(this).hasNewInvite()) {
            iv_contact_tips.setVisibility(View.VISIBLE);
        } else {
            iv_contact_tips.setVisibility(View.GONE);
        }
        iv_diandi_tips.setVisibility(View.GONE);
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        //清空
        MyMessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.ehList.remove(this);
    }

    @Override
    public void onMessage(BmobMsg message) {
        // 声音提示
        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            CustomApplication.getInstance().getMediaPlayer().start();
        }
        iv_recent_tips.setVisibility(View.VISIBLE);
        //保存接收到的消息-并发送已读回执给对方
        BmobChatManager.getInstance(this).saveReceiveMessage(true, message);
        if (currentTabIndex == 1) {
            //当前页面如果为会话页面，刷新此页面
            if (recentFragment != null) {
                recentFragment.refresh();
            }
        }

    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if (isNetConnected) {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        // 声音提示
        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            CustomApplication.getInstance().getMediaPlayer().start();
        }
        iv_contact_tips.setVisibility(View.VISIBLE);
        if (currentTabIndex == 2) {
            if (contactFragment != null) {
                contactFragment.refresh();
            }
        } else {
            String tickerText = message.getFromname() + "请求添加好友";
            boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
            BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate, R.drawable.icc_launcher, tickerText, message.getFromname(), tickerText, NewFriendActivity.class);
        }
    }

    @Override
    public void onOffline() {
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // TODO Auto-generated method stub
    }

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
        firstTime = System.currentTimeMillis();
    }


}
