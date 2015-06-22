package com.diandi.demo.ui.activity;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.diandi.demo.R;
import com.diandi.demo.model.User;
import com.diandi.demo.model.diandi.DianDi;
import com.diandi.demo.util.CacheUtils;
import com.diandi.demo.util.L;
import com.diandi.demo.util.OverridePendingUtil;
import com.diandi.demo.widget.HeaderLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

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
public class NewDiandiActivity extends ActivityBase implements OnClickListener {


    private static final int REQUEST_CODE_ALBUM = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    EditText content;

    LinearLayout openLayout;
    LinearLayout takeLayout;

    ImageView albumPic;
    ImageView takePic;
    String dateTime;
    String targeturl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();

    }

    protected void findView() {
        setContentView(R.layout.activity_new_diandi);
        content = (EditText) findViewById(R.id.edit_content);

        openLayout = (LinearLayout) findViewById(R.id.open_layout);
        takeLayout = (LinearLayout) findViewById(R.id.take_layout);

        albumPic = (ImageView) findViewById(R.id.open_pic);
        takePic = (ImageView) findViewById(R.id.take_pic);
    }

    protected void initView() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initTopBarForBoth("记下点滴", R.drawable.base_action_bar_true_bg_selector, new HeaderLayout.onRightImageButtonClickListener() {
            @Override
            public void onClick() {
                String commitContent = content.getText().toString().trim();
                if (TextUtils.isEmpty(commitContent)) {
                    ShowToast("内容不能为空");
                    return;
                }
                if (targeturl == null) {
                    publish(commitContent, null, true);

                } else {
                    publish(commitContent);

                }
                finish();
                OverridePendingUtil.out(NewDiandiActivity.this);
            }
        });
        bindEvent();
    }

    protected void bindEvent() {
        openLayout.setOnClickListener(this);
        takeLayout.setOnClickListener(this);
        albumPic.setOnClickListener(this);
        takePic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.open_layout:
                Date date1 = new Date(System.currentTimeMillis());
                dateTime = date1.getTime() + "";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                break;
            case R.id.take_layout:
                Date date = new Date(System.currentTimeMillis());
                dateTime = date.getTime() + "";
                File f = new File(CacheUtils.getCacheDirectory(this, true, "pic") + dateTime);
                if (f.exists()) {
                    f.delete();
                }
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(f);
                Log.e("uri", uri + "");

                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(camera, REQUEST_CODE_CAMERA);
                break;
            default:
                break;
        }
    }

    /*
     * 发表带图片
     */
    private void publish(final String commitContent) {

        final BmobFile figureFile = new BmobFile(new File(targeturl));
        figureFile.upload(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                L.i(TAG, "上传文件成功。" + figureFile.getFileUrl());
                publish(commitContent, figureFile, false);
            }

            @Override
            public void onProgress(Integer arg0) {

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                L.i(TAG, "上传文件失败。" + arg1);
            }
        });

    }

    private void publish(final String commitContent, final BmobFile figureFile, boolean isPrivate) {
        User user = BmobUser.getCurrentUser(this, User.class);

        final DianDi qiangYu = new DianDi();
        if (figureFile != null) {
            qiangYu.setContentfigureurl(figureFile);
        }
        qiangYu.setAuthor(user);
        qiangYu.setContent(commitContent);
        qiangYu.setLove(0);
        qiangYu.setHate(0);
        qiangYu.setShare(0);
        qiangYu.setComment(0);
        qiangYu.setPass(true);
        qiangYu.save(this, new SaveListener() {

            @Override
            public void onSuccess() {
                ShowToast("发表成功！");
                L.i(TAG, "创建成功。");
                setResult(RESULT_OK);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ShowToast("发表失败！yg" + arg1);
                L.i(TAG, "创建失败。" + arg1);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.i(TAG, "get album:");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    String fileName = null;
                    if (data != null) {
                        Uri originalUri = data.getData();
                        ContentResolver cr = getContentResolver();
                        Cursor cursor = cr.query(originalUri, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            do {
                                fileName = cursor.getString(cursor.getColumnIndex("_data"));
                                L.i(TAG, "get album:" + fileName);
                            } while (cursor.moveToNext());
                        }
                        Bitmap bitmap = compressImageFromFile(fileName);
                        targeturl = saveToSdCard(bitmap);
                        albumPic.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        takeLayout.setVisibility(View.GONE);
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    String files = CacheUtils.getCacheDirectory(this, true, "pic") + dateTime;
                    File file = new File(files);
                    if (file.exists()) {
                        Bitmap bitmap = compressImageFromFile(files);
                        targeturl = saveToSdCard(bitmap);
                        takePic.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        openLayout.setVisibility(View.GONE);
                    } else {

                    }
                    break;
                default:
                    break;
            }
        }
    }

    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//		return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
        return bitmap;
    }

    public String saveToSdCard(Bitmap bitmap) {
        String files = CacheUtils.getCacheDirectory(this, true, "pic") + dateTime + "_11";
        File file = new File(files);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        L.i(TAG, file.getAbsolutePath());
        return file.getAbsolutePath();
    }


}
