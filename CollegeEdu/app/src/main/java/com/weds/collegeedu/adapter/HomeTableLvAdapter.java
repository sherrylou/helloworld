package com.weds.collegeedu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.weds.lip_library.adapter.CommonAdapter;
import android.weds.lip_library.adapter.ViewHolder;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.collegeedu.entity.SubCalendar;

/**
 * Created by lip on 2016/11/28.
 * <p>
 * 待机界面课程列表adapter
 */

public class HomeTableLvAdapter extends CommonAdapter<SubCalendar> {

    private Context context;

    public HomeTableLvAdapter(Context context, int layoutId) {
        super(context, layoutId);
        this.context = context;
    }

    @Override
    public void convert(ViewHolder vh, SubCalendar item) {
        View llRoot = vh.getView(R.id.ll_root);
        if (vh.getPosition()%2==1){
            llRoot.setBackgroundColor(context.getResources().getColor(R.color.Y12));
        }else{
            llRoot.setBackgroundColor(context.getResources().getColor(R.color.Y2));
        }
        String subName = "";
        String subTime = "";
        String subAmount = "";
        View tvSubAmount = vh.getView(R.id.tv_sub_amount);
        LogUtils.i("课程信息list", tvSubAmount.getTag()+"---"+item.getSubsuji() + "---" + item.getName() + "---" + item.getStartTime() + "-" + item.getDownTime());
        if (tvSubAmount.getTag()==null){
            subName = item.getName();
            subTime = item.getStartTime() + "-" + item.getDownTime();
            subAmount = item.getSubsuji();
            tvSubAmount.setTag(item.getStartTime());
        }else if (tvSubAmount.getTag().equals(item.getStartTime())){
            subName = item.getName();
            subTime = item.getStartTime() + "-" + item.getDownTime();
            subAmount = item.getSubsuji();
        }
        vh.setText(R.id.tv_sub_name, subName);
        vh.setText(R.id.tv_time,subTime);
        vh.setText(R.id.tv_sub_amount, subAmount);
    }
}
