package com.weds.collegeedu.thread;

import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.datainterface.SimulationTestInterface;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.settings.entity.MenuVariablesInfo;

/**
 * Created by lxy on 2017/2/4.
 */

public class AutoCard implements Runnable {
    private boolean isContinue;
    private int ret = 0;

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    @Override
    public void run() {
        isContinue = true;
        SubCalendar curCalendar;
        while (isContinue) {
            ret = SimulationTestInterface.getInstence().testCardSend();
            curCalendar = CalendarInterface.getInstence().getCurrentCalendar();
            if (MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysPushCardEnable).equals("1") && curCalendar != null && !curCalendar.getState().equals("0")&& ret ==1) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
