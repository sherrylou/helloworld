package com.weds.tenedu.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.TextInfo;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.entity.AttendanceState;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.tenedu.adapter.BaseRecyclerAdapter;
import com.weds.tenedu.adapter.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExamStandbyFragment extends BaseFragment {

    @Bind(R.id.exam_list_rv)
    RecyclerView examListRv;
    @Bind(R.id.tv_text_room_name)
    TextView tvTextRoomName;
    @Bind(R.id.tv_text_subsuji)
    TextView tvTextSubsuji;
    @Bind(R.id.tv_text_name)
    TextView tvTextName;
    @Bind(R.id.tv_teacher_name)
    TextView tvTeacherName;
    @Bind(R.id.tv_should_be_num)
    TextView tvShouldBeNum;
    @Bind(R.id.tv_current_be_num)
    TextView tvCurrentBeNum;
    @Bind(R.id.tv_not_be_num)
    TextView tvNotBeNum;
    private BaseRecyclerAdapter mAdapter;
    private TextInfo textInfo;
    private List<SubCalendar> subCalendars = new ArrayList<>();
    private Context mContext;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.OBJ_BACK:
                    upDataView();
                    break;
            }
        }
    };

    /**
     * 更新数据
     */
    private void upDataView() {
        if (textInfo != null) {
            mAdapter.replaceList(textInfo.getTextList());
            SubCalendar curSub = textInfo.getCurSub();
            if (curSub != null) {
                tvTextName.setText(curSub.getName());
                tvTextSubsuji.setText("第"+curSub.getTextsuji()+"场");
                tvTextRoomName.setText(curSub.getExamRoom());
            }
            AttendanceState attendanceState = textInfo.getAttendanceState();
            if (attendanceState != null) {
                tvShouldBeNum.setText(attendanceState.getShouldNum());
                tvCurrentBeNum.setText(attendanceState.getCurrentNum());
                tvNotBeNum.setText(attendanceState.getTruantNum());
            }
            List<SchoolPerson> teacherList = textInfo.getTeacherList();
            if (teacherList != null && teacherList.size()>0) {
                String teacherName = "";
                for (SchoolPerson schoolPerson : teacherList) {
                    teacherName += schoolPerson.getName()+"   ";
                }
                tvTeacherName.setText(teacherName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ten_fragment_exam_standby_title, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        setExamListRv();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void setExamListRv() {
        mAdapter = new BaseRecyclerAdapter<SubCalendar>(mContext, subCalendars) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.ten_item_exam_list_rv;
            }

            @Override
            public void bindData(RecyclerView.ViewHolder holder, int position, SubCalendar item) {
                RecyclerViewHolder mHolder = (RecyclerViewHolder) holder;
                mHolder.setText(R.id.tv_text_amount,"第"+item.getTextsuji()+"场");
                mHolder.setText(R.id.tv_text_name,item.getName());
                mHolder.setText(R.id.tv_text_time,item.getStartTime()+"-"+item.getDownTime());
                SubCalendar currentCalendar = CalendarInterface.getInstence().getCurrentCalendar();
                if (currentCalendar != null && currentCalendar.getTextsuji().equals(item.getTextsuji())) {
                    mHolder.getView(R.id.ll_root).setBackgroundColor(getResources().getColor(R.color.Y10));
                }else {
                    mHolder.getView(R.id.ll_root).setBackgroundColor(Color.TRANSPARENT);
                }
            }
        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        examListRv.setLayoutManager(mLayoutManager);
        examListRv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
            }
        });
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.TEXT_INFO, getDataCallBackInterface);
    }

    //==========数据回调==========
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
            textInfo = ((TextInfo) data);
            handler.sendEmptyMessage(EventConfig.OBJ_BACK);
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
