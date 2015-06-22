package com.diandi.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diandi.demo.CustomApplication;
import com.diandi.demo.R;
import com.diandi.demo.adapter.base.BaseListAdapter;
import com.diandi.demo.db.DatabaseUtilC;
import com.diandi.demo.model.User;
import com.diandi.demo.model.diandi.DianDi;
import com.diandi.demo.model.diandi.OfficialDiandi;
import com.diandi.demo.sync.UserHelper;
import com.diandi.demo.sync.sns.TencentShare;
import com.diandi.demo.sync.sns.TencentShareConstants;
import com.diandi.demo.sync.sns.TencentShareEntity;
import com.diandi.demo.ui.activity.CommentActivity;
import com.diandi.demo.ui.activity.ImageBrowserActivity;
import com.diandi.demo.ui.activity.LoginActivity;
import com.diandi.demo.util.ActivityUtil;
import com.diandi.demo.util.ImageLoadOptions;
import com.diandi.demo.util.L;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

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
public class ChannelAdapter extends BaseListAdapter<OfficialDiandi> {
    public static final int SAVE_FAVOURITE = 2;
    public static final int DIANDI_ALL = 1;
    private int mDiandiType = DIANDI_ALL;
    public static final int DIANDI_PERSON = 2;
    public static final int DIANDI_FAV = 3;
    private static final String VIEW_ID = "view_id_";

    public ChannelAdapter(Context context, List<OfficialDiandi> list) {
        super(context, list);
    }

