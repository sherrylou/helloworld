package com.weds.settings.ui;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.InitDevices;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.settings.utils.WedsSettingUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.settings.utils.WedsSettingUtils.NAME;
import static com.weds.settings.utils.WedsSettingUtils.SUBMENU;


/**
 * Created by lip
 * 菜单向导Fragment
 */
public class SettingGuideFragment extends BaseFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    @Bind(R.id.ll_setting_right)
    LinearLayout llSettingRight;
    @Bind(R.id.ll_tir_contain)
    LinearLayout llTirContain;
    @Bind(R.id.tv_bottom)
    TextView tvBottom;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.tv_guide_finish)
    TextView tvGuideFinish;


    private static final String INDEX = "index";
    @Bind(R.id.tv_prior_button)
    TextView tvPriorButton;

    /**
     * 二级引导菜单
     */
    private JSONArray secGuideJsonArray;

    /**
     * 标题view集合
     */
    private List<View> titleViews = new ArrayList<>();

    private int count;
    private View guideLayout;
    private JSONArray firstJsonArray;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    loadingDialog.dismissLoadingDialog();
                    //显示配置完成
                    llRoot.setVisibility(View.GONE);
                    tvGuideFinish.setVisibility(View.VISIBLE);
                    tvPriorButton.setVisibility(View.GONE);
                    break;
            }
        }
    };
    private SettingMotionDialog loadingDialog;

    public SettingGuideFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingGuideFragment.
     */
    public static SettingGuideFragment newInstance() {
        SettingGuideFragment fragment = new SettingGuideFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_guide, container, false);
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
        addGuideTitleView(0);
        addLevelTirView(0, 0);
        tvBottom.setText("下一步");
    }

    private void registerListener() {
        tvBottom.setOnClickListener(this);
        tvPriorButton.setOnClickListener(this);
    }

    /**
     * 添加向导标题菜单
     *
     * @param index
     */
    private void addGuideTitleView(int index) {
        //清空原先的子view
        llSettingRight.removeAllViews();
        titleViews.clear();
        //添加
        if (firstJsonArray != null && firstJsonArray.size() > index) {
            //获取index对应的一级类
            JSONObject firstJsonObject = (JSONObject) firstJsonArray.get(index);
            //二级菜单索引
            Integer secIndex = Integer.valueOf((String) firstJsonObject.get(WedsSettingUtils.NEXT_GUIDE_INDEX));
            //获取二级菜单
            secGuideJsonArray = WedsSettingUtils.getSecJsonArray(secIndex, firstJsonObject.getJSONArray(SUBMENU));
            if (secGuideJsonArray != null && secGuideJsonArray.size() > 0) {
                guideLayout = LayoutInflater.from(context).inflate(R.layout.setting_guide_amount_layout, null);
                ((TextView) guideLayout.findViewById(R.id.tv_setting_guide_start_name)).setText(((JSONObject) secGuideJsonArray.get(0)).getString("name"));//设置开始向导名称
                ((TextView) guideLayout.findViewById(R.id.tv_guide_end_amount)).setText(String.valueOf(secGuideJsonArray.size() + 1));//结束时的步数
                ((TextView) guideLayout.findViewById(R.id.tv_setting_guide_end_name)).setText("配置完成");//设置结束向导名称
                LinearLayout centerGuideView = (LinearLayout) guideLayout.findViewById(R.id.ll_guide_center);
                for (int i = 0; i < secGuideJsonArray.size(); i++) {
                    if (i == 0) {//如果只有一步，跳出
                        continue;
                    }
                    View centerView = LayoutInflater.from(context).inflate(R.layout.setting_guide_center_layout, null);
                    ((TextView) centerView.findViewById(R.id.tv_setting_guide_name)).setText(((JSONObject) secGuideJsonArray.get(i)).getString("name"));//设置向导名称
                    ((TextView) centerView.findViewById(R.id.tv_guide_amount)).setText(String.valueOf(i + 1));//节数步数
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.setting_guide_sec_top_height));
                    centerView.setLayoutParams(lp);
                    centerGuideView.addView(centerView);
                    titleViews.add(centerView);
                }
                //设置步骤导航栏位置
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.setting_guide_sec_top_height));
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                guideLayout.setLayoutParams(lp);
                //添加
                llSettingRight.addView(guideLayout);

            }
        }
    }

    /**
     * 添加三级菜单具体内容
     *
     * @param index 索引
     * @param state 向前向后 0-下一步
     */
    private void addLevelTirView(int index, int state) {
        if (secGuideJsonArray != null) {
            //清空原先的子view
            llTirContain.removeAllViews();
            if (secGuideJsonArray.size() < index || index < 0) {
                //如果超过角标或index小于0
                return;
            } else if (secGuideJsonArray.size() == index) {
                //如果等于角标,配置完成界面显示
                return;
            }
            //获取二级菜单里index对应object
            JSONObject secJsonObject = (JSONObject) secGuideJsonArray.get(index);
            Integer tirIndex = Integer.valueOf(secJsonObject.getString(WedsSettingUtils.NEXT_GUIDE_INDEX));

            JSONArray tirContentJsonArray = secJsonObject.getJSONArray(SUBMENU);
            if (tirContentJsonArray != null && tirContentJsonArray.size() > 0) {
                //获取三级菜单
                JSONArray tirGuideJsonArray = tirContentJsonArray.getJSONArray(tirIndex);
                //添加
                if (tirGuideJsonArray.size() > 0) {
                    //如果引导三级菜单不为null
                    for (Object o : tirGuideJsonArray) {
                        JSONObject tirJsonObject = (JSONObject) o;
                        //带标题包含view
                        View tirView = LayoutInflater.from(context).inflate(R.layout.setting_tir_layout, null);
                        TextView tvTirTitleName = (TextView) tirView.findViewById(R.id.tv_tir_title_name);
                        tvTirTitleName.setText(tirJsonObject.getString(NAME));
                        LinearLayout llTirItemContain = (LinearLayout) tirView.findViewById(R.id.ll_tir_contain);
                        //根据style添加不同view
                        WedsSettingUtils.getInstance().switchStyleLayout(tirJsonObject, llTirItemContain, context);
                        //添加到三级菜单item上
                        LinearLayout.LayoutParams tirItemlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        tirView.setLayoutParams(tirItemlp);
                        llTirContain.addView(tirView);
                    }
                }
            }
        }
    }

    /**
     * 侧边栏点击改变
     *
     * @param index
     */
    public void leftChange(int index) {
        //重选左侧边时重置
        count = 0;
        llRoot.setVisibility(View.VISIBLE);
        tvGuideFinish.setVisibility(View.GONE);
        tvPriorButton.setVisibility(View.GONE);
        tvBottom.setText("下一步");
        addGuideTitleView(index);
        addLevelTirView(0, 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_bottom://点击下一步
                count++;
                setTitleColor(0);
                addLevelTirView(count, 0);
                break;
            case R.id.tv_prior_button:
                if (count != 0) {
                    //当为第一个时不再减小
                    count--;
                }
                setTitleColor(1);
                addLevelTirView(count, 1);
                break;
        }
    }

    /**
     * 上一步或下一步
     *
     * @param state 0-下一步
     */
    private void setTitleColor(int state) {
        LogUtils.i("count数目", "====" + titleViews.size() + "=====" + count + "=====");
        if (state == 0) {
            //设置下一个步骤的条目变色
            if (titleViews.size() > 0 && titleViews.size() > count - 1) {
                View view = titleViews.get(count - 1);
                CenterViewHolder centerViewHolder = new CenterViewHolder(view);
                centerViewHolder.llGuideCenterLeftLine.setBackgroundColor(getResources().getColor(R.color.B8));
                centerViewHolder.llGuideCenterRightLine.setBackgroundColor(getResources().getColor(R.color.B8));
                centerViewHolder.ivAmount.setImageResource(R.mipmap.guide_light);
                centerViewHolder.tvGuideAmount.setTextColor(getResources().getColor(R.color.B8));
                centerViewHolder.tvSettingGuideName.setTextColor(getResources().getColor(R.color.B8));
                tvPriorButton.setVisibility(View.VISIBLE);
                if (titleViews.size() == count) {
                    tvBottom.setText("完成");
                } else {
                    tvBottom.setText("下一步");
                }
            } else if (titleViews.size() == count - 1) {
                //配置完成
                LeftAndRightViewHolder leftAndRightViewHolder = new LeftAndRightViewHolder(guideLayout);
                leftAndRightViewHolder.llEndLeftLine.setBackgroundColor(getResources().getColor(R.color.B8));
                leftAndRightViewHolder.ivEndAmount.setImageResource(R.mipmap.guide_light);
                leftAndRightViewHolder.tvGuideEndAmount.setTextColor(getResources().getColor(R.color.B8));
                leftAndRightViewHolder.tvSettingGuideEndName.setTextColor(getResources().getColor(R.color.B8));
                leftAndRightViewHolder.llEndRightLine.setBackgroundResource(R.drawable.setting_guide_end_back);
                loadingDialog = SettingMotionDialog.getInstance();
                loadingDialog.showLoadingDialog(context,"配置保存中，请稍后...");
                //保存设置
                InitDevices.getInstence().ResetDevices(WedsSettingUtils.getVariablesMap(), new InitDevices.SaveSettingInfoFinishCallBack() {
                    @Override
                    public void saveSettingInfoFinish() {
                        //完成回调
                        handler.sendEmptyMessageDelayed(0, 3000);
                    }
                });
            }
        } else {
            //设置上一个
            if (titleViews.size() > 0 && titleViews.size() > count) {
                View view = titleViews.get(count);
                CenterViewHolder centerViewHolder = new CenterViewHolder(view);
                centerViewHolder.llGuideCenterLeftLine.setBackgroundColor(getResources().getColor(R.color.G3));
                centerViewHolder.llGuideCenterRightLine.setBackgroundColor(getResources().getColor(R.color.G3));
                centerViewHolder.ivAmount.setImageResource(R.mipmap.guide_black);
                centerViewHolder.tvGuideAmount.setTextColor(getResources().getColor(R.color.G3));
                centerViewHolder.tvSettingGuideName.setTextColor(getResources().getColor(R.color.G3));
                tvPriorButton.setVisibility(View.VISIBLE);
                if (count == 0) {
                    tvPriorButton.setVisibility(View.GONE);
                }
            }
        }
    }

    class CenterViewHolder {

        @Bind(R.id.ll_guide_center_left_line)
        LinearLayout llGuideCenterLeftLine;
        @Bind(R.id.iv_amount)
        ImageView ivAmount;
        @Bind(R.id.tv_guide_amount)
        TextView tvGuideAmount;
        @Bind(R.id.rl_guide_iv_contain)
        RelativeLayout rlGuideIvContain;
        @Bind(R.id.tv_setting_guide_name)
        TextView tvSettingGuideName;
        @Bind(R.id.ll_guide_center_right_line)
        LinearLayout llGuideCenterRightLine;

        CenterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class LeftAndRightViewHolder {

        @Bind(R.id.ll_guide_start_line)
        LinearLayout llGuideStartLine;
        @Bind(R.id.iv_start_amount)
        ImageView ivStartAmount;
        @Bind(R.id.tv_guide_start_amount)
        TextView tvGuideStartAmount;
        @Bind(R.id.rl_guide_start_iv_contain)
        RelativeLayout rlGuideStartIvContain;
        @Bind(R.id.tv_setting_guide_start_name)
        TextView tvSettingGuideStartName;
        @Bind(R.id.ll_guide_center)
        LinearLayout llGuideCenter;
        @Bind(R.id.ll_end_left_line)
        LinearLayout llEndLeftLine;
        @Bind(R.id.iv_end_amount)
        ImageView ivEndAmount;
        @Bind(R.id.tv_guide_end_amount)
        TextView tvGuideEndAmount;
        @Bind(R.id.rl_guide_end_iv_contain)
        RelativeLayout rlGuideEndIvContain;
        @Bind(R.id.tv_setting_guide_end_name)
        TextView tvSettingGuideEndName;
        @Bind(R.id.ll_end_right_line)
        LinearLayout llEndRightLine;


        LeftAndRightViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
