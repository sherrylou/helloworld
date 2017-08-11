package com.weds.collegeedu.bean;

import com.weds.collegeedu.entity.Mulitedia;
import com.weds.collegeedu.entity.Regular;

import java.util.List;

import lombok.Data;

/**
 * Created by lip on 2016/12/12.
 * 多媒体数据加规则
 */
@Data
public class MediaInfo {

    /**
     * 多媒体数据
     */
    List<Mulitedia> mulitedias;

    /**
     * 规则
     */
    Regular regular;

}
