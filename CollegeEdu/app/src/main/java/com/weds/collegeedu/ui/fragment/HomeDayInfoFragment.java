package com.weds.collegeedu.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.FileUtils;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.DayInfo;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.DAY_INFO;

/**
 * 当天时间等信息fragment
 */
public class HomeDayInfoFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.time_clock)
    TextClock timeClock;
    @Bind(R.id.tv_week)
    TextView tvWeek;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_temp)
    TextView tvTemp;
    @Bind(R.id.tv_teach_week)
    TextView tvTeachWeek;
    @Bind(R.id.iv_net_state)
    ImageView ivNetState;
    @Bind(R.id.iv_yellow_line)
    ImageView ivYellowLine;
    @Bind(R.id.tv_weather_info)
    TextView tvWeatherInfo;
    @Bind(R.id.iv_weather)
    ImageView ivWeather;

    private String mParam1;
    private String mParam2;

    private boolean isYellowLineShow = false;
    private int count = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    count++;
                    if (isYellowLineShow && count > 5) {
                        ivYellowLine.setVisibility(View.INVISIBLE);
                        isYellowLineShow = false;
                        count = 0;
                    } else if (count > 5) {
                        ivYellowLine.setVisibility(View.VISIBLE);
                        isYellowLineShow = true;
                        count = 0;
                    }
                    break;
                case EventConfig.OBJ_BACK:
                    setDayInfo(dayInfo);
                    break;
            }
        }
    };
    private DayInfo dayInfo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeDayInfoFragment.
     */
    public static HomeDayInfoFragment newInstance() {
        HomeDayInfoFragment fragment = new HomeDayInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_home_day_info, container, false);
        ButterKnife.bind(this, view);
        registerBootReceiver();
        initView();
        initData();
        return view;
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(DAY_INFO, getDataCallBackInterface);
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weds.ttf");
        timeClock.setTypeface(typeface);
        handler.sendEmptyMessage(0);
    }

    //===========数据回调=========
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
            dayInfo = (DayInfo) data;
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

    /**
     * 筛选设置网络状态
     *
     * @param state 网络状态
     */
    private void swiNetStateImg(String state) {
        if (ivNetState != null) {
            switch (state) {
                case "0":
//                    ivNetState.setImageResource(R.mipmap.sys_wire_unlink);
                    handler.sendEmptyMessage(0);
                    break;
                case "1":
//                    ivNetState.setImageResource(R.mipmap.sys_comm_unconect);
                    handler.sendEmptyMessage(0);
                    break;
                case "2":
//                    ivNetState.setImageResource(R.mipmap.sys_wire_link);
                    ivYellowLine.setVisibility(View.INVISIBLE);
                    handler.removeCallbacksAndMessages(null);
                    break;
                case "3":
                    break;
            }
        }
    }

    private NetStateChangeReceiver netStateChangeReceiver;
    private boolean isRegister = false;

    private void registerBootReceiver() {
        //动态注册广播监听
        IntentFilter filter = new IntentFilter();
        filter.addAction("wireStat");
        netStateChangeReceiver = new NetStateChangeReceiver();
        if (!isRegister) {//防止重复注册
            context.registerReceiver(netStateChangeReceiver, filter);
            isRegister = true;
        }
    }

    /**
     * 监听网络连接状态广播
     */
    private class NetStateChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String messageStat = intent.getStringExtra("messageStat");
            if (Strings.isNotEmpty(messageStat)) {
                swiNetStateImg(messageStat);
            }
        }
    }

    /**
     * 设置当天信息
     *
     * @param dayInfo
     */
    private void setDayInfo(DayInfo dayInfo) {
        tvTemp.setText("");
        LogUtils.i("时间刷新当天info界面", dayInfo.toString());
        if (dayInfo != null) {
            tvDate.setText(dayInfo.getDate());
            tvWeek.setText(dayInfo.getWeek());
            tvTeachWeek.setText(dayInfo.getTeachWeek());
            tvWeatherInfo.setText(dayInfo.getWeatherInfo());
            tvTemp.setText(dayInfo.getTemp());
            //根据名称获取mipmap资源
            int mipmapImgId = FileUtils.getMipmapImgId(dayInfo.getWeatherImg(), context);
            ivWeather.setImageResource(mipmapImgId);
            LogUtils.i("天气图片",dayInfo.getWeatherImg()+"---"+mipmapImgId);
            ivWeather.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(netStateChangeReceiver);
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }
}
