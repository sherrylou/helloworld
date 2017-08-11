package com.weds.tenedu.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;

import com.weds.collegeedu.R;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.entity.CourseTimeTable;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.tenedu.adapter.BaseRecyclerAdapter;
import com.weds.tenedu.adapter.RecyclerViewHolder;
import com.weds.tenedu.ui.CourseTableDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeTableFragment extends BaseFragment {
    private Context mContext;
    private BaseRecyclerAdapter mAdapter;
    @Bind(R.id.table_rv)
    RecyclerView tableRv;

    private List<CourseTimeTable> list = new ArrayList<>();
    private List<SubCalendar> subCalendars = new ArrayList<>();

    private final int CAN_REFRESH = 400;
    /**
     * 是否可以滑动
     */
    private boolean mIsRefreshing = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EventConfig.LIST_BACK:
                    mIsRefreshing = true;
                    mAdapter.replaceList(subCalendars);
                    handler.sendEmptyMessageDelayed(CAN_REFRESH, 500);
                    break;
                case CAN_REFRESH:
                    mIsRefreshing = false;
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ten_fragment_course_timetable, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        setCourseTimeTable();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.SUB_CALENDAR, getDataCallBackInterface);
    }

    private void setCourseTimeTable() {
        mAdapter = new BaseRecyclerAdapter<SubCalendar>(mContext, subCalendars) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.ten_item_home_course_timetable;
            }

            @Override
            public void bindData(RecyclerView.ViewHolder holder, int position, SubCalendar item) {
                RecyclerViewHolder mHolder = (RecyclerViewHolder) holder;
                if (CalendarInterface.getInstence().getIsExam().equals("0")){
                    mHolder.setText(R.id.item_course_index_tv,item.getSubsuji());
                }else{
                    mHolder.setText(R.id.item_course_index_tv,item.getTextsuji());
                }
                mHolder.getTextView(R.id.item_course_name_tv).setText(item.getName());
            }
        };
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        tableRv.setLayoutManager(mLayoutManager);
        tableRv.setAdapter(mAdapter);
        tableRv.setOnTouchListener(
                //防止数据替换时滑动报错,所以在数据替换时禁止滑动
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (mIsRefreshing) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                mContext.startActivity(new Intent(mContext, CourseTableDetailsActivity.class));
            }
        });

    }

    //=============数据回调=============
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {
            if (data != null) {
                subCalendars = data;
                handler.sendEmptyMessage(EventConfig.LIST_BACK);
            }
        }

        @Override
        public void backObjectSuccess(Object data) {

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
