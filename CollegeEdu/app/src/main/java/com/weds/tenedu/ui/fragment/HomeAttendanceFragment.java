package com.weds.tenedu.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.AttendanceInfo;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.tenedu.ui.AttendanceDetailActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.ATTENDANCE;

public class HomeAttendanceFragment extends BaseFragment {

    @Bind(R.id.course_name_tv)
    TextView courseNameTv;
    @Bind(R.id.teacher_tv)
    TextView teacherTv;
    @Bind(R.id.current_class_tv)
    TextView currentClassTv;
    @Bind(R.id.attendance_layout)
    LinearLayout attendanceLayout;
    @Bind(R.id.tv_should_here)
    TextView tvShouldHere;
    @Bind(R.id.tv_current_here)
    TextView tvCurrentHere;
    @Bind(R.id.tv_not_here)
    TextView tvNotHere;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;

    private Context mContext;

    /**
     * 数据
     */
    private AttendanceInfo info;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EventConfig.OBJ_BACK:
                    if (info != null) {
                        courseNameTv.setText(info.getSubName());
                        currentClassTv.setText("第" + info.getSubsuji() + "节");
                        List<SchoolPerson> teachers = info.getTeachers();
                        if (teachers != null && teachers.size() > 0) {
                            teacherTv.setText(teachers.get(0).getName());
                        }
                        tvShouldHere.setText(info.getShouldNum());
                        tvCurrentHere.setText(info.getCurrenntNum());
                        tvNotHere.setText(info.getNotHereNum());
                    }else {
                        courseNameTv.setText(" ");
                        currentClassTv.setText(" ");
                        teacherTv.setText(" ");
                        tvShouldHere.setText(" ");
                        tvCurrentHere.setText(" ");
                        tvNotHere.setText(" ");
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ten_fragment_home_attendance, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(ATTENDANCE, getDataCallBackInterface);
    }

    private void initView() {
        llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AttendanceDetailActivity.class));
            }
        });
    }

    //============数据回调===========
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
            if (data != null) {
                info = (AttendanceInfo) data;
                handler.sendEmptyMessage(EventConfig.OBJ_BACK);
            }
        }

        @Override
        public void LoadArchivesData() {

        }

        @Override
        public void SwipeCardShow(SchoolPerson userInfo, int result) {

        }

        @Override
        public void otherNotice(String type) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }
}
