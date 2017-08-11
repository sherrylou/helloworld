package com.weds.collegeedu.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.weds.lip_library.util.FileUtils;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weds.collegeedu.App;
import com.weds.collegeedu.resfile.ConstantConfig;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.thread.MessageEvent;
import com.weds.settings.entity.MenuVariablesInfo;
import com.weds.settings.parse.FastJsons;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lip on 2016/12/2.
 * <p>{"a":["a.mp3","成功声音"]}
 * 声音工具类
 */

public class SoundUtils {
    private boolean isSilent = false;
    public static final String successMp3 = "success";
    public static final String errorMp3 = "error";
    private static Map<String, Integer> defaultSounds;
    private static SoundUtils soundUtils;
    private static SoundPool soundPool;
    private static int streamVolumeMax;

    private static final String soundRootPath = ConstantConfig.AppPartition+"music/";

    private static final String soundJsonPath = soundRootPath + "sound.json";

    private SoundUtils() {
    }

    public static SoundUtils getInstance() {
        if (soundUtils == null) {
            soundUtils = new SoundUtils();
        }
        return soundUtils;
    }

    /**
     * 初始化声音变量表
     */
    public void initSoundsVariales() {
        defaultSounds = (Map<String, Integer>) new HashMap<String, Integer>();
        defaultSounds.put(successMp3, -1);
        defaultSounds.put(errorMp3, -1);
        initSoundPool();
    }

    private void initSoundPool() {
        int soundId;
        String soundPath = "";
        JSONArray array;
        /**
         * 初始化段音频SoundPool
         */
        soundPool = new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 100);
        AudioManager mgr = (AudioManager) App.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        String jsonStr = FileUtils.readFile(soundJsonPath);
        if (!Strings.isNotEmpty(jsonStr)) {
            return;
        }
        JSONObject jsonObject = FastJsons.toJsonObject(jsonStr);
        if (jsonObject == null) {
            return;
        }
        //
        System.out.println("通过Map.entrySet遍历key和value");
        for (Map.Entry<String, Integer> entry : defaultSounds.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            array = jsonObject.getJSONArray(entry.getKey());
            if (array != null && Strings.isNotEmpty(array.getString(0))) {
                soundPath = soundRootPath + array.getString(0);
            } else {
                continue;
            }
            File okfile = new File(soundPath);
            if (!okfile.exists()) {
                continue;
            }
            soundId = soundPool.load(soundPath, 1);
            defaultSounds.put(entry.getKey(), soundId);
        }

    }


    /**
     * 播放Mp3语音
     */
    public void playMp3Sound(String soundKey) {

        int soundId = -1;

        soundId = defaultSounds.get(soundKey);

        if (soundId == -1) {
            return;
        }
        //静音媒体
        SilenceVideo();

        soundPool.play(soundId, streamVolumeMax, streamVolumeMax, 1, 0, 1.0f);
        //恢复媒体声音
        recoverVideo();
    }

    /**
     * 设置音量
     */
    public void initVolumeDevice() {
        int volume = 0;
        volume = Integer.valueOf(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysVolume));
        LogUtils.i("设置系统音量", volume + "=========1");
        AudioManager am = (AudioManager) App.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_SYSTEM, volume / 7, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_ALARM, volume / 7, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume / 7, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_RING, volume / 7, AudioManager.FLAG_PLAY_SOUND);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume / 7, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 恢复媒体声音
     */
    private void recoverVideo() {
        EventBus.getDefault().postSticky(new MessageEvent(EventConfig.VIEDO_RECL));
    }

    /**
     * 将媒体静音
     */
    private void SilenceVideo() {
        EventBus.getDefault().postSticky(new MessageEvent(EventConfig.SILENCE_VIDEO));
    }

    /**
     * 静音
     */

    public void selientVolumeDevice() {
        if (!isSilent) {
            LogUtils.i("声音<<<<静默", "=================");
            AudioManager am = (AudioManager) App.getAppContext().getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_PLAY_SOUND);
            isSilent = true;
        }
    }

    /**
     * 开启声音
     */
    public void openVolumeDevice() {
        if (isSilent) {
            LogUtils.i("声音<<<<开启", "=================");
            //获取声音默认值
            String value = "";
            int valueSys = 10;
            try {
                value = MenuVariablesInfo.getInstance().readVariableDataFromMap("SysVolume");
                valueSys = Integer.valueOf(value) / 7;
            } catch (Exception e) {
                LogUtils.i("声音默认值出错", "=========" + value + "========");
            }
            AudioManager am = (AudioManager) App.getAppContext().getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_SYSTEM, valueSys, AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_ALARM, valueSys, AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, valueSys, AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_RING, valueSys, AudioManager.FLAG_PLAY_SOUND);
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, valueSys, AudioManager.FLAG_PLAY_SOUND);
            isSilent = false;
        }
    }
}
