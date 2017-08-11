package com.weds.collegeedu.datainterface;


import android.util.Log;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;

import com.weds.collegeedu.datafile.TeacherFile;
import com.weds.collegeedu.entity.SchoolPerson;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/11/24.
 * 老师接口文件
 */

public class TeacherInterface {
    private SchoolPerson teacher = new SchoolPerson();
    private static TeacherInterface teacherInterface = null;

    public static TeacherInterface getInstence() {
        if (teacherInterface == null) {
            teacherInterface = new TeacherInterface();
        }
        return teacherInterface;
    }

    /**
     * 根据学生序号获取学生信息
     *
     * @param data
     * @return
     */
    public SchoolPerson checkDataFromTeacherNo(String data) {
        int ret = 0;
        SchoolPerson schoolPerson = null;

        ret = TeacherFile.getInstence().FileIndexOperationFind(TeacherFile.ryxh, data);
        if (ret <= 0) {
            return new SchoolPerson();
        }
        String ryxh = TeacherFile.getInstence().GetData(TeacherFile.ryxh);
        String rybm = TeacherFile.getInstence().GetData(TeacherFile.rybm);
        String ryxm = TeacherFile.getInstence().GetData(TeacherFile.ryxm);
        String rylx = TeacherFile.getInstence().GetData(TeacherFile.rylx);
        String kh = TeacherFile.getInstence().GetData(TeacherFile.kh);
        String zw = TeacherFile.getInstence().GetData(TeacherFile.zw);
        String mm = TeacherFile.getInstence().GetData(TeacherFile.mm);
        String zp = TeacherFile.getInstence().GetData(TeacherFile.zp);
        String mj = TeacherFile.getInstence().GetData(TeacherFile.mj);
        String glk = TeacherFile.getInstence().GetData(TeacherFile.glk);
        String lxdh = TeacherFile.getInstence().GetData(TeacherFile.lxdh);
        String sfzh = TeacherFile.getInstence().GetData(TeacherFile.sfzh);
        LogUtils.i("档案回调信息教师姓名-----1",ryxm);
        return new SchoolPerson(ryxh, rybm, ryxm, rylx, kh, zw, mm, zp, mj, sfzh, "teacher", "", "", glk, lxdh);
    }

    /**
     * 根据卡号获取老师信息
     *
     * @param data
     * @return
     */
    public SchoolPerson checkDataFromTeacherCard(String data) {
        int ret = 0;
        SchoolPerson schoolPerson = null;

        ret = TeacherFile.getInstence().FileIndexOperationFind(TeacherFile.kh, data);
        if (ret <= 0) {
            return schoolPerson;
        }
        String ryxh = TeacherFile.getInstence().GetData(TeacherFile.ryxh);
        String rybm = TeacherFile.getInstence().GetData(TeacherFile.rybm);
        String ryxm = TeacherFile.getInstence().GetData(TeacherFile.ryxm);
        String rylx = TeacherFile.getInstence().GetData(TeacherFile.rylx);
        String kh = TeacherFile.getInstence().GetData(TeacherFile.kh);
        String zw = TeacherFile.getInstence().GetData(TeacherFile.zw);
        String mm = TeacherFile.getInstence().GetData(TeacherFile.mm);
        String zp = TeacherFile.getInstence().GetData(TeacherFile.zp);
        String mj = TeacherFile.getInstence().GetData(TeacherFile.mj);
        String glk = TeacherFile.getInstence().GetData(TeacherFile.glk);
        String lxdh = TeacherFile.getInstence().GetData(TeacherFile.lxdh);
        String sfzh = TeacherFile.getInstence().GetData(TeacherFile.sfzh);
        return new SchoolPerson(ryxh, rybm, ryxm, rylx, kh, zw, mm, zp, mj, sfzh, "teacher", "", "", glk, lxdh);
    }
}
