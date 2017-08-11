package com.weds.collegeedu.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.collegeedu.widget.ControlRvSpeedLinearLayoutManager;
import com.weds.tenedu.adapter.BaseRecyclerAdapter;
import com.weds.tenedu.adapter.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 课表fragment
 */
public class HomeTableFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.tv_sub_type)
    TextView tvSubType;
    @Bind(R.id.tv_sub_name)
    TextView tvSubName;
    @Bind(R.id.rlv_table)
    RecyclerView rlvTable;

    private String mParam1;
    private String mParam2;
    private List<SubCalendar> subCalendars = new ArrayList<>();

    private final int ROLL_END = 123;
    private final int ROLL_RESET = 125;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.LIST_BACK:
                    for (SubCalendar subCalendar : subCalendars) {
                        Log.i("更新课表数据", subCalendar.toString());
                    }
                    boolean isRoll = upDataList(subCalendars);
                    handler.removeCallbacksAndMessages(null);
                    if (isRoll) {
                        handler.sendEmptyMessageDelayed(ROLL_END, 1000);
                    }
                    break;
                case ROLL_END:
                    rlvTable.smoothScrollToPosition(subCalendars.size());
                    break;
                case ROLL_RESET:
                    rlvTable.scrollToPosition(0);
                    handler.sendEmptyMessageDelayed(ROLL_END, 2000);
                    break;
            }
        }
    };
    private BaseRecyclerAdapter<SubCalendar> homeTableRlvAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeTableFragment newInstance() {
        HomeTableFragment fragment = new HomeTableFragment();
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
        View view = inflater.inflate(R.layout.fragment_home_table, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.SUB_CALENDAR, getDataCallBackInterface);
    }

    //========数据回调===========
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {
//            if (data != null && data.size() > 0) {
            subCalendars = data;
            handler.sendEmptyMessage(EventConfig.LIST_BACK);
//            }
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

    /**
     * 更新list数据
     *
     * @param data 课程list
     */
    private boolean upDataList(List<SubCalendar> data) {
        boolean isRoll = true;
        handler.removeCallbacksAndMessages(null);//除去所有之前线程

        if (CalendarInterface.getInstence().getIsExam().equals("1")) {
            LogUtils.i("课程界面黑屏", "=======222======");
            tvSubType.setText("场次");
            tvSubName.setText("科目");
            WedsDataUtils.getInstance().switchFileIndex(EventConfig.CURRENT_TEXT_DAY);
        } else {
            LogUtils.i("课程界面黑屏", "=======3333======");
            tvSubType.setText("课节");
            tvSubName.setText("课程");
            WedsDataUtils.getInstance().switchFileIndex(EventConfig.CURRENT_SUB_DAY);
        }

        LogUtils.i("ListView数据更新", String.valueOf(data.size()));
        if (data.size() < 6) {
            isRoll = false;
            for (int i = 0; i < 6 - data.size(); i++) {
                data.add(new SubCalendar());
            }
        }
        homeTableRlvAdapter.replaceList(data);
        return isRoll;
    }

    private void initView() {

        String isExam = CalendarInterface.getInstence().getIsExam();
        if (isExam.equals("1")) {
            tvSubType.setText("场次");
            tvSubName.setText("科目");
            WedsDataUtils.getInstance().switchFileIndex(EventConfig.CURRENT_TEXT_DAY);
        } else {
            tvSubType.setText("课节");
            tvSubName.setText("课程");
            WedsDataUtils.getInstance().switchFileIndex(EventConfig.CURRENT_SUB_DAY);
        }

//        if (subCalendars.size() < 5) {
//            for (int i = 0; i < 5 - subCalendars.size(); i++) {
//                subCalendars.add(new SubCalendar());
//            }
//        }

        homeTableRlvAdapter = new BaseRecyclerAdapter<SubCalendar>(context, subCalendars) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.home_table_item;
            }

            @Override
            public void bindData(RecyclerView.ViewHolder holder, int position, SubCalendar item) {
                RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
                String isExam = CalendarInterface.getInstence().getIsExam();
                if (isExam.equals("0")) {
                    recyclerViewHolder.setText(R.id.tv_sub_amount, item.getSubsuji());
                } else {
                    recyclerViewHolder.setText(R.id.tv_sub_amount, item.getTextsuji());
                }
                Log.i("课程节次", item.getSubsuji());
                if (Strings.isEmpty(item.getName())&&(Strings.isNotEmpty(item.getSubsuji()) || Strings.isNotEmpty(item.getTextsuji()))){
                    recyclerViewHolder.setText(R.id.tv_sub_name, "空闲");
                }else {
                    recyclerViewHolder.setText(R.id.tv_sub_name, item.getName());
                }

                if (Strings.isNotEmpty(item.getSubsuji()) || Strings.isNotEmpty(item.getTextsuji())) {
                    recyclerViewHolder.setText(R.id.tv_time, item.getStartTime() + "-" + item.getDownTime());
                }
            }
        };
        ControlRvSpeedLinearLayoutManager controlRvSpeedLinearLayoutManager = new ControlRvSpeedLinearLayoutManager(context, new ControlRvSpeedLinearLayoutManager.StopScrollCallBack() {
            @Override
            public void scrollStop(int position) {
                handler.sendEmptyMessageDelayed(ROLL_RESET, 3000);
            }
        }, ControlRvSpeedLinearLayoutManager.EXTREMELY_SLOW);
        rlvTable.setLayoutManager(controlRvSpeedLinearLayoutManager);
        rlvTable.setAdapter(homeTableRlvAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }
}
