package com.weds.tenedu.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.Strings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.entity.Notification;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.ui.BAwakeActivity;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.tenedu.adapter.BaseRecyclerAdapter;
import com.weds.tenedu.adapter.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationActivity extends BAwakeActivity implements GetDataCallBackInterface {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.notification_rv)
    RecyclerView notificationRv;
    @Bind(R.id.notification_details_content_tv)
    TextView notificationDetailsContentTv;
    @Bind(R.id.notification_sv)
    ScrollView notificationSv;
    @Bind(R.id.activity_notification_details)
    LinearLayout activityNotificationDetails;
    @Bind(R.id.tv_content)
    TextView tvContent;
    private BaseRecyclerAdapter mAdapter;
    private List<Notification> list = new ArrayList<>();
    private int pageIndex = 0;
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
                    mIsRefreshing = true;
                    mAdapter.replaceList(list);
                    handler.sendEmptyMessageDelayed(CAN_REFRESH, 500);
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
        setContentView(R.layout.ten_activity_notification);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        tvTitle.setText("通知通告");
        setNotificationListView();
    }

    private void setNotificationListView() {
        mAdapter = new BaseRecyclerAdapter<Notification>(this, list) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.ten_item_notification_rv;
            }

            @Override
            public void bindData(RecyclerView.ViewHolder holder, int position, Notification item) {
                RecyclerViewHolder mHolder = (RecyclerViewHolder) holder;
                if (item != null) {
                    mHolder.setText(R.id.tv_title, "");
                    mHolder.setText(R.id.tv_from, item.getFrom());
                    String startTime = item.getStartTime();
                    if (Strings.isNotEmpty(startTime) && startTime.length() > 10) {
                        mHolder.setText(R.id.tv_date, startTime.substring(0, 10));
                    }
                    mHolder.setText(R.id.tv_content, item.getContent().replace("\\r\\n", "\n"));
                }
            }
        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        notificationRv.setLayoutManager(mLayoutManager);
        notificationRv.setAdapter(mAdapter);
        notificationRv.setOnTouchListener(
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
                List<Notification> notificationList = (List<Notification>) mAdapter.mData;
                if (notificationList != null && notificationList.size() > pos) {
                    setShowNotificationDetailsView(notificationList.get(pos));
                }
            }
        });
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(NOTICE, this);
    }

    /**
     * 显示通知列表
     */
    private void setShowNotificationListView() {
        pageIndex = 0;
        notificationRv.setVisibility(View.VISIBLE);
        notificationSv.setVisibility(View.GONE);
    }

    /**
     * 显示通知详情
     *
     * @param notification 要显示的通知
     */
    private void setShowNotificationDetailsView(Notification notification) {
        pageIndex = 1;
        notificationRv.setVisibility(View.GONE);
        notificationSv.setVisibility(View.VISIBLE);
        if (notification != null) {
            tvContent.setText(notification.getContent().replace("\\r\\n", "\n"));
        }
    }

    @OnClick({R.id.iv_back})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back:
                if (pageIndex == 0) {
                    AppManager.getInstance().finishActivity(this);
                } else {
                    setShowNotificationListView();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
        ButterKnife.unbind(this);
    }

    //============数据回调==============
    @Override
    public void backListSuccess(List data) {
        if (data != null && handler != null) {
            list = data;
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
}
