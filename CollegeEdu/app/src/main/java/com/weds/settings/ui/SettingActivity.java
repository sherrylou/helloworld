package com.weds.settings.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.ui.BaseActivity;
import android.weds.lip_library.util.LogUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.R;
import com.weds.collegeedu.ible.PhysicalButtonsInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.PhysicalButtonsUtils;
import com.weds.settings.ible.OnSettingReturnCallBack;
import com.weds.settings.utils.WedsSettingUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.settings.utils.WedsSettingUtils.ADVANCED_JSON;
import static com.weds.settings.utils.WedsSettingUtils.GUIDE_JSON;
import static com.weds.settings.utils.WedsSettingUtils.ICO;
import static com.weds.settings.utils.WedsSettingUtils.ICON_ROOT_PATH;
import static com.weds.settings.utils.WedsSettingUtils.NAME;

/**
 * 设置界面
 */
public class SettingActivity extends BaseActivity implements OnSettingReturnCallBack{

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.fl_setting)
    LinearLayout flSetting;
    @Bind(R.id.tv_right_rec)
    TextView tvRightRec;
    @Bind(R.id.ll_guide_setting)
    LinearLayout llGuideSetting;
    @Bind(R.id.ll_advanced_setting)
    LinearLayout llAdvancedSetting;
    @Bind(R.id.ll_setting_left_down)
    LinearLayout llSettingLeftDown;
    @Bind(R.id.fl_right_setting_contain)
    FrameLayout flRightSettingContain;
    /**
     * 前一个被点击的侧边栏item
     */
    private View priorLeftItemView;

    /**
     * 前一个被点击的图片icon名称
     */
    private String priorIconName;

    /**
     * 向导或高级模式状态 0-向导
     */
    private int settingState = 0;
    private SettingGuideFragment guideFragment;
    private SettingAdvancedFragment settingAdvancedFragment;
    private Context context;
    private List<View> leftIcon = new ArrayList<>();

    /**
     * 是否确定进入了里菜单
     */
    private boolean isInFragment = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.UP_KEY_DOWN:
                    if (isInFragment){
                        settingAdvancedFragment.physicalKeyDown(EventConfig.UP_KEY_DOWN);
                    }else{
                        PhysicalButtonsUtils.getInstance().listViewUp(leftIcon,true,null);
                    }
                    break;
                case EventConfig.DOWN_KEY_DOWN:
                    if (isInFragment){
                        settingAdvancedFragment.physicalKeyDown(EventConfig.DOWN_KEY_DOWN);
                    }else{
                        PhysicalButtonsUtils.getInstance().listViewDown(leftIcon,true,null);
                    }
                    break;
                case EventConfig.CANCEL_KEY_DOWN:
                    if (isInFragment){
                        settingAdvancedFragment.physicalKeyDown(EventConfig.CANCEL_KEY_DOWN);
                    }else{
                        AppManager.getInstance().finishActivity(SettingActivity.this);
                    }
                    break;
                case EventConfig.ENTER_KEY_DOWN:
                    isInFragment = true;
                    settingAdvancedFragment.physicalKeyDown(EventConfig.ENTER_KEY_DOWN);
                    break;
            }
        }
    };

    /**
     * fragment回调
     */
    @Override
    public void settingReturn() {
        isInFragment = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        context = SettingActivity.this;
        initView();
        initData();
        stateCheck(savedInstanceState);
        registerListener();
    }

    private void initData() {
        PhysicalButtonsUtils.getInstance().setPhysicalButtonCallBack(this, physicalButtonsInterface);
        //获取向导模式一级菜单
//        firstGuideJsonArray = WedsSettingUtils.getFirstJsonArray(0);
//        firstAdvancedJsonArray = WedsSettingUtils.getFirstJsonArray(1);
    }

    private void initView() {
        tvTitle.setText("设置");
        addLeftSideView(0);
        setRightFragment();
        guideFragment = SettingGuideFragment.newInstance();
        settingAdvancedFragment = SettingAdvancedFragment.newInstance();
    }

    /**
     * 添加左边侧边栏
     */
    private void addLeftSideView(final int index) {
        llSettingLeftDown.removeAllViews();//清空旧布局
        JSONArray firstJsonArray = WedsSettingUtils.getFirstJsonArray(index);
        if (firstJsonArray != null && firstJsonArray.size() > 0) {
            //添加左边栏
            for (int i = 0; i < firstJsonArray.size(); i++) {
                JSONObject firstMenu = ((JSONObject) firstJsonArray.get(i));
                final View view = LayoutInflater.from(context).inflate(R.layout.setting_left_item, null);
                //设置icon
                final ImageView ivIcon = (ImageView) view.findViewById(R.id.
                        iv_icon);
                //icon名称
                final String iconName = firstMenu.getString(ICO);
                setLeftIcon(ivIcon, iconName, 1);
                //设置名称
                final TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                tv_name.setText(firstMenu.getString(NAME));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.setting_left_item_height));
                view.setLayoutParams(lp);
                llSettingLeftDown.addView(view);
                //设置点击事件
                final int finalI = i;
                view.findViewById(R.id.ll_root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (priorLeftItemView == null) {
                            priorLeftItemView = view;
                            priorIconName = iconName;
                        }
                        //设置上一个被点击的item变暗
                        priorLeftItemView.findViewById(R.id.ll_root).setBackgroundColor(getResources().getColor(R.color.B8));
                        ((TextView) priorLeftItemView.findViewById(R.id.tv_name)).setTextColor(getResources().getColor(R.color.W1));
                        ImageView priorImageView = (ImageView) priorLeftItemView.findViewById(R.id.iv_icon);
                        setLeftIcon(priorImageView, priorIconName, 1);
                        //将被点击的icon变亮
                        view.findViewById(R.id.ll_root).setBackgroundColor(getResources().getColor(R.color.W1));
                        tv_name.setTextColor(getResources().getColor(R.color.B8));
                        setLeftIcon(ivIcon, iconName, 0);
                        //将本次被点击的item记录
                        priorLeftItemView = view;
                        priorIconName = iconName;
                        //改变右边菜单
//                        if (index == 0) {
//                            guideFragment.leftChange(finalI);
//                        } else if (index == 1) {
//
//                        }
                        settingAdvancedFragment.leftChange(finalI);
                    }
                });
                if (i == 0) {
                    //初始化第一个item
                    view.findViewById(R.id.ll_root).setBackgroundColor(getResources().getColor(R.color.W1));
                    tv_name.setTextColor(getResources().getColor(R.color.B8));
                    setLeftIcon(ivIcon, iconName, 0);
                    //将本次被点击的item记录
                    priorLeftItemView = view;
                    priorIconName = iconName;
                }
                leftIcon.add(view);//添加到物理按键list里
            }
        }
    }

    /**
     * 设置右边栏fragment
     */
    private void setRightFragment() {

    }

    private void registerListener() {
        llGuideSetting.setOnClickListener(this);
        llAdvancedSetting.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    /**
     * 状态检测 用于内存不足的时候保证fragment不会重叠
     *
     * @param savedInstanceState
     */
    private void stateCheck(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.add(R.id.fl_right_setting_contain, settingAdvancedFragment, ADVANCED_JSON).commit();
        } else {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            Fragment gt = getSupportFragmentManager().findFragmentByTag(ADVANCED_JSON);
            fts.show(gt).commit();
        }
    }

    /**
     * fragment 切换
     *
     * @param from
     * @param to
     */
    public void switchContent(Fragment from, Fragment to, String tag) {
        LogUtils.i("设置切换fragment", "====" + from.isAdded() + "====" + "=====" + to.isAdded());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!from.isAdded()) {
            return;
        }
        if (!to.isAdded()) { // 先判断是否被add过
            transaction.hide(from)
                    .add(R.id.fl_right_setting_contain, to, tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }
    }

    /**
     * 设置左边栏icon
     *
     * @param ivIcon  imageView
     * @param iconStr 图片路径
     * @param state   显示亮或暗 0-亮
     */
    private void setLeftIcon(ImageView ivIcon, String iconStr, int state) {
        String icon = ICON_ROOT_PATH + iconStr;
        //如果亮暗两种效果图片文件存在
        if (new File(icon).exists()) {
            ivIcon.setImageBitmap(optimizeImage(icon));
            if (state == 0) {
                //设置亮色图片
                ivIcon.setColorFilter(context.getResources().getColor(R.color.B8));
            } else {
                ivIcon.setColorFilter(context.getResources().getColor(R.color.W1));
            }
        } else {
            //图片资源不存在，设置默认图片
            if (state == 0) {
                //设置亮色图片
                ivIcon.setImageResource(R.mipmap.wifi_light);
            } else {
                ivIcon.setImageResource(R.mipmap.wifi_black);
            }
        }
    }

    /**
     * 优化图片资源
     *
     * @param path
     * @return
     */
    private Bitmap optimizeImage(String path) {
        //优化内存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;//图片宽高都为原来的二分之一，即图片为原来的四分之一
        Bitmap b = BitmapFactory.decodeFile(path, options);
        return b;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_guide_setting:
                llGuideSetting.setBackgroundResource(R.drawable.setting_left_top_button_sel_frame);
                llAdvancedSetting.setBackgroundResource(R.drawable.setting_left_top_button_unsel_frame);
                translateAnimation(1, llGuideSetting);
                switchContent(settingAdvancedFragment, guideFragment, GUIDE_JSON);
                addLeftSideView(0);
                break;
            case R.id.ll_advanced_setting:
                llAdvancedSetting.setBackgroundResource(R.drawable.setting_left_top_button_sel_frame);
                llGuideSetting.setBackgroundResource(R.drawable.setting_left_top_button_unsel_frame);
                translateAnimation(0, llAdvancedSetting);
                switchContent(guideFragment, settingAdvancedFragment, ADVANCED_JSON);
                addLeftSideView(0);
                break;
            case R.id.iv_back:
                AppManager.getInstance().finishActivity(SettingActivity.this);
//                finish();
                break;
        }
    }

    /**
     * 按钮移动动画
     *
     * @param state 0-右移
     */
    private void translateAnimation(int state, View v) {
        AnimationSet as = new AnimationSet(true);
        TranslateAnimation ta;
        if (state == 0) {
            ta = new TranslateAnimation(-90, 0, 0, 0);
        } else {
            ta = new TranslateAnimation(90, 0, 0, 0);
        }
        ta.setDuration(800);
        as.addAnimation(ta);
        v.startAnimation(as);
    }

    //========物理按键回调===========
    PhysicalButtonsInterface physicalButtonsInterface = new PhysicalButtonsInterface() {
        @Override
        public void cnacelKeyDown() {
            handler.sendEmptyMessage(EventConfig.CANCEL_KEY_DOWN);
        }

        @Override
        public void enterKeyDown() {
            handler.sendEmptyMessage(EventConfig.ENTER_KEY_DOWN);
        }

        @Override
        public void upDown() {
            handler.sendEmptyMessage(EventConfig.UP_KEY_DOWN);
        }

        @Override
        public void downKeyDown() {
            handler.sendEmptyMessage(EventConfig.DOWN_KEY_DOWN);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhysicalButtonsUtils.getInstance().clearIndexCatch();
    }
}
