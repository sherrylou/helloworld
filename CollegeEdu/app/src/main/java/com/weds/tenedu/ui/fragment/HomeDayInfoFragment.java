package com.weds.tenedu.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.FileUtils;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.DayInfo;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.UIHelper;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.tenedu.ui.StandByActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.DAY_INFO;

/**
 * 主页顶部右边时间和一些状态图标的设置
 */
public class HomeDayInfoFragment extends BaseFragment {

    @Bind(R.id.time_clock)
    TextClock timeClock;
    @Bind(R.id.date_clock)
    TextClock dateClock;
    @Bind(R.id.iv_wifi_power)
    ImageView ivWifiPower;
    @Bind(R.id.tv_use)
    TextView tvUse;
    @Bind(R.id.iv_net_state)
    ImageView ivNetState;
    @Bind(R.id.iv_weather)
    ImageView ivWeather;
    @Bind(R.id.tv_weather_info)
    TextView tvWeatherInfo;
    @Bind(R.id.tv_temp)
    TextView tvTemp;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.tv_available)
    TextView tvAvailable;
    private DayInfo dayInfo;

    private boolean isColonShow = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (isColonShow) {
                        timeClock.setFormat24Hour("hh mm");
                        isColonShow = false;
                    } else {
                        timeClock.setFormat24Hour("hh:mm");
                        isColonShow = true;
                    }
                    handler.sendEmptyMessage(1);
                    break;
                case 1:
                    handler.sendEmptyMessageDelayed(0, 500);
                    break;
                case EventConfig.OBJ_BACK:
                    if (dayInfo != null) {
                        tvWeatherInfo.setText(dayInfo.getWeatherInfo());
                        tvTemp.setText(dayInfo.getTemp());
                        //根据名称获取mipmap资源
                        int mipmapImgId = FileUtils.getMipmapImgId(dayInfo.getWeatherImg(), context);
                        ivWeather.setImageResource(mipmapImgId);
                        LogUtils.i("天气图片",dayInfo.getWeatherImg()+"---"+mipmapImgId);
                        ivWeather.setVisibility(View.VISIBLE);
                    }else {
                        tvWeatherInfo.setText("");
                        tvTemp.setText("");
                        ivWeather.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

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
            int linkStat = intent.getIntExtra("linkStat",0);
            LogUtils.i("接收网络状态", "=======" + messageStat + "======");
            if (Strings.isNotEmpty(messageStat)) {
                swiNetStateImg(messageStat,linkStat);
            }
        }
    }

    /**
     * 筛选设置网络状态
     *
     * @param state 网络状态
     * @param linkStat
     */
    private void swiNetStateImg(String state, int linkStat) {
        if (ivNetState != null) {
            switch (state) {
                case "0":
                    ivNetState.setImageResource(R.mipmap.net_offline);
                    break;
                case "1":
                    ivNetState.setImageResource(R.mipmap.net_offline);
                    break;
                case "2":
                    ivNetState.setImageResource(R.mipmap.net_normal);
                    break;
                case "3":

                    break;
                case "10":
                    if (linkStat==0){
                        ivWifiPower.setImageResource(R.mipmap.wifi_useless_04);
                    }else{
                        ivWifiPower.setImageResource(R.mipmap.wifi_4);
                    }
                    break;
                case "11":
                    if (linkStat==0){
                        ivWifiPower.setImageResource(R.mipmap.wifi_useless_03);
                    }else{
                        ivWifiPower.setImageResource(R.mipmap.wifi_3);
                    }
                    break;
                case "12":
                    if (linkStat==0){
                        ivWifiPower.setImageResource(R.mipmap.wifi_useless_02);
                    }else{
                        ivWifiPower.setImageResource(R.mipmap.wifi_2);
                    }
                    break;
                case "13":
                    if (linkStat==0){
                        ivWifiPower.setImageResource(R.mipmap.wifi_useless_01);
                    }else{
                        ivWifiPower.setImageResource(R.mipmap.wifi_1);
                    }
                    break;

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ten_fragment_head_icon_state, container, false);
        ButterKnife.bind(this, view);
        initView();
        registerBootReceiver();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
//        handler.sendEmptyMessageDelayed(0,500);

    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(DAY_INFO, getDataCallBackInterface);
        //磁盘剩余空间设置
        String path = Environment.getExternalStorageDirectory().getPath();
        //一个包装类，用来检索文件系统的信息
        StatFs stat = new StatFs(path);
        //文件系统的块的大小（byte）
        long blockSize = stat.getBlockSize();
        //文件系统的总的块数
        long totalBlocks = stat.getBlockCount();
        //文件系统上空闲的可用于程序的存储块数
        long availableBlocks = stat.getAvailableBlocks();
        //总的容量
        long totalSize = blockSize * totalBlocks;
        long availableSize = blockSize * availableBlocks;
        Float totleSizeD = Float.valueOf(totalSize);
        Float availableSizeD = Float.valueOf(availableSize);
        Float use = (totleSizeD - availableSizeD)/totleSizeD;
        LogUtils.i("磁盘信息","---"+use+"---"+(totalSize - availableSize)+"---"+totalSize);
        if (use<0.2){
            use = 0.2f;
        }
        LinearLayout.LayoutParams tvUseLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,use);
        tvUse.setLayoutParams(tvUseLp);
        LinearLayout.LayoutParams tvAvailableLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1-use);
        tvAvailable.setLayoutParams(tvAvailableLp);
    }

    //============数据回调============
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
            if (data != null && handler!=null) {
                dayInfo = ((DayInfo) data);
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

    @OnClick({R.id.ll_root})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_root:
                UIHelper.toSettingActivity(context);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(netStateChangeReceiver);
        handler.removeCallbacksAndMessages(null);
        handler = null;
        ButterKnife.unbind(this);
    }
}
