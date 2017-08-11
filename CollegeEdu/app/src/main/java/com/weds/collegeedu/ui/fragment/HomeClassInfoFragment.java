package com.weds.collegeedu.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.entity.ClassRoom;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 班级信息fragment
 */
public class HomeClassInfoFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.iv_college_logo)
    ImageView ivCollegeLogo;
    @Bind(R.id.tv_class_name)
    TextView tvClassName;
    @Bind(R.id.tv_class_contain_num)
    TextView tvClassContainNum;
    @Bind(R.id.tv_text_name)
    TextView tvTextName;

    private String mParam1;
    private String mParam2;

    private ClassRoom classRoom;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.OBJ_BACK:
                    if (classRoom != null) {
                        String isExam = CalendarInterface.getInstence().getIsExam();
                        SubCalendar currentCalendar = CalendarInterface.getInstence().getCurrentCalendar();
                        if (isExam.equals("1")) {
                            if (currentCalendar != null) {
                                tvTextName.setVisibility(View.VISIBLE);
                                tvTextName.setText(currentCalendar.getExam());
                                tvClassName.setText(currentCalendar.getExamCenter());
                            }
                        } else {
                            tvTextName.setVisibility(View.GONE);
                            tvClassName.setText(classRoom.getRoomName());
                        }
                        Log.i("classroom--", "--" + classRoom.getCapacity() + "---" + classRoom.getRoomName());
                        tvClassContainNum.setText(classRoom.getCapacity());
                    }
                    break;
            }
        }
    };

    /**
     */
    public static HomeClassInfoFragment newInstance() {
        HomeClassInfoFragment fragment = new HomeClassInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_home_class_info, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initView() {

    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.CLASS_INFO, getDataCallBackInterface);
    }

    //=========数据回调============
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
            if (data != null) {
                classRoom = (ClassRoom) data;
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
