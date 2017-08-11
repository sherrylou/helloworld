package com.weds.collegeedu.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.Strings;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.AttendanceInfo;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.ATTENDANCE;

/**
 * 首页出勤详情fragment
 */
public class HomeAttendanceFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.tv_sub_amount)
    TextView tvSubAmount;
    @Bind(R.id.tv_sub_name)
    TextView tvSubName;
    @Bind(R.id.tv_first_teach_name)
    TextView tvFirstTeachName;
    @Bind(R.id.tv_first_sec_name)
    TextView tvFirstSecName;
    @Bind(R.id.tv_should_here)
    TextView tvShouldHere;
    @Bind(R.id.tv_current_here)
    TextView tvCurrentHere;
    @Bind(R.id.tv_not_here)
    TextView tvNotHere;
    @Bind(R.id.ll_teacher)
    LinearLayout llTeacher;
    @Bind(R.id.ll_attendance_num)
    LinearLayout llAttendanceNum;
    @Bind(R.id.ll_attendance_class_names)
    LinearLayout llAttendanceClassNames;

    private AttendanceInfo attendanceInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.OBJ_BACK:
                    setAttendanceInfo(attendanceInfo);
                    break;
            }
        }
    };
    private String mParam1;
    private String mParam2;
    private Context context;


    public HomeAttendanceFragment() {
    }

    /**
     */
    // TODO: Rename and change types and number of parameters
    public static HomeAttendanceFragment newInstance() {
        HomeAttendanceFragment fragment = new HomeAttendanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_attendance, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initView() {

    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(ATTENDANCE, getDataCallBackInterface);
    }

    //=====数据回调======
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
//            if (data != null) {
            attendanceInfo = ((AttendanceInfo) data);
            handler.sendEmptyMessage(EventConfig.OBJ_BACK);
//            }
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

    /**
     * 设置出勤数据
     *
     * @param info
     */
    private void setAttendanceInfo(AttendanceInfo info) {
        tvCurrentHere.setText(info.getCurrenntNum());
        tvShouldHere.setText(info.getShouldNum());
        tvNotHere.setText(info.getNotHereNum());
        if (Strings.isNotEmpty(info.getIsText()) && info.getIsText().equals("1")) {
            if (Strings.isNotEmpty(info.getSubsuji())) {
                tvSubAmount.setText("第" + info.getSubsuji() + "场");
            } else {
                tvSubAmount.setText("");
            }
        } else {
            if (Strings.isNotEmpty(info.getSubsuji())) {
                tvSubAmount.setText("第" + info.getSubsuji() + "节");
            } else {
                tvSubAmount.setText("");
            }
        }
        if (Strings.isNotEmpty(info.getSubName())) {
            tvSubName.setText(info.getSubName());
        } else {
            tvSubName.setText("");
        }
        List<SchoolPerson> teachers = info.getTeachers();
        if (teachers != null) {
            if (teachers.size() > 0) {
                Log.i("档案回调信息教师姓名-----2", teachers.get(0).getName());
                tvFirstTeachName.setText(teachers.get(0).getName());
                llTeacher.setVisibility(View.VISIBLE);
            } else {
                tvFirstTeachName.setText("");
                llTeacher.setVisibility(View.GONE);
            }
            if (teachers.size() > 1) {
                Log.i("档案回调信息教师姓名-----3", teachers.get(1).getName());
                tvFirstSecName.setText(teachers.get(1).getName());
                llTeacher.setVisibility(View.VISIBLE);
            } else {
                tvFirstSecName.setText("");
            }
        } else {
            tvFirstTeachName.setText("");
            tvFirstSecName.setText("");
            llTeacher.setVisibility(View.GONE);
        }

        //添加班级名称
        llAttendanceClassNames.removeAllViews();
        String className = info.getClassName();
        if (Strings.isNotEmpty(className)) {
            String[] names = className.split(" ");
            for (String name : names) {
                TextView textView = new TextView(getContext());
                textView.setText(name);
                textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.size_16));
                textView.setTextColor(getResources().getColor(R.color.C1));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setLayoutParams(lp);
                llAttendanceClassNames.addView(textView);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }
}
