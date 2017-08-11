package com.weds.settings.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.weds.lip_library.adapter.CommonAdapter;
import android.weds.lip_library.adapter.ViewHolder;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.ImageView;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.settings.ible.OnWifiSelOrContFinishCallBack;
import com.weds.settings.utils.WifiAdminUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lip on 2016/11/5.
 * <p>
 * wifi列表adapter
 */

public class SettingWfiAdapter extends CommonAdapter<ScanResult> {

    private Context context;
    private TextView priorShowState;
    private ImageView priorIvRotate;

    public SettingWfiAdapter(Context context, int layoutId) {
        super(context, layoutId);
        this.context = context;
    }

    @Override
    public void convert(ViewHolder vh, final ScanResult scanResult) {
        WifiAdminUtil wifiAdminUtil = WifiAdminUtil.getInstance(context);
        WifiInfo wifiInfo = wifiAdminUtil.getWifiInfo();//当前连接wifi

        //设置信号强度
        setRssiImg(scanResult, vh);
        //设置WiFi信息
        if (Strings.isNotEmpty(scanResult.SSID)) {
            vh.setText(R.id.tv_wifi_name, scanResult.SSID);
            final TextView tvIsConnect = (TextView) vh.getView(R.id.tv_isConnect);
            //获取安全信息
            setSafeInfo(vh, scanResult);
            LogUtils.i("wifi连接处log", wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1) + "---------" + scanResult.SSID);
            if (wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1).equals(scanResult.SSID) && wifiAdminUtil.isWifiConnect(context)) {
                //标出当前连接的wifi
                tvIsConnect.setVisibility(View.VISIBLE);
                priorShowState = tvIsConnect;
            } else {
                tvIsConnect.setVisibility(View.INVISIBLE);
            }
            final RotateAnimation raa = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            raa.setRepeatCount(-1);
            raa.setDuration(3000);
            final ImageView ivRotateLoading = (ImageView) vh.getView(R.id.iv_rotate_loading);
            ivRotateLoading.clearAnimation();
            ivRotateLoading.setVisibility(View.INVISIBLE);
            View llRoot = vh.getView(R.id.ll_root);
            llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //wifi信息item点击事件
                    SettingMotionDialog.getInstance().showConnectWifiDialog(context, scanResult, new OnWifiSelOrContFinishCallBack() {
                        @Override
                        public void onWifiSelOrContFinish(int state) {
                            switch (state) {
                                case 0://正在连接
                                    startRotateAnimation(ivRotateLoading, raa);
                                    break;
                                case 1://连接成功
                                    //连接成功后的回调
                                    tvIsConnect.setVisibility(View.VISIBLE);
                                    cancelRotateAnimation(ivRotateLoading, raa);
                                    if (priorShowState != null) {
                                        //如果有以前已连接的wifi，显示为未连接
                                        priorShowState.setVisibility(View.GONE);
                                        priorShowState = tvIsConnect;
                                    }
                                    break;
                                case 2://连接失败
                                    cancelRotateAnimation(ivRotateLoading, raa);
                                    break;
                                case 3://忘记网络
                                    if (tvIsConnect.getVisibility() == View.VISIBLE) {
                                        tvIsConnect.setVisibility(View.GONE);
                                    }
                                    cancelRotateAnimation(ivRotateLoading, raa);
                                    break;
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 结束连接动画
     *
     * @param ivRotateLoading
     * @param raa
     */
    private void cancelRotateAnimation(ImageView ivRotateLoading, RotateAnimation raa) {
        raa.cancel();
        ivRotateLoading.clearAnimation();//必须调用，否则imageView设置Gone不管用
        ivRotateLoading.setVisibility(View.INVISIBLE);
        priorIvRotate = null;
    }

    /**
     * 开始连接动画
     *
     * @param ivRotateLoading
     * @param raa
     */
    private void startRotateAnimation(ImageView ivRotateLoading, RotateAnimation raa) {
        ivRotateLoading.setVisibility(View.VISIBLE);
        ivRotateLoading.startAnimation(raa);
        if (priorIvRotate != null) {
            priorIvRotate.setVisibility(View.INVISIBLE);
        }
        if (priorShowState!=null && priorShowState.getVisibility() == View.VISIBLE) {
            priorShowState.setVisibility(View.GONE);
        }
        priorIvRotate = ivRotateLoading;
    }

    /**
     * 设置信号强度对应图片
     *
     * @param scanResult
     * @param vh
     */
    private void setRssiImg(ScanResult scanResult, ViewHolder vh) {
//设置信号强度
        int level = scanResult.level;
        LogUtils.i("wifi强度", String.valueOf(level));
        if (level >= -50) {
            vh.setImageResource(R.id.iv_wifi_power, R.mipmap.wifi_4);
        } else if (-50 < level && level <= -70) {
            vh.setImageResource(R.id.iv_wifi_power, R.mipmap.wifi_3);
        } else if (-70 < level && level <= 85) {
            vh.setImageResource(R.id.iv_wifi_power, R.mipmap.wifi_2);
        } else {
            vh.setImageResource(R.id.iv_wifi_power, R.mipmap.wifi_1);
        }
    }

    /**
     * 设置安全信息
     *
     * @param vh
     * @param scanResult
     */
    private void setSafeInfo(ViewHolder vh, ScanResult scanResult) {
        String bssid = "";
        Matcher m = Pattern.compile("\\[([^\\[\\]]+)\\]").matcher(scanResult.capabilities);
        while (m.find()) {
            String[] split = m.group(1).split("-");
            bssid = bssid + split[0] + " ";
        }
        if (Strings.isNotEmpty(bssid)) {
            vh.setText(R.id.tv_safe_name, bssid);
        } else {
            vh.setText(R.id.tv_safe_name, "WPA");
        }
    }
}
