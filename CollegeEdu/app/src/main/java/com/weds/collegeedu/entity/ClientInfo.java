package com.weds.collegeedu.entity;

import java.util.List;

import lombok.Data;

/**
 * Created by lxy on 2016/12/9.
 */
@Data
public class ClientInfo {

    public ClientInfo(String terminalIsMaster, List<String> slaveList){
        this.terminalIsMaster = terminalIsMaster;
        this.slaveList = slaveList;
    }
    /*
    * 1-主机 0-从机
    * */
    private String terminalIsMaster = "0";
    /*
    * 从机ip列表
    * */
    private List<String> slaveList;
}
