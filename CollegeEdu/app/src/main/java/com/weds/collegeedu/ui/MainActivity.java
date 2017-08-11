package com.weds.collegeedu.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.ui.BaseActivity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.entity.Regular;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.ible.PhysicalButtonsInterface;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.ui.fragment.HomeAttendanceFragment;
import com.weds.collegeedu.ui.fragment.HomeCarouselImgFragment;
import com.weds.collegeedu.ui.fragment.HomeClassInfoFragment;
import com.weds.collegeedu.ui.fragment.HomeDayInfoFragment;
import com.weds.collegeedu.ui.fragment.HomeTableFragment;
import com.weds.collegeedu.ui.fragment.HomeTextNotifiFragment;
import com.weds.collegeedu.utils.PhysicalButtonsUtils;
import com.weds.collegeedu.utils.UIHelper;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.dialog.InputPswDialog;
import com.weds.settings.utils.WedsSettingUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.TWENTY_ONE_MAIN;

/**
 * 主页
 */
public class MainActivity extends BAwakeActivity {

    @Bind(R.id.fl_class_info)
    FrameLayout flClassInfo;
    @Bind(R.id.fl_multi)
    FrameLayout flMulti;
    @Bind(R.id.fl_day_info)
    FrameLayout flDayInfo;
    @Bind(R.id.fl_notification)
    FrameLayout flNotification;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;
    @Bind(R.id.fl_table_info)
    FrameLayout flTableInfo;
    @Bind(R.id.fl_attendance_info)
    FrameLayout flAttendanceInfo;
    @Bind(R.id.rl_sub_info)
    CardView rlSubInfo;
    @Bind(R.id.tv_sub_title)
    TextView tvSubTitle;

    private final int SHOW_PSW_DIALOG = 200;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String type = (String) msg.obj;
                    if (type.equals(EventConfig.CURRENT_TEXT_DAY)) {
                        tvSubTitle.setText("考试安排");
                    } else if (type.equals(EventConfig.CURRENT_SUB_DAY)) {
                        tvSubTitle.setText("课程安排");
                    }
                    break;
                case SHOW_PSW_DIALOG:
                    if (!isDialogShow) {
                        inputPswDialog = UIHelper.toSettingActivity(MainActivity.this);
                        isDialogShow = true;
                    }
                    break;
            }
        }
    };
    private Regular regular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView(savedInstanceState);
        initData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (App.CurrentWakeTime.isEmpty()) {
            App.CurrentWakeTime = App.getHHMMSS();
        }
//        finishLastActivity();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        isDialogShow = false;
    }

    private void initView(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            //班级信息
            fm.beginTransaction().replace(R.id.fl_class_info, HomeClassInfoFragment.newInstance(), ConstantConfig.CLASS_INFO_TAG).commit();

            //轮播图
            fm.beginTransaction().replace(R.id.fl_multi, HomeCarouselImgFragment.newInstance(), ConstantConfig.CAROUSE_IMG).commit();

            //当天信息
            fm.beginTransaction().replace(R.id.fl_day_info, HomeDayInfoFragment.newInstance(), ConstantConfig.DAY_INFO).commit();

            //通知
            fm.beginTransaction().replace(R.id.fl_notification, HomeTextNotifiFragment.newInstance(), ConstantConfig.NOTICE_TAG).commit();

            //课表
            fm.beginTransaction().replace(R.id.fl_table_info, HomeTableFragment.newInstance(), ConstantConfig.TABLE_LIST).commit();

            //出勤
            fm.beginTransaction().replace(R.id.fl_attendance_info, HomeAttendanceFragment.newInstance(), ConstantConfig.ATTENDANCE_TAG).commit();
        } else {
            //防止重叠
            //班级信息
            HomeClassInfoFragment homeClassInfoFragment = (HomeClassInfoFragment) fm.findFragmentByTag(ConstantConfig.CLASS_INFO_TAG);
            //轮播图
            HomeCarouselImgFragment homeCarouselImgFragment = ((HomeCarouselImgFragment) fm.findFragmentByTag(ConstantConfig.CAROUSE_IMG));
            //当天信息
            HomeDayInfoFragment homeDayInfoFragment = ((HomeDayInfoFragment) fm.findFragmentByTag(ConstantConfig.DAY_INFO));
            //通知
            HomeTextNotifiFragment homeTextNotifiFragment = ((HomeTextNotifiFragment) fm.findFragmentByTag(ConstantConfig.NOTICE_TAG));
            //课表
            HomeTableFragment homeTableFragment = ((HomeTableFragment) fm.findFragmentByTag(ConstantConfig.TABLE_LIST));
            //出勤
            HomeAttendanceFragment homeAttendanceFragment = ((HomeAttendanceFragment) fm.findFragmentByTag(ConstantConfig.ATTENDANCE_TAG));
            fm.beginTransaction().show(homeClassInfoFragment).commit();
            fm.beginTransaction().show(homeCarouselImgFragment).commit();
            fm.beginTransaction().show(homeDayInfoFragment).commit();
            fm.beginTransaction().show(homeTextNotifiFragment).commit();
            fm.beginTransaction().show(homeTableFragment).commit();
            fm.beginTransaction().show(homeAttendanceFragment).commit();
        }
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(TWENTY_ONE_MAIN, getDataCallBackInterface);
        PhysicalButtonsUtils.getInstance().setPhysicalButtonCallBack(this, physicalButtonsInterface);
        App.startCheckThread();
    }

    //=============数据回调===============
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

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
            Message msg = Message.obtain();
            msg.obj = type;
            msg.what = 0;
            handler.sendMessage(msg);
        }
    };

    private InputPswDialog inputPswDialog;
    private boolean isDialogShow = false;
    //=============物理按键回调================
    PhysicalButtonsInterface physicalButtonsInterface = new PhysicalButtonsInterface() {
        @Override
        public void cnacelKeyDown() {
            if (isDialogShow && inputPswDialog != null) {
                inputPswDialog.physicalKeyDown(EventConfig.CANCEL_KEY_DOWN);
                isDialogShow = false;
            }
        }

        @Override
        public void enterKeyDown() {
            if (AppManager.getInstance().getCurrentActivity() instanceof MainActivity) {
                handler.sendEmptyMessage(SHOW_PSW_DIALOG);
            }
            if (isDialogShow && inputPswDialog != null) {
                inputPswDialog.physicalKeyDown(EventConfig.ENTER_KEY_DOWN);
            }
        }

        @Override
        public void upDown() {
            if (isDialogShow && inputPswDialog != null) {
                inputPswDialog.physicalKeyDown(EventConfig.UP_KEY_DOWN);
            }
        }

        @Override
        public void downKeyDown() {
            if (isDialogShow && inputPswDialog != null) {
                inputPswDialog.physicalKeyDown(EventConfig.DOWN_KEY_DOWN);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
