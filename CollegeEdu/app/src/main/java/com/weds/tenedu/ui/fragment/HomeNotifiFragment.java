package com.weds.tenedu.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.LogUtils;

import com.dalong.marqueeview.MarqueeView;
import com.weds.collegeedu.R;
import com.weds.collegeedu.entity.Notification;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.tenedu.ui.NotificationActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeNotifiFragment extends BaseFragment {
    @Bind(R.id.mMarqueeView)
    MarqueeView mMarqueeView;
    private Context mContext;

    private List<Notification> notifications;

    /**
     * 当前播放的条数
     */
    private int count = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EventConfig.LIST_BACK:
                    count = 0;
                    startTextShowLoop(count);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ten_fragment_home_notification, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        if (notifications != null && notifications.size() > 0) {
            mMarqueeView.startScroll();
        }
    }

    /**
     * 初始化通知效果视图
     */
    private void initView() {
        mMarqueeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifications != null && notifications.size() > 0) {
                    mContext.startActivity(new Intent(mContext, NotificationActivity.class));
                }
            }
        });
        mMarqueeView.setOnMargueeListener(new MarqueeView.OnMargueeListener() {
            @Override
            public void onRollOver() {
                LogUtils.i("通知播放完毕回调","=================");
                //播放完毕回调
                count++;
                if (notifications != null && notifications.size() > 0) {
                    if (notifications.size() > count) {
                        startTextShowLoop(count);
                    } else {
                        count = 0;
                        startTextShowLoop(count);
                    }
                }
            }
        });
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.NOTICE, getDataCallBackInterface);
    }

    //======数据回调=========
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {
            if (data != null) {
                notifications = data;
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

    /**
     * 开始循环播放通知
     */
    private void startTextShowLoop(int index) {
        if (notifications != null) {
            LogUtils.i("通知更新长度", String.valueOf(notifications.size()));
        }
        if (notifications != null && notifications.size() > index) {
            mMarqueeView.setText(notifications.get(index).getContent().replace("\\r\\n","\n"));
            mMarqueeView.startScroll();
        }else{
            mMarqueeView.setText("   ");
//            mMarqueeView.stopScroll();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }
}
