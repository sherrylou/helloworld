package com.weds.tenedu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.widget.ImageView;

import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.utils.UIHelper;
import com.weds.collegeedu.utils.WedsDataUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.TEN_MAIN_TIME;


public class MainActivity extends StandByActivity {
    @Bind(R.id.iv_default_img)
    ImageView ivDefaultImg;
    private Fragment[] mFragments;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //无课程
                    setFragmentIndicator(1);
                    break;
                case 1:
                    //有课程
                    setFragmentIndicator(0);
                    break;
                case 2:
                    ivDefaultImg.setVisibility(View.GONE);
                    setFragmentIndicator(2);
                    break;
                case 3:
                    ivDefaultImg.setVisibility(View.GONE);
                    setFragmentIndicator(3);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.CurrentWakeTime.isEmpty()) {
            App.CurrentWakeTime = App.getHHMMSS();
        }
    }

    private void initView() {
        mFragments = new Fragment[4];
        fragmentManager = getSupportFragmentManager();
        mFragments[0] = fragmentManager.findFragmentById(R.id.fragment_attendance);
        mFragments[1] = fragmentManager.findFragmentById(R.id.fragment_course_timetable);
        mFragments[2] = fragmentManager.findFragmentById(R.id.fragment_album);
        mFragments[3] = fragmentManager.findFragmentById(R.id.fragment_video);
        //在xml里固定fragment了所以不用add
        fragmentTransaction = fragmentManager.beginTransaction()
                .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]);
        fragmentTransaction.show(mFragments[1]).commitAllowingStateLoss();
        ivDefaultImg.setVisibility(View.VISIBLE);
//        setFragmentIndicator(1);
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(TEN_MAIN_TIME, getDataCallBackInterface);
        App.startCheckThread();
    }

    /**
     * @param index ,设置显示哪个fragment模块，0是显示课程信息，1是隐藏课程，显示课表
     */
    private void setFragmentIndicator(int index) {
        if (mFragments[index].isHidden()) {
            if (index < 2) {
                fragmentTransaction = fragmentManager.beginTransaction()
                        .hide(mFragments[0]).hide(mFragments[1]);
                fragmentTransaction.show(mFragments[index]).commitAllowingStateLoss();
            } else {
                fragmentTransaction = fragmentManager.beginTransaction()
                        .hide(mFragments[2]).hide(mFragments[3]);
                fragmentTransaction.show(mFragments[index]).commitAllowingStateLoss();
            }
        }
    }



    //===========数据回调============
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
            switch (type) {
                case SUB_CHANGE_NOSUB://无课程
                    handler.sendEmptyMessage(0);
                    break;
                case SUB_CHANGE_INSUB://有课程
                    handler.sendEmptyMessage(1);
                    break;
                case SHOW_IMG_FRAG://显示图片
                    handler.sendEmptyMessage(2);
                    break;
                case SHOW_VIDEO_FRAG://显示视频
                    handler.sendEmptyMessage(3);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
