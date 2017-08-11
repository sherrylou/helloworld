package com.weds.collegeedu.datainterface;

import android.text.LoginFilter;
import android.util.Log;
import android.weds.lip_library.util.LogUtils;

import com.weds.collegeedu.datafile.CalendarFile;
import com.weds.collegeedu.datafile.HistoryCalendarFile;
import com.weds.collegeedu.entity.SubCalendar;

import java.util.List;


/**
 * Created by Administrator on 2016/11/28.\
 * 历史课程表
 */
public class HistoryCalendarInterface {
    private SubCalendar lastCalendar = null;//上节课程信息
    private SubCalendar historyCalendar = null; //历史课程信息
    private static HistoryCalendarInterface historyCalendarInterface;

    public static HistoryCalendarInterface getInstence() {
        if (historyCalendarInterface == null) {
            historyCalendarInterface = new HistoryCalendarInterface();
        }
        return historyCalendarInterface;
    }

    /**
     * 装载历史课程信息
     */
    public void loadDataToHistroyCalendar() {
        String fileLines = "1";
        int ret = 0;

        historyCalendar = null;
        ret = HistoryCalendarFile.getInstence().FileIndexOperationGetRows("0", fileLines);
        if (ret <= 0) {
            return;
        }
        String tsxh = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.tsxh);
        String skrq = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.skrq);
        String zhou = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.zhou);
        String kcjc = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.kcjc);
        String cc = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.cc);
        String kssk = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.kssk);
        String sksj = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.sksj);
        String cdsj = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.cdsj);
        String ztsj = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.ztsj);
        String xksj = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.xksj);
        String jssk = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.jssk);
        String kcxh = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.kcxh);
        String jsxh = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.jsxh);
        String bjxh = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.bjxh);
        String ltbs = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.ltbs);
        String sfks = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.sfks);
        String sfbxk = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.sfbxk);
        String name = "";
        name = CourseInterface.getInstence().checkDataFromCourseNo(kcxh).getCourseName();
        String x1 = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.x1);
        String x2 = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.x2);
        String jc = HistoryCalendarFile.getInstence().GetData(HistoryCalendarFile.jc);
        historyCalendar = new SubCalendar(tsxh, skrq, zhou, kcjc, cc, kssk, sksj, cdsj, ztsj, xksj, jssk, kcxh, jsxh, bjxh, ltbs, sfks, sfbxk, name, x1, x2 ,jc);
    }

    /**
     * 获取历史课程信息
     *
     * @return
     */
    public SubCalendar getHistoryCalendar() {
        return historyCalendar;
    }

    public int checkHistoryCalendarFromCurCalendar() {
        int ret = 0;
        List<SubCalendar> oneDayCalendars = CalendarInterface.getInstence().getTheCalendars();
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();

        //因为会有"5-6"这样的节次字符串出现，所以要判断
        String currentSubsuji = curCalendar.getSubsuji();
        String currentSubNo = curCalendar.getSubNo();
        String currentCc = curCalendar.getTextsuji();
        String lastSubsuji,lastCc;
        String history_Jc,hitoryCc;
        String lastSubNo;
        int curJc = 0;
        int lastJc = 0;
        int sign = 0;
        //判断当前课节是否合并了选修
        if (currentSubsuji.contains("-")) {
            //合并
            String[] split = currentSubsuji.split("-");
            curJc = Integer.valueOf(split[0]);
        } else {
            //未合并
            if (!currentSubsuji.equals("")){
                curJc = Integer.valueOf(currentSubsuji); //当前课程节次
            }
        }
        if(lastCalendar != null){
            lastSubsuji = lastCalendar.getSubsuji();
            if (lastSubsuji.contains("-")) {
                //合并
                String[] split = lastSubsuji.split("-");
                lastJc = Integer.valueOf(split[0]);
            } else {
                //未合并
                if (!lastSubsuji.equals("")){
                    lastJc = Integer.valueOf(lastSubsuji);
                }
            }
            lastSubNo = lastCalendar.getSubNo();
            lastCc = lastCalendar.getTextsuji();
        }else {
            LogUtils.i("lastcalendar---","--0");
            lastCalendar = curCalendar;
            if (historyCalendar == null) {
                historyCalendar = curCalendar;
                writeHistoryCalendar(historyCalendar);
                LogUtils.i("lastcalendar---","--1");
                return 1;
            }
            lastSubNo = lastCalendar.getSubNo();
            lastCc = lastCalendar.getTextsuji();
            sign = 1;
        }

        if (CalendarInterface.getInstence().getIsExam().equals("1")){
            //考试
            if ((currentCc.equals(lastCc) )&&(currentSubNo.equals(lastSubNo)) && sign !=1) {
                return 0;
            }

        }else {
            //教务
            if ((curJc == lastJc)&&(currentSubNo.equals(lastSubNo)) && sign !=1) {
                return 0;
            }
        }

        //上节课程和当前课程同步
        lastCalendar = curCalendar;

        /**
         * 临时处理
         */
        if (historyCalendar == null) {
            historyCalendar = curCalendar;
            writeHistoryCalendar(historyCalendar);
            LogUtils.i("lastcalendar---","--1");
            return 1;
        }

        //当前课程固定班级
        if (curCalendar.getClassNo().equals(ClassRoomInterface.getInstence().getClassRoom().getFixeNo())) {
            //检测时间、固定班级是否一致
            LogUtils.i("课程更新", curCalendar + "----" + historyCalendar + "-----");
            if (!curCalendar.getData().equals(historyCalendar.getData())) {
                //更新历史课程信息，清除已到人员
                writeHistoryCalendar();
                CurPersonnelInterface.getInstence().clearCurPersonnel();
                LogUtils.i("lastcalendar---","--3");
                ret = 1;
            }
        } else {
            //是选修课
            int i = 0;
            int historyJc=0;
            history_Jc = historyCalendar.getSubsuji();
            String historySubNo = historyCalendar.getSubNo();
            if (history_Jc.contains("-")) {
                //合并
                String[] split = history_Jc.split("-");
                historyJc = Integer.valueOf(split[0]);
            } else {
                //未合并
                if (!history_Jc.equals("")){
                    historyJc = Integer.valueOf(history_Jc); //当前课程节次
                }
            }

            if (!curCalendar.getData().equals(historyCalendar.getData())) {
                //更新历史课程信息，清除已到人员
                historyCalendar = curCalendar;
                writeHistoryCalendar(historyCalendar);
                CurPersonnelInterface.getInstence().clearCurPersonnel();
                LogUtils.i("lastcalendar---","--3");
                ret = 1;
            }
            hitoryCc = historyCalendar.getTextsuji();
            if (CalendarInterface.getInstence().getIsExam().equals("1")){
                //考试
                if ((!currentCc.equals(hitoryCc))||(!currentSubNo.equals(historySubNo))){
                    historyCalendar = curCalendar;
                    writeHistoryCalendar(historyCalendar);
                    CurPersonnelInterface.getInstence().clearCurPersonnel();
                    ret = 1;
                }

            }else {
                //教务
                if ((curJc != historyJc)||(!currentSubNo.equals(historySubNo))){
                    historyCalendar = curCalendar;
                    writeHistoryCalendar(historyCalendar);
                    CurPersonnelInterface.getInstence().clearCurPersonnel();
                    LogUtils.i("lastcalendar---","--2");
                    ret = 1;
                }
            }

        }
        if (sign ==1){
            ret =1;

        }
        return ret;
    }

    /**
     * 写入课程信息作为历史课程信息
     */
    private void writeHistoryCalendar(SubCalendar curCalendar) {
        String data = getDataFromCurCalendar(curCalendar);
        LogUtils.i("写入历史数据",data);
        HistoryCalendarFile.getInstence().FileDelete();
        HistoryCalendarFile.getInstence().LoadDataToMemory();
        HistoryCalendarFile.getInstence().FileIndexOperationAddRow(data);
        loadDataToHistroyCalendar();
    }

    /**
     * 写入当前课程信息到历史课程文件
     */
    private void writeHistoryCalendar() {
        SubCalendar curCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        String data = getDataFromCurCalendar(curCalendar);
        LogUtils.i("写入历史数据",data);

        HistoryCalendarFile.getInstence().FileDelete();
        HistoryCalendarFile.getInstence().LoadDataToMemory();
        HistoryCalendarFile.getInstence().FileIndexOperationAddRow(data);
        loadDataToHistroyCalendar();
    }

    private String getDataFromCurCalendar(SubCalendar curCalendar) {
        String data;

        data = curCalendar.getTextNo();
        data += ",";
        data += curCalendar.getData();
        data += ",";
        data += curCalendar.getWeek();
        data += ",";
        data += curCalendar.getSubsuji();
        data += ",";
        data += curCalendar.getTextsuji();
        data += ",";
        data += curCalendar.getAllowSlotTime();
        data += ",";
        data += curCalendar.getStartTime();
        data += ",";
        data += curCalendar.getLateTime();
        data += ",";
        data += curCalendar.getEarlyTime();
        data += ",";
        data += curCalendar.getDownTime();
        data += ",";
        data += curCalendar.getEndSlotTime();
        data += ",";
        data += curCalendar.getSubNo();
        data += ",";
        data += curCalendar.getClassroomNo();
        data += ",";
        data += curCalendar.getClassNo();
        data += ",";
        data += curCalendar.getIsContinue();
        data += ",";
        data += curCalendar.getIsText();
        data += ",";
        data += curCalendar.getIsFixed();
        return data;
    }

    /**
     * 清空历史课程信息
     */
    public void clearHistoryCalendar() {
        HistoryCalendarFile.getInstence().FileDelete();
        CurPersonnelInterface.getInstence().clearCurPersonnel();
        lastCalendar = null;//上节课程信息
        historyCalendar = null; //历史课程信息
    }
}
