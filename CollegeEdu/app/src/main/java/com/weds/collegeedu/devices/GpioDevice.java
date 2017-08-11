package com.weds.collegeedu.devices;

import android.util.Log;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.A23;
import com.weds.collegeedu.App;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.utils.WedsDataUtils;

import java.io.File;


/**
 * Created by Administrator on 2016/11/4.
 */

public class GpioDevice {
    //gpio操作类型
    public static char OpenGpio = 's';
    public static char PulseGpio = 'p';
    public static char CloseGpio = 'c';
    //gpio定义
    /**
     * <输入点1
     */
    public static int INPUT_1 = 0;
    /**
     * <输入点2
     */
    public static int INPUT_2 = 1;
    /**
     * <输入点3
     */
    public static int INPUT_3 = 2;
    /**
     * <输入点4
     */
    public static int INPUT_4 = 3;
    /**
     * <输入点5
     */
    public static int INPUT_5 = 4;
    /**
     * <输出点1
     */
    public static int OUT_1 = 5;
    /**
     * <输出点2
     */
    public static int OUT_2 = 6;
    /**
     * <红灯
     */
    public static int RED_LED = 7;
    /**
     * <绿灯
     */
    public static int GREEN_LED = 8;
    /**
     * <LCD设备
     */
    public static int LCD = 9;
    /**
     * <摄像头设备
     */
    public static int CAMERA = 10;
    /**
     * <GPRS_POWER  用于模块开关机
     */
    public static int GPRS_POWER = 11;
    /**
     * <GPRS_RESET
     */
    public static int GPRS_RESET = 12;
    /**
     * <打印机开启/关闭
     */
    public static int PRINTER_POWER = 13;
    /**
     * <条码开启/关闭
     */
    public static int BARCODE_POWER = 14;
    /**
     * <USB电源控制  1-外置卡 0-内置口
     */
    public static int USB_POWER = 15;
    /**
     * <GPRS 电源控制 用于模块电源开关
     */
    public static int GPRS_POWER_SUPPLY = 16;
    /**
     * 内置WIFI 模块电源控制口
     */
    public static int WIFI_POWER = 17;
    private static GpioDevice initGpioDevices;
    /**
     * GPIO配置文件
     */
    public static final String GPIODEVICESFILE = ConstantConfig.AppPartition+"gpio/gpio.ini";

    public GpioDevice() {
    }

    public static GpioDevice getInstence() {
        if (initGpioDevices == null) {
            initGpioDevices = new GpioDevice();
        }
        return initGpioDevices;
    }

    /**
     * 初始化gpio
     */
    public void initGpioDevices() {
        try {
            File f = new File(GPIODEVICESFILE);
            if (!f.exists()) {
                return;
            }

        } catch (Exception e) {

        }
        //初始化配置文件
        A23.initGpioDevices(Strings.changerStr2C(GPIODEVICESFILE));
        //初始化gpio监控线程
        new Thread(new GpioThread()).start();
        setGpioOff(GREEN_LED);
        setGpioOff(RED_LED);
        int ret = GpioDevice.getInstence().lcd_turn_on();
        if (ret == 1)
        {
            App.LcdState =  1;
        }
    }

    /**
     * 打开某个GPIO设备
     */
    public static int setGpioOn(int deviceType) {
        int retval = 0;
        retval = A23.setGpioOn(deviceType);
        return retval;
    }

    /**
     * 关闭某个GPIO设备
     */
    public static int setGpioOff(int deviceType) {
        int retval = 0;
        retval = A23.setGpioOff(deviceType);
        return retval;
    }

    /**
     * GPIO 看守函数,线程中定时调用
     *
     * @return
     */
    public static int watchGpioThread() {
        int retval = 0;
        LogUtils.i("gpio看守函数", "1111");
        retval = A23.threadWatchGpio();
        return retval;
    }

    /**
     * GPIO 操作函数
     *
     * @param optionType
     * @param mSec
     * @param devicesType
     * @return
     */
    public int setGpioDevices(char optionType, int mSec, int devicesType) {
        int ret = 0;
        ret = A23.optionGpio(optionType, mSec, devicesType);
        if (ret <= 0)
            return 0;
        return 1;
    }
    /**
     * 打开背光
     * @return
     */
    public int lcd_turn_on()
    {
        LogUtils.i("lcd_turn_on","lcd_turn_on");
        int ret = setGpioDevices('S',0,LCD);
        if (ret <= 0)
            return 0;
        return 1;
    }

    /**
     * 关闭背光
     * @return
     */
    public int lcd_turn_off()
    {
        LogUtils.i("lcd_turn_on","lcd_turn_off");
        int ret = setGpioDevices('C',0,LCD);
        if (ret <= 0)
            return 0;
        return 1;
    }


    /**
     * 获取某个GPIO的状态
     *
     * @param devicesType
     * @return
     */
    public static int getGpioDevicesStat(int devicesType) {
        int retval = 0;
        retval = A23.getImportStat(devicesType);
        return retval;
    }

    /**
     * gpio线程
     */
    private class GpioThread implements Runnable {

        @Override
        public void run() {
            watchGpioThread();
        }
    }
}