    public ChannelAdapter(Context context, List<OfficialDiandi> list, int diandiType) {
        super(context, list);
        this.mDiandiType = diandiType;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_feed, null);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.userLogo = (ImageView) convertView.findViewById(R.id.user_logo);
            viewHolder.favMark = (ImageView) convertView.findViewById(R.id.item_action_fav);
            viewHolder.contentText = (TextView) convertView.findViewById(R.id.content_text);
            viewHolder.contentImage = (ImageView) convertView.findViewById(R.id.content_image);
            viewHolder.contentLink = (TextView) convertView.findViewById(R.id.content_link);
            viewHolder.love = (TextView) convertView.findViewById(R.id.item_action_love);
            viewHolder.share = (TextView) convertView.findViewById(R.id.item_action_share);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.item_action_comment);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final OfficialDiandi entity = mDataList.get(position);
        L.i("user", entity.toString());
        User user = entity.getAuthor();
        if (user == null) {
            L.i("user", "USER IS NULL");
        }
        if (user.getAvatar() == null) {
            L.i("user", "USER avatar IS NULL");
        }

        String avatarUrl = null;
        if (user.getAvatar() != null) {
            avatarUrl = user.getAvatar();
        }
        ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.userLogo, ImageLoadOptions.getOptions());
        viewHolder.userName.setText(entity.getAuthor().getNick());

        viewHolder.contentText.setText(entity.getContent());
        if (null == entity.getContentfigureurl()) {
            viewHolder.contentImage.setVisibility(View.GONE);
        } else {
            viewHolder.contentImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance()
                    .displayImage(entity.getContentfigureurl().getFileUrl() == null ? "" : entity.getContentfigureurl().getFileUrl(), viewHolder.contentImage,
                            ImageLoadOptions.getOptions(R.drawable.bg_pic_loading),
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view,
                                                              Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    float[] cons = ActivityUtil.getBitmapConfiguration(loadedImage, viewHolder.contentImage, 1.0f);
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) cons[0], (int) cons[1]);
                                    layoutParams.addRule(RelativeLayout.BELOW, R.id.content_text);
                                    viewHolder.contentImage.setLayoutParams(layoutParams);
                                }

                            }
                    );
            viewHolder.contentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                    ArrayList<String> photos = new ArrayList<String>();
                    photos.add(entity.getContentfigureurl().getFileUrl());
                    intent.putStringArrayListExtra("photos", photos);
                    intent.putExtra("position", 0);
                    mContext.startActivity(intent);
                }
            });
        }
        final String link = entity.getLink();
        if (link != null) {
            viewHolder.contentLink.setVisibility(View.VISIBLE);
            viewHolder.contentLink.setText(link);
            viewHolder.contentLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(link);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(it);
                }
            });
        } else {
            viewHolder.contentLink.setVisibility(View.GONE);
        }
        viewHolder.love.setText(entity.getLove() + "");
        L.i("love", entity.getMyLove() + "..");
        if (entity.getMyLove()) {
            viewHolder.love.setTextColor(Color.parseColor("#D95555"));
        } else {
            viewHolder.love.setTextColor(Color.parseColor("#888888"));
        }
    /*    if (DatabaseUtil.getInstance(mContext).isLoved(entity)) {
            entity.setMyLove(true);
            entity.setLove(entity.getLove());
            viewHolder.love.setTextColor(Color.parseColor("#D95555"));
            viewHolder.love.setText(entity.getLove() + "");
        }
*/
        /**
         *  点赞
         */
        viewHolder.love.setOnClickListener(new View.OnClickListener() {
            boolean oldFav = entity.getMyFav();

            @Override
            public void onClick(View v) {
                if (entity.getMyLove()) {
                    viewHolder.love.setTextColor(Color.parseColor("#D95555"));
                    return;
                }

                entity.setLove(entity.getLove() + 1);
                viewHolder.love.setTextColor(Color.parseColor("#D95555"));
                viewHolder.love.setText(entity.getLove() + "");

                entity.increment("love", 1);
                if (entity.getMyFav()) {
                    entity.setMyFav(false);
                }
          /*      entity.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        entity.setMyLove(true);
                        entity.setMyFav(oldFav);
                        DatabaseUtil.getInstance(mContext).insertFav(entity);
                        L.i(TAG, "点赞成功~");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        entity.setMyLove(true);
                        entity.setMyFav(oldFav);
                    }
                });*/
            }
        });

        /**
         *  分享
         */

        viewHolder.share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ShowToast("分享给好友看哦");
                final TencentShare tencentShare = new TencentShare(CustomApplication.getInstance().getTopActivity(), getQQShareEntity(entity));
                tencentShare.shareToQQ();
            }
        });
        /**
         *  评论
         */
        viewHolder.comment.setVisibility(View.GONE);
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserHelper.getCurrentUser() == null) {
                    ShowToast("请先登录。");
                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    CustomApplication.getInstance().getTopActivity().startActivity(intent);
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(CustomApplication.getInstance().getTopActivity(), CommentActivity.class);
                intent.putExtra("data", entity);
                startAnimActivity(intent);
            }
        });


        /**
         *  收藏
         */
        if (entity.getMyFav()) {
            viewHolder.favMark.setImageResource(R.drawable.ic_action_fav_choose);
        } else {
            viewHolder.favMark.setImageResource(R.drawable.ic_action_fav_normal);
        }
        viewHolder.favMark.setVisibility(View.GONE);
    /*    viewHolder.favMark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //收藏
                ShowToast("收藏");
                onClickFav(v, entity);

            }
        });*/
        return convertView;
    }

    private TencentShareEntity getQQShareEntity(OfficialDiandi diandi) {
        String title = "这里有好多有用的资源哦";
        String comment = "快来这里下载吧";
        String img = null;
        if (diandi.getContentfigureurl() != null) {
            img = diandi.getContentfigureurl().getFileUrl();
        } else {
            img = TencentShareConstants.DEFAULT_IMG_URL;
        }
        String summary = diandi.getContent() + diandi.getLink();
        TencentShareEntity entity = new TencentShareEntity(title, img, TencentShareConstants.WEB, summary, comment);
        entity.setTargetUrl(diandi.getLink());
        return entity;
    }

    private void onClickFav(View v, final DianDi DianDi) {
        User user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null && user.getSessionToken() != null) {
            BmobRelation favRelaton = new BmobRelation();
            DianDi.setMyFav(!DianDi.getMyFav());
            if (DianDi.getMyFav()) {
                ((ImageView) v).setImageResource(R.drawable.ic_action_fav_choose);
                favRelaton.add(DianDi);
                user.setFavorite(favRelaton);
                ShowToast("收藏成功。");
                user.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtilC.getInstance(mContext).insertFav(DianDi);
                        L.i(TAG, "收藏成功。");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        L.i(TAG, "收藏失败。请检查网络~");
                        ShowToast("收藏失败。请检查网络~" + arg0);
                    }
                });

            } else {
                ((ImageView) v).setImageResource(R.drawable.ic_action_fav_normal);
                favRelaton.remove(DianDi);
                user.setFavorite(favRelaton);
                ShowToast("取消收藏。");
                user.update(mContext, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        DatabaseUtilC.getInstance(mContext).deleteFav(DianDi);
                        L.i(TAG, "取消收藏。");
                        //try get fav to see if fav success
//						getMyFavourite();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        L.i(TAG, "取消收藏失败。请检查网络~");
                        ShowToast("取消收藏失败。请检查网络~" + arg0);
                    }
                });
            }
        }
    }

    public static class ViewHolder {
        public ImageView userLogo;
        public TextView userName;
        public TextView contentText;
        public ImageView contentImage;
        public TextView contentLink;

        public ImageView favMark;
        public TextView love;
        public TextView hate;
        public TextView share;
        public TextView comment;

    }
}
