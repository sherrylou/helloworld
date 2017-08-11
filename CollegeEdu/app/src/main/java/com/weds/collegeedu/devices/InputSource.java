package com.weds.collegeedu.devices;

import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/8.
 * 输入源
 */

public class InputSource {
    private static String inputSources = "WEDS_DEVICES";
    private static Map<String, List<String>> inputSourceList = new HashMap();
    private static InputSource inputSource;

    public static InputSource getInstence() {
        if (inputSource == null) {
            inputSource = new InputSource();
        }
        return inputSource;
    }

    /**
     * 初始化输入源列表
     */
    public void initInputSourceList() {
        if (inputSourceList.size() > 0) {
            return;
        }
        inputSourceList.put("0", new ArrayList<String>() {{
            add("WEDS_DEVICES");
            add("威尔卡头");
        }});
        inputSourceList.put("1", new ArrayList<String>() {{
            add("RFID_0100D");
            add("RFID_0100D电信2.4G");
        }});
        inputSourceList.put("2", new ArrayList<String>() {{
            add("RFID_0201A");
            add("RFID_0201A电信2.4G");
        }});
        inputSourceList.put("3", new ArrayList<String>() {{
            add("TEXT_CARD");
            add("文本协议卡头");
        }});
        setInputSourceArray();
    }

    /**
     * 设置输入源
     */
    public void setInputSourceArray() {
        int i = 0;
        String data= MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysInputSource);
        LogUtils.i("setInputSourceArray","setInputSourceArray"+data);
        String[] datas = data.split(",");
        inputSources = "";
        for (String item : datas) {
            if (i == 0) {
                inputSources = inputSourceList.get(item).get(0);
            } else {
                inputSources = "," + inputSourceList.get(item).get(0);
            }
            i++;
        }
    }

    /**
     * 根据输入源获取数据
     *
     * @return
     */
    public String[] readDevicesData() {
        int ret = 0;
        byte[] bvalue = new byte[1024];
        byte[] bvalueType = new byte[1024];

        if (inputSources == null) {
            return null;
        }
        LogUtils.i("inputresource", inputSources);
        ret = A23.ReadDevicesData(Strings.changerStr2C(inputSources), bvalue, bvalueType);
        LogUtils.i("lxy---", "card111"+ret);
        if (ret < 0) {
            LogUtils.i("wxjwxjwxj", "card111"+ret);
            return null;
        }
        if (App.LcdState == 0 && !App.CurrentWakeTime.isEmpty())
        {
            int ret1 = GpioDevice.getInstence().lcd_turn_on();
            if (ret1 == 1 )
            {
                App.LcdState = 1;
                App.CurrentWakeTime = App.getHHMMSS();
            }
        }
        LogUtils.i("wxjwxjwxj-----", "---"+String.valueOf(ret)+ WedsDataUtils.ChangeCode(bvalue));
        return new String[]{String.valueOf(ret), WedsDataUtils.ChangeCode(bvalue), WedsDataUtils.ChangeCode(bvalueType)};
    }
}
