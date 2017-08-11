package com.weds.collegeedu.datainterface;


import com.weds.collegeedu.datafile.CourseFile;
import com.weds.collegeedu.entity.Course;

/**
 * Created by Administrator on 2016/11/23.
 */

public class CourseInterface {
    private static CourseInterface courseInterface = null;

    public static CourseInterface getInstence() {
        if (courseInterface == null) {
            courseInterface = new CourseInterface();
        }
        return courseInterface;
    }

    /**
     * 根据课程序号获取课程名
     * @param data
     * @return
     */
    public Course checkDataFromCourseNo(String data) {
        int ret = 0;


        ret = CourseFile.getInstence().FileIndexOperationFind(CourseFile.kcxh, data);
        if(ret<=0){
            return new Course();
        }
        String kcxh=CourseFile.getInstence().GetData(CourseFile.kcxh);
        String mc=CourseFile.getInstence().GetData(CourseFile.mc);
        String bm=CourseFile.getInstence().GetData(CourseFile.bm);
        return new Course(kcxh,mc,bm);
    }
}
