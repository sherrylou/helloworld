package com.weds.collegeedu.datafile;

import static com.weds.collegeedu.resfile.ConstantConfig.AppArchivePath;

/**
 * Created by Administrator on 2016/11/14.
 */

public class WeatherFile extends BaseFile {
    public static final String rq = "rq";
    public static final String wd = "wd";
    public static final String tq = "tq";
    public static final String tp = "tp";


    private static String fileName = AppArchivePath+"weather.wts";
    private static String separator = ",";
    private static String fileIndex = "0";
    private static String fingerIndex = "";
    private static String fingerPath = "";
    private static String versionIndex = "1";
    private static WeatherFile weatherFile = null;

    private WeatherFile() {
        super(weatherFile, fileName, separator, fileIndex, fingerIndex, fingerPath, versionIndex);
    }

    public static WeatherFile getInstence() {
        if (weatherFile == null) {
            weatherFile = new WeatherFile();
        }
        return weatherFile;
    }
}
