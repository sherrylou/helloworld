package com.weds.tenedu.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
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


public class HomeTitleFragment extends BaseFragment {
    @Bind(R.id.tv_class_name)
    TextView tvClassName;
    @Bind(R.id.tv_text_name)
    TextView tvTextName;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.OBJ_BACK:
                    if (CalendarInterface.getInstence().getIsExam().equals("1")) {
                        tvTextName.setVisibility(View.VISIBLE);
                        SubCalendar currentCalendar = CalendarInterface.getInstence().getCurrentCalendar();
                        if (currentCalendar != null) {
                            tvClassName.setText(currentCalendar.getExamCenter());
                            tvTextName.setText(currentCalendar.getExam());
                        }
                    } else {
                        tvTextName.setVisibility(View.GONE);
                        tvClassName.setText(classRoom.getRoomName());
                    }
                    break;
            }
        }
    };
    private ClassRoom classRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ten_fragment_home_title, container, false);
        ButterKnife.bind(this, view);
        initData();
        tvClassName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UIHelper.toExamStandbyActivity(context);
            }
        });
        return view;
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.CLASS_INFO, getDataCallBackInterface);
    }

    //==============数据回调=============
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
