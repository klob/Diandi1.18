package com.diandi.demo.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.diandi.demo.R;
import com.diandi.demo.db.PlanDao;
import com.diandi.demo.model.Plan;
import com.diandi.demo.util.OverridePendingUtil;
import com.diandi.demo.widget.HeaderLayout;

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

public class NoteActivity extends ActivityBase {

    private EditText mNoteEdit;
    private ImageView mDoneImg;
    private Plan mPlan;
    private PlanDao mPlanDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
    }

    @Override
    void findView() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_note);
        initTopBarForBoth("写记事", R.drawable.base_action_bar_true_bg_selector, new HeaderLayout.onRightImageButtonClickListener() {
            @Override
            public void onClick() {
                saveNote();
            }
        });
        mNoteEdit = (EditText) findViewById(R.id.activity_note_edit);
        mDoneImg = (ImageView) findViewById(R.id.activity_note_done_img);

    }

    @Override
    void initView() {
        mPlanDao = new PlanDao(this);
        int mPlanId = getIntent().getIntExtra(Plan.PLAN_ID, 0);
        mPlan = mPlanDao.getPlanById(mPlanId);
        mNoteEdit.setText(mPlan.getNote());

        bindEvent();
    }

    @Override
    void bindEvent() {
        mDoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }

    @Override
    public void onBackPressed() {
        ShowToast("保存成功");
        mPlan.setNote(mNoteEdit.getText().toString());
        mPlanDao.createPlan(mPlan);
        super.onBackPressed();
    }

    private void saveNote() {
        ShowToast("保存成功");
        mPlan.setNote(mNoteEdit.getText().toString());
        mPlanDao.createPlan(mPlan);
        finish();
        OverridePendingUtil.out(NoteActivity.this);

    }
}
