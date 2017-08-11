package com.weds.tenedu.ui;

import android.os.Bundle;
import android.weds.lip_library.ui.*;


import com.weds.collegeedu.R;
import com.weds.collegeedu.ui.BAwakeActivity;
import com.weds.tenedu.adapter.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 考试待机界面
 */
public class ExamStandbyActivity extends BAwakeActivity {
    private BaseRecyclerAdapter mAdapter;
    private List<String> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_activity_exam_standby);
    }
}
