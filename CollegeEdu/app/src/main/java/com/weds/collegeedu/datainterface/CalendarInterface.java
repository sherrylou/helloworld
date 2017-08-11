package com.weds.collegeedu.datainterface;

import android.util.Log;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.collegeedu.App;
import com.weds.collegeedu.datafile.CalendarFile;
import com.weds.collegeedu.entity.CourseTable;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.utils.GetTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/23.
 * 课时表
 */

public class CalendarInterface {
    private  SubCalendar currentCalendar = new SubCalendar();
    private  List<SubCalendar> oneDayCalendars = null;
    private  Map<Integer, List<SubCalendar>> oneWeekCalendars = new HashMap<>();
    private static CalendarInterface calendarInterface = null;
    private  List<CourseTable> courseTables = new ArrayList<>();

    public static CalendarInterface getInstence() {
        if (calendarInterface == null) {
            calendarInterface = new CalendarInterface();
        }
        return calendarInterface;
    }

    Comparator sortComparatorBySubCalendar = new Comparator() {
        public int compare(Object o1, Object o2) {
            SubCalendar p1 = (SubCalendar) o1;
            SubCalendar p2 = (SubCalendar) o2;
            if (p1.getIsText().equals("1")){
                if (Strings.isNotEmpty(p1.getTextsuji()) && Strings.isNotEmpty(p2.getTextsuji())) {
                    return (Integer.valueOf(p1.getTextsuji()) - Integer.valueOf(p2.getTextsuji()));
                }
            }
            if (p1.getSubsuji().length() > 2 || p2.getSubsuji().length() > 2) {
                return 0;
            }
            if (Strings.isNotEmpty(p1.getSubsuji()) && Strings.isNotEmpty(p2.getSubsuji())) {
                return (Integer.valueOf(p1.getSubsuji()) - Integer.valueOf(p2.getSubsuji()));
            }
            return 0;
        }
    };

    public static List<SubCalendar> sortBySomeFiled(List<SubCalendar> objects) {
        List<SubCalendar> list = new ArrayList<>();
        for (SubCalendar object : objects) {
            list.add(object);
        }
        for (int i = 0; i < objects.size(); i++) {
            LogUtils.i("课程信息排序", list.size() + "------" + i);
            if (objects.get(i).getIsText().equals("0")) {
                list.set(Integer.valueOf(objects.get(i).getSubsuji()) - 1, objects.get(i));
            } else {
                list.set(Integer.valueOf(objects.get(i).getTextsuji()) - 1, objects.get(i));
            }
        }
        for (SubCalendar subCalendar : list) {
            LogUtils.i("课程信息排序", subCalendar.getData() + subCalendar.getSubsuji() + subCalendar.getName());
        }
        return list;
    }

    /**
     * 根据日期获取课时表
     *
     * @param date
     */
    public List<SubCalendar> checkArraryFromDate(String date) {
        int i = 0;
        int ret = 0, fileLines = 0;
        List<SubCalendar> calendars = new ArrayList<>();

        calendars.clear();
        ret = CalendarFile.getInstence().FileIndexOperationSetFilter("==", CalendarFile.skrq, date);
        if (ret <= 0) {
            return calendars;
        }
        ret = CalendarFile.getInstence().FileIndexOperationGetFilterRows("0", String.valueOf(ret));
        if (ret <= 0) {
            return calendars;
        }
        for (i = 0; i < ret; i++) {
            String tsxh = CalendarFile.getInstence().GetData(CalendarFile.tsxh, i);
            String skrq = CalendarFile.getInstence().GetData(CalendarFile.skrq, i);
            String zhou = CalendarFile.getInstence().GetData(CalendarFile.zhou, i);
            String kcjc = CalendarFile.getInstence().GetData(CalendarFile.kcjc, i);
            String cc = CalendarFile.getInstence().GetData(CalendarFile.cc, i);
            String kssk = CalendarFile.getInstence().GetData(CalendarFile.kssk, i);
            String sksj = CalendarFile.getInstence().GetData(CalendarFile.sksj, i);
            String cdsj = CalendarFile.getInstence().GetData(CalendarFile.cdsj, i);
            String ztsj = CalendarFile.getInstence().GetData(CalendarFile.ztsj, i);
            String xksj = CalendarFile.getInstence().GetData(CalendarFile.xksj, i);
            String jssk = CalendarFile.getInstence().GetData(CalendarFile.jssk, i);
            String kcxh = CalendarFile.getInstence().GetData(CalendarFile.kcxh, i);
            String jsxh = CalendarFile.getInstence().GetData(CalendarFile.jsxh, i);
            String bjxh = CalendarFile.getInstence().GetData(CalendarFile.bjxh, i);
            String ltbs = CalendarFile.getInstence().GetData(CalendarFile.ltbs, i);
            String sfks = CalendarFile.getInstence().GetData(CalendarFile.sfks, i);
            String sfbxk = CalendarFile.getInstence().GetData(CalendarFile.sfbxk, i);
            String x1 = CalendarFile.getInstence().GetData(CalendarFile.x1, i);
            String x2 = CalendarFile.getInstence().GetData(CalendarFile.x2, i);
            String jc = CalendarFile.getInstence().GetData(CalendarFile.jc, i);
            String name = "";
            name = CourseInterface.getInstence().checkDataFromCourseNo(kcxh).getCourseName();
            calendars.add(new SubCalendar(tsxh, skrq, zhou, kcjc, cc, kssk, sksj, cdsj, ztsj, xksj, jssk, kcxh, jsxh, bjxh, ltbs, sfks, sfbxk, name, x1, x2, jc));
        }
        CalendarFile.getInstence().ClearDataMaps();
        Collections.sort(calendars, sortComparatorBySubCalendar);
        return calendars;
    }

//    /**
//     * 装载一周课表
//     */
//    public int loadOneWeekCalendars() {
//        int ret = 0;
//        oneWeekCalendars.clear();
//        List<String> sevendate = GetTime.getSevendate();
//        LogUtils.i("课表日期", "begin");
//        for (int i = 0; i < sevendate.size(); i++) {
//            String s = sevendate.get(i);
//            LogUtils.i("课表日期", s);
//            oneWeekCalendars.put(i, checkArraryFromDate(s));
//        }
//        LogUtils.i("课表日期", "end");
//
//        return ret;
//    }

