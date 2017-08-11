package com.weds.collegeedu.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.entity.Notification;
import com.weds.collegeedu.entity.Regular;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.collegeedu.view.VerticalMarqueeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.NOTICE;

/**
 * 首页通知
 */
public class HomeTextNotifiFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.vertical_marquee_view)
    VerticalMarqueeView verticalMarqueeView;
    @Bind(R.id.tv_notify_date)
    TextView tvNotifyDate;
    @Bind(R.id.tv_notify_form)
    TextView tvNotifyForm;
    //==============通知滚动线程==============
    private String mParam1;
    private String mParam2;
    private List<Notification> notice = new ArrayList<>();
    private int count;
    /**
     * 防止退出线程未销毁异常
     */
    private boolean isShow = true;
    private Regular regular;
    private final int NOTICE_SCROLL = 111;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isShow) {//防止退出线程未销毁异常
                switch (msg.what) {
                    case EventConfig.LIST_BACK:
                        count = 0;
                        startTextShowLoop(count);
                        break;
                    case NOTICE_SCROLL:
                        break;
                }
            }
        }
    };

    /**
     * 开始循环播放通知
     */
    private void startTextShowLoop(int index) {
        if (notice != null && notice.size() > index) {
            Log.i("noticelxy", "---:"+notice.size()+"---"+index);
            verticalMarqueeView.stopScroll();
            String replace = notice.get(index).getContent().replace("\\r\\n", "\n");
            verticalMarqueeView.setText(replace);
            verticalMarqueeView.startScroll(true);
            tvNotifyForm.setText(notice.get(index).getFrom());
            tvNotifyDate.setText(notice.get(index).getStartTime().subSequence(0, 10));
        }else{
            Log.i("noticelxy", "-1--:"+notice.size()+"---"+index);
            verticalMarqueeView.clearText();
//            verticalMarqueeView.stopScroll();
            tvNotifyForm.setText("");
            tvNotifyDate.setText("");
        }
    }

    public HomeTextNotifiFragment() {
    }

    /**
     * 获取实例
     */
    public static HomeTextNotifiFragment newInstance() {
        HomeTextNotifiFragment fragment = new HomeTextNotifiFragment();
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
        View view = inflater.inflate(R.layout.fragment_text_home_notifi, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        registerListener();
        return view;
    }

    private void registerListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (verticalMarqueeView!=null && verticalMarqueeView.isThreadRunning()){
            startTextShowLoop(count);
        }
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(NOTICE, getDataCallBackInterface);
    }

    //========数据回调==========
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {
//            if (data != null && data.size() > 0 && data.get(0) instanceof Notification) {
                notice = data;
                handler.sendEmptyMessageDelayed(EventConfig.LIST_BACK,2000);
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

    private void initView() {
        count = 0;
        verticalMarqueeView.setOnMargueeListener(new VerticalMarqueeView.OnMargueeListener() {
            @Override
            public void onRollOver() {
                Log.i("通知播放完毕回调","=================");
                //播放完毕回调
                count++;
                if (notice != null && notice.size() > 0) {
                    if (notice.size() > count) {
                        startTextShowLoop(count);
                    } else {
                        count = 0;
                        startTextShowLoop(count);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onPause() {
        super.onPause();
//        isShow = false;
        //fragment暂停时移除线程
//        handler.removeCallbacks(autoScrollRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }
}
