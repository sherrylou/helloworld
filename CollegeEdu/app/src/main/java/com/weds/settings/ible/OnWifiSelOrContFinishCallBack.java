package com.weds.settings.ible;

/**
 * Created by lip on 2016/12/3.
 *
 * wifi网络回调
 */

public interface OnWifiSelOrContFinishCallBack {

    /**
     * @param state 0---正在连接 1---连接成功 2---连接失败 3---忘记网络
     */
    public void onWifiSelOrContFinish(int state);

}