    /**
     * 获取一周课表
     *
     * @return
     */
    public Map<Integer, List<SubCalendar>> getOneWeekCalendars() {
//        String dateTime = App.getLocalDate(Dates.FORMAT_TT_DATE);
//
//        if (oneDayCalendars == null) {
//            loadOneWeekCalendars();
//        }
//        List<String> sevendate = GetTime.getSevendate();
//        for (int i = 0; i < oneDayCalendars.size(); i++) {
//            String s = oneDayCalendars.get(i).getData();
//            if (!s.equals(dateTime)) {
//                loadOneWeekCalendars();
//                break;
//            }
//        }
        return oneWeekCalendars;
    }

    /**
     * 装载一周课表
     */
    public int loadOneWeekCalendars() {
        int ret = 0;
        oneWeekCalendars.clear();
        courseTables.clear();
        List<String> sevendate = GetTime.getSevendate();
        for (int i = 0; i < sevendate.size(); i++) {
            String s = sevendate.get(i);
            List<SubCalendar> subCalendars = checkArraryFromDate(s);
            oneWeekCalendars.put(i, subCalendars);
        }
        int courses = 13;
        for (Map.Entry<Integer, List<SubCalendar>> integerListEntry : oneWeekCalendars.entrySet()) {
            if (courses < integerListEntry.getValue().size()){
                courses = integerListEntry.getValue().size();
            }
        }
        for (int i = 0; i < courses; i++) {
            CourseTable ct = new CourseTable();
            for (int j = 0; j < sevendate.size(); j++) {
                List<SubCalendar> subCalendars = oneWeekCalendars.get(j);
                if (subCalendars.size() > i) {
                    SubCalendar subCalendar = subCalendars.get(i);
                    switch (j) {
                        case 0:
                            ct.setWeek_1_courseName(subCalendar.getName());
                            break;
                        case 1:
                            ct.setWeek_2_courseName(subCalendar.getName());
                            break;
                        case 2:
                            ct.setWeek_3_courseName(subCalendar.getName());
                            break;
                        case 3:
                            ct.setWeek_4_courseName(subCalendar.getName());
                            break;
                        case 4:
                            ct.setWeek_5_courseName(subCalendar.getName());
                            break;
                        case 5:
                            ct.setWeek_6_courseName(subCalendar.getName());
                            break;
                        case 6:
                            ct.setWeek_7_courseName(subCalendar.getName());
                            break;
                    }
                }
            }
            courseTables.add(ct);
        }
        return ret;
    }

    public List<CourseTable> getOneWeekCourseTable() {
        return courseTables;
    }

    /**
     * 装载一天课表
     */
    public List<SubCalendar> loadOneDayCalendars(String data) {
        return checkArraryFromDate(App.getLocalDate(Dates.FORMAT_TT_DATE));
    }

    /**
     * 装载一天课表
     *
     * @return
     */
    public int loadOneDayCalendars() {
        int ret = 0;
        oneDayCalendars = checkArraryFromDate(App.getLocalDate(Dates.FORMAT_TT_DATE));
        return ret;
    }

    /*
    * 获取教学周
    * */
    public String getTeachZhou() {
        return oneDayCalendars.get(0).getWeek();
    }

    /*
    * 获取是否考试
    * */
    public String getIsExam() {
        String isText = "0";
        SubCalendar currentCalendar = calendarInterface.getCurrentCalendar();
        if (currentCalendar == null ) {
                return isText;
        }
        return currentCalendar.getIsText();
    }

