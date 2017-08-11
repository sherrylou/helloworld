package com.weds.settings.entity;

import java.util.List;

import lombok.Data;

/**
 * Created by lip on 2017/1/6.
 * wifi信息list
 */
@Data
public class WifiConfigurationList {

    /**
     * 列表
     */
    private List<WifiInfoConfiguration> wifiConfigurations;

}
