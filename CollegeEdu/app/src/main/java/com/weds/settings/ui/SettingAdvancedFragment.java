package com.weds.settings.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.LogUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.collegeedu.dialog.DialogShowUtils;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.PhysicalButtonsUtils;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.ible.OnSettingReturnCallBack;
import com.weds.settings.ible.OnSettingTirViewReturnCallBack;
import com.weds.settings.utils.WedsSettingUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.settings.utils.WedsSettingUtils.NAME;
import static com.weds.settings.utils.WedsSettingUtils.STYLE;
import static com.weds.settings.utils.WedsSettingUtils.SUBMENU;


/**
 * Created by lip
 * 高级菜单fragment
 */
public class SettingAdvancedFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.ll_setting_right)
    LinearLayout llSettingRight;
    @Bind(R.id.ll_tir_contain)
    LinearLayout llTirContain;
    @Bind(R.id.tv_bottom)
    TextView tvBottom;
    @Bind(R.id.sl_tir_contain)
    ScrollView slTirContain;

    private String mParam1;
    private String mParam2;

    /**
     * 一级高级菜单
     */
    private JSONArray firstJsonArray;

    /**
     * 二级高级菜单
     */
    private JSONArray secAdvancedJsonArray;

    private TextView priorTitleText;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    loadingDialog.dismissLoadingDialog();
                    break;
            }
        }
    };
    private SettingMotionDialog loadingDialog;
    private DialogShowUtils dialogShowUtils;

    public SettingAdvancedFragment() {
    }

    private OnSettingReturnCallBack onSettingReturnCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null) {
            onSettingReturnCallBack = ((OnSettingReturnCallBack) context);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingAdvancedFragment.
     */
    public static SettingAdvancedFragment newInstance() {
        SettingAdvancedFragment fragment = new SettingAdvancedFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting_advanced, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView();
        registerListener();
        return view;
    }

    private void initData() {
        firstJsonArray = WedsSettingUtils.getFirstJsonArray(0);
    }

    private void initView() {
        addAdvancedTitleView(0);
        addLevelTirView(0);
    }

    private void registerListener() {
        tvBottom.setOnClickListener(this);
    }

    /**
     * 添加高级设置标题view
     */
    private void addAdvancedTitleView(int index) {
//        if (llSettingRight != null) {
        //清空原先的子view
        titleViews.clear();
        llSettingRight.removeAllViews();
//        }
        //添加
        if (firstJsonArray != null && firstJsonArray.size() > index) {
            //获取index对应的一级类
            JSONObject firstJsonObject = (JSONObject) firstJsonArray.get(index);
            //二级菜单索引
//            Integer secIndex = Integer.valueOf((String) firstJsonObject.get(WedsSettingUtils.NEXT_GUIDE_INDEX));
            String nextGuideIndex = ((String) firstJsonObject.get(WedsSettingUtils.NEXT_GUIDE_INDEX));
            Integer secIndex = 0;
            if (!nextGuideIndex.equals("0")) {
                secIndex = Integer.valueOf((String) MenuVariablesInfo.getInstance().getSysVariable(nextGuideIndex));
            }
            //获取二级菜单
            secAdvancedJsonArray = WedsSettingUtils.getSecJsonArray(secIndex, firstJsonObject.getJSONArray(SUBMENU));
            if (secAdvancedJsonArray != null) {
                int count = 0;
                for (Object o : secAdvancedJsonArray) {
                    JSONObject jsonObject = (JSONObject) o;
                    View advancedTitleButton = LayoutInflater.from(context).inflate(R.layout.setting_advanced_title_button_layout, null);
                    final TextView tvTitleName = (TextView) advancedTitleButton.findViewById(R.id.tv_advanced_title_name);
                    String name = jsonObject.getString("name");
                    if (App.getProjectType()==0 && name.equals("待机设置")){
                        //防止21寸出现待机设置选项
                        continue;
                    }
                    tvTitleName.setText(name);//设置标题按钮名称
                    if (count == 0 && App.getCanTouchScreen()==1) {
                        //如果当前为触摸屏设备
                        tvTitleName.setTextColor(Color.WHITE);
                        tvTitleName.setBackgroundResource(R.drawable.setting_advanced_title_button_back);
                        priorTitleText = tvTitleName;
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.CENTER_VERTICAL;
                    int right = getResources().getDimensionPixelOffset(R.dimen.setting_advanced_title_button_right_margin);
                    lp.setMargins(0, 0, right, 0);
                    advancedTitleButton.setLayoutParams(lp);
                    final int finalCount = count;
                    //Title Button点击事件
                    advancedTitleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setOnButtonBack(tvTitleName);
                            addLevelTirView(finalCount);
                        }
                    });
                    llSettingRight.addView(advancedTitleButton);
                    count++;
                    titleViews.add(advancedTitleButton);
                }
            }
        }
    }

    /**
     * 设置标题按钮背景
     *
     * @param tvTitleName 被设置textView
     */
    private void setOnButtonBack(TextView tvTitleName) {
        if (priorTitleText != null) {
            priorTitleText.setTextColor(getResources().getColor(R.color.B8));
            priorTitleText.setBackgroundColor(Color.WHITE);
        }
        tvTitleName.setTextColor(Color.WHITE);
        tvTitleName.setBackgroundResource(R.drawable.setting_advanced_title_button_back);
        priorTitleText = tvTitleName;
    }

    private int tirSettingIndex = 0;
    /**
     * 添加三级菜单具体内容
     *
     * @param index 索引
     */
    private void addLevelTirView(int index) {
        tirSettingIndex = index;
        if (secAdvancedJsonArray != null) {
            //清空原先的子view
            llTirContain.removeAllViews();
            tirViews.clear();
            if (secAdvancedJsonArray.size() < index || index < 0) {
                //如果超过角标或index小于0
                return;
            } else if (secAdvancedJsonArray.size() == index) {
                //如果等于角标,配置完成界面显示
                return;
            }
            //获取二级菜单里index对应object
            JSONObject secJsonObject = (JSONObject) secAdvancedJsonArray.get(index);
            String nextGuideIndex = ((String) secJsonObject.get(WedsSettingUtils.NEXT_GUIDE_INDEX));
            Integer tirIndex = 0;
            if (!nextGuideIndex.equals("0")) {
                tirIndex = Integer.valueOf((String) MenuVariablesInfo.getInstance().getSysVariable(nextGuideIndex));
            }
            JSONArray tirContentJsonArray = secJsonObject.getJSONArray(SUBMENU);
            String style = secJsonObject.getString(STYLE);
            if (tirContentJsonArray != null && tirContentJsonArray.size() > 0) {
                //获取三级菜单
                JSONArray tirGuideJsonArray = tirContentJsonArray.getJSONArray(tirIndex);
                //添加
                if (tirGuideJsonArray.size() > 0) {
                    //如果引导三级菜单不为null
                    for (Object o : tirGuideJsonArray) {
                        final JSONObject tirJsonObject = (JSONObject) o;
                        //带标题包含view
                        View tirView = LayoutInflater.from(context).inflate(R.layout.setting_tir_layout, null);
                        TextView tvTirTitleName = (TextView) tirView.findViewById(R.id.tv_tir_title_name);
                        tvTirTitleName.setText(tirJsonObject.getString(NAME));
                        LinearLayout rlTirItemContain = (LinearLayout) tirView.findViewById(R.id.ll_tir_contain);
                        //根据style添加不同view
                        WedsSettingUtils.getInstance().switchStyleLayout(tirJsonObject, rlTirItemContain, context);
                        //添加到三级菜单item上
                        LinearLayout.LayoutParams tirItemlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        tirView.setLayoutParams(tirItemlp);
                        llTirContain.addView(tirView);
                        tirViews.add(tirView);
                        if (App.getCanTouchScreen() == 0){
                            tirView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //非触屏菜单设置点击监听
                                    isInDialog = true;
                                    dialogShowUtils = DialogShowUtils.getInstance();
                                    dialogShowUtils.showPhysicalSettingDialog(context, tirJsonObject, new OnSettingReturnCallBack() {
                                        @Override
                                        public void settingReturn() {
                                            //菜单具体设置弹窗dismiss回调,更新菜单变量状态
                                            isInDialog = false;
                                            addLevelTirView(tirSettingIndex);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            } else {
                //根据style添加不同view
                if (style != null) {
                    WedsSettingUtils.getInstance().switchStyleLayout(secJsonObject, llTirContain, context);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_bottom:
                loadingDialog = SettingMotionDialog.getInstance();
                loadingDialog.showLoadingDialog(context, "配置保存中，请稍后...");
                InitDevices.getInstence().ResetDevices(WedsSettingUtils.getVariablesMap(), new InitDevices.SaveSettingInfoFinishCallBack() {
                    @Override
                    public void saveSettingInfoFinish() {
                        handler.sendEmptyMessageDelayed(0, 3000);
                    }
                });
                break;
        }
    }

    /**
     * 物理按键-标题views
     */
    private List<View> titleViews = new ArrayList<>();

    /**
     * 三级菜单view
     */
    private List<View> tirViews = new ArrayList<>();

    private List<JSONObject> jsonObjects = new ArrayList<>();
    /**
     * 是否进入第三层菜单
     */
    private boolean isInView = false;

    /**
     * 是否第一次进入
     */
    private boolean isFirstIn = true;

    /**
     * 是否弹出dialog
     */
    private boolean isInDialog = false;

    private ImageView priorSelImg;

    /**
     * 物理按键相应方法
     *
     * @param type 按键类型
     */
    public void physicalKeyDown(int type) {
        switch (type) {
            case EventConfig.UP_KEY_DOWN:
                if (isInDialog){
                    dialogShowUtils.physicalKeyDown(EventConfig.UP_KEY_DOWN);
                }else if (isInView) {
                    PhysicalButtonsUtils.getInstance().listViewUp(tirViews, false, new OnSettingTirViewReturnCallBack() {
                        @Override
                        public void onSettingTirViewReturn(View view,int index) {
                            if (priorSelImg != null) {
                                priorSelImg.setVisibility(View.GONE);
                            }
                            ImageView imageView = (ImageView) view.findViewById(R.id.iv_sel);
                            imageView.setVisibility(View.VISIBLE);
                            priorSelImg = imageView;
//                            imageView.requestFocus();
                            LogUtils.i("物理按键","滑动到"+100*index+"位置");
                            slTirContain.smoothScrollTo(0,100*index);
                        }
                    });
                } else {
                    PhysicalButtonsUtils.getInstance().listViewUp(titleViews, true, null);
                }
                break;
            case EventConfig.DOWN_KEY_DOWN:
                if (isInDialog){
                    dialogShowUtils.physicalKeyDown(EventConfig.DOWN_KEY_DOWN);
                }else if (isInView) {
                    PhysicalButtonsUtils.getInstance().listViewDown(tirViews, false, new OnSettingTirViewReturnCallBack() {
                        @Override
                        public void onSettingTirViewReturn(View view,int index) {
                            if (priorSelImg != null) {
                                priorSelImg.setVisibility(View.GONE);
                            }
                            ImageView imageView = (ImageView) view.findViewById(R.id.iv_sel);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.requestFocus();
                            priorSelImg = imageView;
                            LogUtils.i("物理按键","滑动到"+100*index+"位置");
                            slTirContain.smoothScrollTo(0,100*index);
                        }
                    });
                } else {
                    PhysicalButtonsUtils.getInstance().listViewDown(titleViews, true, null);
                }
                break;
            case EventConfig.ENTER_KEY_DOWN:
                if (isInDialog){
                    dialogShowUtils.physicalKeyDown(EventConfig.ENTER_KEY_DOWN);
                }else if (isInView) {
                    //进去了三级菜单
                    PhysicalButtonsUtils.getInstance().listViewEnter(tirViews);
                } else if (isFirstIn) {
                    if (titleViews != null && titleViews.size()>0) {
                        //设置选中状态
                        View firstTitleView = titleViews.get(0);
                        TextView tvTitleName = (TextView) firstTitleView.findViewById(R.id.tv_advanced_title_name);
                        tvTitleName.setTextColor(Color.WHITE);
                        tvTitleName.setBackgroundResource(R.drawable.setting_advanced_title_button_back);
                        priorTitleText = tvTitleName;
                    }
                    isFirstIn = false;
                } else {
                    isInView = true;
                    if (tirViews != null && tirViews.size() > 0) {
                        ImageView imageView = (ImageView) tirViews.get(0).findViewById(R.id.iv_sel);
                        imageView.setVisibility(View.VISIBLE);
                        priorSelImg = imageView;
                    }
                }
                break;
            case EventConfig.CANCEL_KEY_DOWN:
                if (isInDialog){//进入了dialog
                    dialogShowUtils.physicalKeyDown(EventConfig.CANCEL_KEY_DOWN);
                }else if (isInView) {
                    if (priorSelImg != null) {
                        priorSelImg.setVisibility(View.GONE);
                    }
                    isInView = false;
                } else {
                    if (onSettingReturnCallBack != null) {
                        //返回上一级菜单
                        onSettingReturnCallBack.settingReturn();
                        if (priorTitleText != null) {
                            priorTitleText.setTextColor(getResources().getColor(R.color.B8));
                            priorTitleText.setBackgroundColor(Color.WHITE);
                        }
                        isFirstIn = true;
                        PhysicalButtonsUtils.getInstance().removeOneIndexCatch(titleViews);//清除索引
                        PhysicalButtonsUtils.getInstance().removeOneIndexCatch(tirViews);//清除索引
                    }
                }
                break;
        }
    }

    /**
     * 侧边栏点击改变
     *
     * @param index
     */
    public void leftChange(int index) {
        //重选左侧边时重置
        addAdvancedTitleView(index);
        addLevelTirView(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        PhysicalButtonsUtils.getInstance().removeOneIndexCatch(titleViews);//清除索引
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