    /**
     * 获取当天课程列表
     *
     * @param isMerge 是否合并选修
     * @return
     */
    public synchronized List<SubCalendar> getOneDayCalendars(boolean isMerge) {
        String dateTime = App.getLocalDate(Dates.FORMAT_TT_DATE);
        if (oneDayCalendars == null || oneDayCalendars.size() <= 0) {
            oneDayCalendars = checkArraryFromDate(dateTime);
        } else {
//            if (!dateTime.equals(oneDayCalendars.get(0).getData())) {
                oneDayCalendars = checkArraryFromDate(dateTime);
//            }
        }
        if (isMerge) {
            oneDayCalendars = mergeSubCalendar(oneDayCalendars);
        }
        return oneDayCalendars;
    }

    public List<SubCalendar> getTheCalendars() {
        return oneDayCalendars;
    }

//    /**
//     * 合并选修
//     *
//     * @param oneDayCalendars
//     * @return
//     */
//    private List<SubCalendar> mergeSubCalendars(List<SubCalendar> oneDayCalendars) {
//        //连堂合并
//        SubCalendar priorSub = null;
//        for (int i = 0; i < oneDayCalendars.size(); i++) {
//            SubCalendar subCalendar = oneDayCalendars.get(i);
//            if (priorSub != null && Strings.isNotEmpty(priorSub.getName()) && Strings.isNotEmpty(subCalendar.getName()) && priorSub.getName().equals(subCalendar.getName())) {
//                //上节和这节课为同一节，连堂
//                for (int j = 0; j < i; j++) {
//                    //向上循环遍历
//                    SubCalendar upSub = oneDayCalendars.get(j);
//                    if (Strings.isNotEmpty(upSub.getName()) && Strings.isNotEmpty(subCalendar.getName()) && upSub.getName().equals(subCalendar.getName())) {
//                        subCalendar.setStartTime(upSub.getStartTime());
//                        subCalendar.setAllowSlotTime(upSub.getAllowSlotTime());
//                        if(!upSub.getSubsuji().contains("-")){
//                            subCalendar.setSubsuji(upSub.getSubsuji() + "-" + subCalendar.getSubsuji());
//                        }else{
//                            String[] jc = upSub.getSubsuji().split("-");
//                            subCalendar.setSubsuji(jc[0] + "-" + subCalendar.getSubsuji());
//                        }
//                        oneDayCalendars.remove(j);
//                        break;
//                    }
//                }
//            }
//            LogUtils.i("当天课程--",subCalendar.toString());
//            priorSub = subCalendar;
//        }
//        return oneDayCalendars;
//    }

    /**
     * 合并选修
     *
     * @param oneDayCalendars
     * @return
     */
    private synchronized List<SubCalendar> mergeSubCalendar(List<SubCalendar> oneDayCalendars) {
        //连堂合并
        SubCalendar priorSub = null;
        List<SubCalendar> mergeCalendars = new ArrayList<>();
        for (int i = 0; i < oneDayCalendars.size(); i++) {
            SubCalendar subCalendar = oneDayCalendars.get(i);
            if (!subCalendar.getIsContinue().equals("1")) {
                mergeCalendars.add(subCalendar);
            } else if (priorSub != null) {
                mergeCalendars.remove(priorSub);
                subCalendar.setStartTime(priorSub.getStartTime());
                subCalendar.setAllowSlotTime(priorSub.getAllowSlotTime());
                subCalendar.setIsContinue("0");
                if (!priorSub.getSubsuji().contains("-")) {
                    subCalendar.setSubsuji(priorSub.getSubsuji() + "-" + subCalendar.getSubsuji());
                } else {
                    String[] jc = priorSub.getSubsuji().split("-");
                    subCalendar.setSubsuji(jc[0] + "-" + subCalendar.getSubsuji());
                }
                mergeCalendars.add(subCalendar);
            }
            priorSub = subCalendar;
            LogUtils.i("当天课程--", mergeCalendars.toString());
        }
        return mergeCalendars;
    }


    /**
     * 设置当前课程
     *
     * @param data
     */
    public void setCurrentCalendar(SubCalendar data) {
        currentCalendar = data;
    }

    /**
     * 获取当前课程
     *
     * @return
     */
    public SubCalendar getCurrentCalendar() {
        if (currentCalendar == null) {
            currentCalendar = new SubCalendar();
        }
        return currentCalendar;
    }

    /**
     * 获取后天第一节课
     */
    public SubCalendar getNextDayCalendar() {
        List<SubCalendar> nextDayCalendars = checkArraryFromDate(GetTime.getNextDay(Dates.FORMAT_TT_DATE));
        if (nextDayCalendars.size() <= 0) {
            return null;
        }
        return nextDayCalendars.get(0);
    }
}
