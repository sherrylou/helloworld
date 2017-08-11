package com.weds.settings.adapter;

import android.content.Context;
import android.weds.lip_library.adapter.CommonAdapter;
import android.weds.lip_library.adapter.ViewHolder;
import android.widget.TextView;

import com.weds.collegeedu.R;
import com.weds.settings.entity.SysInfo;


/**
 * Created by lip on 2016/11/11.
 * <p>
 * 设备信息adapter
 */

public class SysInfoListAdapter extends CommonAdapter<SysInfo> {

    public SysInfoListAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void convert(ViewHolder vh, SysInfo item) {
        TextView tvSysInfoName = (TextView) vh.getView(R.id.tv_sys_info_name);
        TextView tvSysInfoContent = (TextView) vh.getView(R.id.tv_sys_info_content);
        tvSysInfoName.setText(item.getSysInfoName());
        tvSysInfoContent.setText(item.getSysInfoContent());
//        String contentTag = (String) tvSysInfoContent.getTag();
//        if (Strings.isNotEmpty(contentTag) && contentTag.equals(item.getSysInfoName())){
//
//            tvSysInfoContent.setTag(item.getSysInfoName());
//        }else if(Strings.isEmpty(contentTag)){
//            tvSysInfoContent.setText(item.getSysInfoContent());
//            tvSysInfoContent.setTag(item.getSysInfoName());
//        }
    }
}
