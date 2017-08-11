package com.weds.tenedu.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.LogUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.entity.ClassUser;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.ui.BAwakeActivity;
import com.weds.collegeedu.utils.TextViewHignLightUtil;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.tenedu.adapter.AttendanceItemDecoration;
import com.weds.tenedu.adapter.BaseRecyclerAdapter;
import com.weds.tenedu.adapter.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AttendanceDetailActivity extends BAwakeActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.due_text_tv)
    TextView dueTextTv;
    @Bind(R.id.due_number_tv)
    TextView dueNumberTv;
    @Bind(R.id.due_layout)
    LinearLayout dueLayout;
    @Bind(R.id.actually_text_tv)
    TextView actuallyTextTv;
    @Bind(R.id.actually_number_tv)
    TextView actuallyNumberTv;
    @Bind(R.id.actually_layout)
    LinearLayout actuallyLayout;
    @Bind(R.id.non_arrival_text_tv)
    TextView nonArrivalTextTv;
    @Bind(R.id.non_arrival_number_tv)
    TextView nonArrivalNumberTv;
    @Bind(R.id.non_arrival_layout)
    LinearLayout nonArrivalLayout;
    @Bind(R.id.attendance_rv)
    RecyclerView attendanceRv;
    @Bind(R.id.introduce_tv)
    TextView introduceTv;
    @Bind(R.id.list_layout)
    LinearLayout listLayout;
    @Bind(R.id.activity_notification_details)
    LinearLayout activityNotificationDetails;
    @Bind(R.id.iv_student_head)
    ImageView ivStudentHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_major)
    TextView tvMajor;
    @Bind(R.id.tv_class_name)
    TextView tvClassName;
    @Bind(R.id.tv_normal)
    TextView tvNormal;
    @Bind(R.id.tv_leave)
    TextView tvLeave;
    @Bind(R.id.tv_truant)
    TextView tvTruant;
    @Bind(R.id.tv_leave_early)
    TextView tvLeaveEarly;
    private BaseRecyclerAdapter mAdapter;
    private List<ClassUser> list = new ArrayList<>();
    private TextView[] textTvs = new TextView[3];
    private TextView[] numberTvs = new TextView[3];

    private AttendanceItemDecoration attendanceItemDecoration;

    private LinearLayout student_layout;

    private List<List<ClassUser>> lists = new ArrayList<>();
    private List<ClassUser> shouldPersons = new ArrayList<>();
    private List<ClassUser> curPersons = new ArrayList<>();
    private List<ClassUser> truantPersons = new ArrayList<>();
    private SchoolPerson schoolPerson;

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
                    if (lists != null && lists.size() > 2) {
                        shouldPersons = lists.get(0);
                        curPersons = lists.get(1);
                        truantPersons = lists.get(2);
                        setNumBerTv(shouldPersons, 0);
                        setNumBerTv(curPersons, 1);
                        setNumBerTv(truantPersons, 2);
                        mIsRefreshing = true;
                        elongateView(index);
                        handler.sendEmptyMessageDelayed(CAN_REFRESH, 500);
                    }
                    break;
                case EventConfig.OBJ_BACK:
                    setStuAttendanceDetails();
                    break;
                case CAN_REFRESH:
                    mIsRefreshing = false;
                    break;
            }
        }
    };
    private SettingMotionDialog loadingDialog;

    /**
     * 设置选中学生具体出勤情况
     */
    private void setStuAttendanceDetails() {
        if (schoolPerson != null) {
            //结果 0--->正常 1--->请假 2---> 旷课 3---->早退
            switch (schoolPerson.getAttendanceState()) {
                case 0:
                    tvNormal.setBackgroundColor(getResources().getColor(R.color.color_red_ff3300));
                    break;
                case 1:
                    tvLeave.setBackgroundColor(getResources().getColor(R.color.color_red_ff3300));
                    break;
                case 2:
                    tvTruant.setBackgroundColor(getResources().getColor(R.color.color_red_ff3300));
                    break;
                case 3:
                    tvLeaveEarly.setBackgroundColor(getResources().getColor(R.color.color_red_ff3300));
                    break;
            }
            WedsDataUtils.setLocalImg(ivStudentHead, schoolPerson);
            tvClassName.setText(schoolPerson.getClassName());
            tvMajor.setText(schoolPerson.getMajor());
        }
    }

    /**
     * 当前所选列表(应到, 实到，未到)
     * 默认实到
     */
    private int index = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_activity_attendance_detail);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {

        tvTitle.setText("出勤详情");

        student_layout = (LinearLayout) findViewById(R.id.student_info_layout);

        //设置数组view，方便动态操作
        textTvs[0] = dueTextTv;
        textTvs[1] = actuallyTextTv;
        textTvs[2] = nonArrivalTextTv;
        numberTvs[0] = dueNumberTv;
        numberTvs[1] = actuallyNumberTv;
        numberTvs[2] = nonArrivalNumberTv;

        setAttendanceRv();
        //默认实到界面
        elongateView(0);
    }

    /**
     * 设置出勤明细的列表视图
     */
    private void setAttendanceRv() {
        mAdapter = new BaseRecyclerAdapter<ClassUser>(this, truantPersons) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.ten_item_attendance_rv;
            }

            @Override
            public void bindData(RecyclerView.ViewHolder holder, int position, final ClassUser item) {
                RecyclerViewHolder mHolder = (RecyclerViewHolder) holder;
                if (item != null) {
                    mHolder.setText(R.id.tv_name, item.getName());
                    WedsDataUtils.setLocalImg(mHolder.getImageView(R.id.iv_head_img), item.getPersonNo(), "4");
                    mHolder.getView(R.id.ll_root).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showStudentAttendanceDetails(item);
                        }
                    });
                }
            }
        };
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        attendanceRv.setLayoutManager(staggeredGridLayoutManager);
        if (attendanceItemDecoration == null) {
            attendanceItemDecoration = new AttendanceItemDecoration(20);
            attendanceRv.addItemDecoration(attendanceItemDecoration);
        }
        attendanceRv.setAdapter(mAdapter);
        attendanceRv.setOnTouchListener(
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

            }
        });
    }

    private ClassUser priorClassUser;

    /**
     * 显示所选学生详情
     *
     * @param classUser
     */
    private void showStudentAttendanceDetails(ClassUser classUser) {
        LogUtils.i("显示单个人的出勤详情", classUser.toString());
        if (priorClassUser == null || !priorClassUser.getPersonNo().equals(classUser.getPersonNo())) {
            //跟上一次选中的不是一个人，需要初始化控件
            //初始状态
            ivStudentHead.setImageResource(R.mipmap.mini_avatar);
            tvName.setText(classUser.getName());
            tvMajor.setText("");
            tvClassName.setText("");
            tvNormal.setBackgroundColor(getResources().getColor(R.color.color_blue_3a90ff));
            tvLeave.setBackgroundColor(getResources().getColor(R.color.color_blue_3a90ff));
            tvTruant.setBackgroundColor(getResources().getColor(R.color.color_blue_3a90ff));
            tvLeaveEarly.setBackgroundColor(getResources().getColor(R.color.color_blue_3a90ff));
            WedsDataUtils.getInstance().getDataWithParam(GetDataCallBackInterface.SCHOOL_PERSON_ATTENDANCE_INFO, getDataCallBackInterface, classUser.getPersonNo());
        }
        priorClassUser = classUser;
        student_layout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);
    }

    private void initData() {
        loadingDialog = SettingMotionDialog.getInstance();
        loadingDialog.showLoadingDialog(AttendanceDetailActivity.this, "正在加载中，请稍后...");
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.ATTENDANCE_DETAILS, getDataCallBackInterface);
    }

    //======数据回调=========
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {
            loadingDialog.dismissLoadingDialog();
            if (data != null && handler!=null) {
                lists = data;
                handler.sendEmptyMessage(EventConfig.LIST_BACK);
            }
        }

        @Override
        public void backObjectSuccess(Object data) {
            loadingDialog.dismissLoadingDialog();
            if (data != null && data instanceof SchoolPerson && handler!=null) {
                schoolPerson = ((SchoolPerson) data);
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

    @OnClick({R.id.due_layout, R.id.actually_layout, R.id.non_arrival_layout, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.due_layout:
                elongateView(0);
                break;
            case R.id.actually_layout:
                elongateView(1);
                break;
            case R.id.non_arrival_layout:
                elongateView(2);
                break;
            case R.id.iv_back:
                if (student_layout.getVisibility() == View.VISIBLE) {
                    student_layout.setVisibility(View.GONE);
                    listLayout.setVisibility(View.VISIBLE);
                } else {
                    AppManager.getInstance().finishActivity(this);
                }
                break;
        }
    }

    /**
     * 加长选中的视图效果，动态切换，未选中的变短
     *
     * @param index
     */
    private void elongateView(int index) {
        this.index = index;
        LogUtils.i("出勤详情", index + "--" + shouldPersons.size() + "---" + curPersons.size() + "---" + truantPersons.size());
        for (int i = 0; i < textTvs.length; i++) {
            if (i != index) {
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(50, 80);
                textTvs[i].setLayoutParams(textParams);
                LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(80, 80);
                numberTvs[i].setLayoutParams(numberParams);
            } else {
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(60, 80);
                textTvs[index].setLayoutParams(textParams);
                LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(100, 80);
                numberTvs[index].setLayoutParams(numberParams);
            }
        }
        switch (index) {
            case 0:
                mAdapter.replaceList(shouldPersons);
                setNumBerTv(shouldPersons, 0);
                introduceTv.setText(TextViewHignLightUtil.matcherSearchTitle(getResources().getColor(R.color.color_red_ff3300), "当日应到人数" + shouldPersons.size() + "人", String.valueOf(shouldPersons.size())));
                break;
            case 1:
                mAdapter.replaceList(curPersons);
                setNumBerTv(curPersons, 1);
                introduceTv.setText(TextViewHignLightUtil.matcherSearchTitle(getResources().getColor(R.color.color_red_ff3300), "当日实到人数" + curPersons.size() + "人", String.valueOf(curPersons.size())));
                break;
            case 2:
                mAdapter.replaceList(truantPersons);
                setNumBerTv(truantPersons, 2);
                introduceTv.setText(TextViewHignLightUtil.matcherSearchTitle(getResources().getColor(R.color.color_red_ff3300), "当日未到人数" + truantPersons.size() + "人", String.valueOf(truantPersons.size())));
                break;
        }
    }

    /**
     * 设置左侧人数
     *
     * @param classUsers
     */
    private void setNumBerTv(List<ClassUser> classUsers, int type) {
        if (classUsers != null) {
            switch (type) {
                case 0:
                    LogUtils.i("出勤详情异常",dueNumberTv+"----"+shouldPersons);
                    String shouldNum = "0";
                    if (shouldPersons != null) {
                        shouldNum = shouldPersons.size()+"";
                    }
                    dueNumberTv.setText(String.valueOf(shouldNum));
                    break;
                case 1:
                    String actuallyNum = "0";
                    if (curPersons != null) {
                        actuallyNum = curPersons.size()+"";
                    }
                    actuallyNumberTv.setText(actuallyNum);
                    break;
                case 2:
                    String actuallyNumber = "0";
                    if (truantPersons != null) {
                        actuallyNumber = truantPersons.size()+"";
                    }
                    nonArrivalNumberTv.setText(actuallyNumber);
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i("出勤详情界面","=======onDestroy========");
        handler.removeCallbacksAndMessages(null);
        handler = null;
        ButterKnife.unbind(this);
    }
}
