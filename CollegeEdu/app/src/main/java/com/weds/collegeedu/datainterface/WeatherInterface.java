package com.weds.collegeedu.datainterface;


import com.weds.collegeedu.datafile.WeatherFile;
import com.weds.collegeedu.entity.Weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */

public class WeatherInterface {
    private Weather weather;
    private static WeatherInterface weatherInterface = null;

    public static WeatherInterface getInstence() {
        if (weatherInterface == null) {
            weatherInterface = new WeatherInterface();
        }
        return weatherInterface;
    }

    /**
     * 获取天气内容
     *
     * @return
     */
    public List<Weather> LoadWeatherToArray() {
        List<Weather> weathers = new ArrayList<>();
        int fileLines = 0;
        int ret = 0;

        fileLines = (int) WeatherFile.getInstence().FileIndexOperationGetRowsCount();
        ret = WeatherFile.getInstence().FileIndexOperationGetRows("0", String.valueOf(fileLines));
        if (ret <= 0) {
            return weathers;
        }
        for (int i = 0; i < ret; i++) {
            String rq = WeatherFile.getInstence().GetData(WeatherFile.rq);
            String wd = WeatherFile.getInstence().GetData(WeatherFile.wd);
            String tq = WeatherFile.getInstence().GetData(WeatherFile.tq);
            String tp = WeatherFile.getInstence().GetData(WeatherFile.tp);
            Weather weatherItem = new Weather(rq, wd, tq, tp);
            weathers.add(weatherItem);
        }
        WeatherFile.getInstence().ClearDataMaps();
        return weathers;
    }

    /**
     * 根据日期查询天气存放到weather类中
     *
     * @param Date
     * @return
     */
    public int CheckWeatherFromDate(String Date) {
        int ret = 0;
        ret = WeatherFile.getInstence().FileIndexOperationFind(WeatherFile.rq, Date);
//        if (ret <= 0) {
//            return ret;
//        }
        String rq = WeatherFile.getInstence().GetData(WeatherFile.rq);
        String wd = WeatherFile.getInstence().GetData(WeatherFile.wd);
        String tq = WeatherFile.getInstence().GetData(WeatherFile.tq);
        String tp = WeatherFile.getInstence().GetData(WeatherFile.tp);
        weather = new Weather(rq, wd, tq, tp);
        WeatherFile.getInstence().ClearDataMaps();
        return ret;
    }

    public Weather GetWeather() {
        return weather;
    }
}
