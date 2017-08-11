package com.weds.settings.parse;

import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by lip on 2016/10/24.
 * <p>
 * 菜单json解析类
 */
public class FastJsons {

    public static JSONArray toJsonArray(String json) {
        return JSON.parseArray(json);
    }

    public static JSONArray toJsonArray(File json) {
        try {
            JSONReader reader = new JSONReader(new FileReader(json));
            return toJsonArray(reader.readString());
        } catch (FileNotFoundException e) {
            LogUtils.i("菜单异常","找不到json文件！");
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject toJsonObject(String json) {
        return JSON.parseObject(json);
    }

    public static JSONObject toJsonObject(JSONArray jsonArray, int position) {
        return jsonArray.getJSONObject(position);
    }

}
