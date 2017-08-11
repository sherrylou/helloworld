package com.weds.tenedu.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.LogUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.entity.CourseTable;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.thread.MessageEvent;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.tenedu.adapter.BaseRecyclerAdapter;
import com.weds.tenedu.adapter.RecyclerViewHolder;
import com.weds.tenedu.view.ObliqueLineView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.TABLE_COURSE;

public class CourseTableDetailsActivity extends StandByActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.line)
    ObliqueLineView line;
    @Bind(R.id.week_one)
    TextView weekOne;
    @Bind(R.id.course_table_rv)
    RecyclerView courseTableRv;
    @Bind(R.id.activity_course_table_details)
    LinearLayout activityCourseTableDetails;

    private BaseRecyclerAdapter mAdapter;
    private Map<Integer, List<SubCalendar>> subMap = new HashMap<>();
    private List<CourseTable> courseTables = new ArrayList<>();
    private final int CAN_REFRESH = 400;
    /**
     * 是否可以滑动
     */
    private boolean mIsRefreshing = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.LIST_BACK:
                    if (courseTables != null && courseTables.size() > 0) {
                        mIsRefreshing = true;
                        mAdapter.replaceList(courseTables);
                        handler.sendEmptyMessageDelayed(CAN_REFRESH, 500);
                    } else {
                        AppManager.getInstance().finishActivity(CourseTableDetailsActivity.this);
                    }
                    break;
                case CAN_REFRESH:
                    mIsRefreshing = false;
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_activity_course_table_details);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("dasdasdasd", "dadasdasdasdas");
    }

    private void initView() {
        tvTitle.setText("课程表");
        setCourseTableRv();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        line.setXY(weekOne.getMeasuredWidth(), weekOne.getMeasuredHeight());
    }

    /**
     * 设置课表视图
     */
    private void setCourseTableRv() {
        mAdapter = new BaseRecyclerAdapter<CourseTable>(this, courseTables) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.ten_item_course_table_rv;
            }

            @Override
            public void bindData(RecyclerView.ViewHolder holder, int position, CourseTable item) {
                RecyclerViewHolder mHolder = (RecyclerViewHolder) holder;
                mHolder.getTextView(R.id.item_course_index_tv).setText("第" + (position + 1) + "节");
                mHolder.getTextView(R.id.course_name_one_tv).setText(item.getWeek_1_courseName() != null ? item.getWeek_1_courseName() : "-");
                mHolder.getTextView(R.id.course_name_two_tv).setText(item.getWeek_2_courseName() != null ? item.getWeek_2_courseName() : "-");
                mHolder.getTextView(R.id.course_name_three_tv).setText(item.getWeek_3_courseName() != null ? item.getWeek_3_courseName() : "-");
                mHolder.getTextView(R.id.course_name_four_tv).setText(item.getWeek_4_courseName() != null ? item.getWeek_4_courseName() : "-");
                mHolder.getTextView(R.id.course_name_five_tv).setText(item.getWeek_5_courseName() != null ? item.getWeek_5_courseName() : "-");
                mHolder.getTextView(R.id.course_name_six_tv).setText(item.getWeek_6_courseName() != null ? item.getWeek_6_courseName() : "-");
                mHolder.getTextView(R.id.course_name_seven_tv).setText(item.getWeek_7_courseName() != null ? item.getWeek_7_courseName() : "-");
            }
        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        courseTableRv.setLayoutManager(mLayoutManager);
        courseTableRv.setAdapter(mAdapter);
        courseTableRv.setOnTouchListener(
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
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(TABLE_COURSE, getDataCallBackInterface);
    }

    @OnClick({R.id.iv_back})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back:
                AppManager.getInstance().finishActivity(this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().postSticky(new MessageEvent("finishMyLoading"));
        handler.removeCallbacksAndMessages(null);
        handler = null;
        ButterKnife.unbind(this);
    }

    //===========数据回调===========
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {
            if (data != null && handler != null) {
                courseTables = data;
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
}
