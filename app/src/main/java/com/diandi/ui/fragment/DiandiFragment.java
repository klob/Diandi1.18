package com.diandi.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.diandi.CustomApplication;
import com.diandi.R;
import com.diandi.bean.User;
import com.diandi.ui.activity.MainActivity;
import com.diandi.ui.activity.NewDiandiActivity;
import com.diandi.ui.activity.NewOfficalDiandiActivity;
import com.diandi.util.L;
import com.diandi.util.factory.OverridePendingFactory;
import com.diandi.view.dialog.ActionItem;
import com.diandi.view.dialog.TitlePop;
import com.diandi.view.residemenu.ResideMenu;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiandiFragment extends BaseFragment {


    private final static String TAG = "DiandiFragment";

    private View mContentView;
    private Button mFeedBtn;
    private Button mChannelBtn;
    private ImageView mUserAvatarImg;
    private ImageButton mMoreBtn;
    private ImageButton mNewDiandiBtn;
    private FeedFragment mFeedFragment;
    private ChannelFragment mChannelFragment;
    private TitlePop mTitlePop;


    public DiandiFragment() {
    }

    @Override
    void initView() {
        ImageLoader.getInstance().displayImage(CustomApplication.getInstance().getCurrentUser().getAvatar(), mUserAvatarImg, CustomApplication.getInstance().getOptions());
        bindEvent();
        mFeedBtn.performClick();
    }

    @Override
    void findView() {
        mUserAvatarImg = (ImageView) mContentView.findViewById(R.id.fragment_diandi_user_avatar_img);
        mFeedBtn = (Button) mContentView.findViewById(R.id.fragment_diandi_feed_btn);
        mChannelBtn = (Button) mContentView.findViewById(R.id.fragment_diandi_channel_btn);
        mMoreBtn = (ImageButton) mContentView.findViewById(R.id.fragment_diandi_more_btn);
        mNewDiandiBtn = (ImageButton) mContentView.findViewById(R.id.fragment_diandi_new_diandi_btn);
    }

    @Override
    void bindEvent() {
        mFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedBtn.isEnabled()) {
                    mFeedBtn.setEnabled(false);
                    mChannelBtn.setEnabled(true);
                }
                mNewDiandiBtn.setVisibility(View.VISIBLE);
                mMoreBtn.setVisibility(View.GONE);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (mFeedFragment == null) {
                    mFeedFragment = new FeedFragment();
                }
                ft.replace(R.id.fragmet_diandi_container, mFeedFragment, TAG);
                ft.commit();
            }
        });

        mChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChannelBtn.isEnabled()) {
                    mChannelBtn.setEnabled(false);
                    mFeedBtn.setEnabled(true);
                }
                mMoreBtn.setVisibility(View.VISIBLE);
                mNewDiandiBtn.setVisibility(View.GONE);
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                if (mChannelFragment == null) {
                    mChannelFragment = new ChannelFragment();
                }
                ft.replace(R.id.fragmet_diandi_container, mChannelFragment, TAG);
                ft.commit();
            }
        });

        mUserAvatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).getResideMenu().openMenu(ResideMenu.DIRECTION_LEFT);
                ((MainActivity) getActivity()).getResideMenu().setMenuListener(new ResideMenu.OnMenuListener() {
                    @Override
                    public void openMenu() {
                        //      mUserIconImg.setVisibility(View.GONE);
                    }

                    @Override
                    public void closeMenu() {
                        //     mUserIconImg.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        mNewDiandiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = mApplication.getCurrentUser();
                if (user.isOfficial()) {
                    startAnimActivity(NewOfficalDiandiActivity.class);
                    L.e(TAG, user.toString());
                } else {
                    startAnimActivity(NewDiandiActivity.class);
                    L.e(TAG, user.toString() + "    ");
                }
                OverridePendingFactory.in(getActivity());
            }
        });

        mMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         /*       ChannelPopWindow channelPopWindow = new ChannelPopWindow(getActivity());
                channelPopWindow.showPopupWindow(mMoreBtn);*/
                mTitlePop = new TitlePop(getActivity(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mTitlePop.addAction(new ActionItem(getActivity(), "华科", R.drawable.hust_));
                mTitlePop.show(view);
                mTitlePop.setItemOnClickListener(new TitlePop.OnItemOnClickListener() {
                    @Override
                    public void onItemClick(ActionItem item, int position) {
                        if (mChannelFragment != null) {
                            switch (position) {
                                case 0:

                                    break;

                            }
                        }
                    }
                });
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_diandi_father, null);
        findView();
        initView();
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
