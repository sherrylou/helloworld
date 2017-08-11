package com.weds.collegeedu.datainterface;


import android.util.Log;

import com.weds.collegeedu.datafile.StudentFile;
import com.weds.collegeedu.entity.SchoolPerson;

/**
 * Created by Administrator on 2016/11/25.
 */

public class StudentInterface {
    private SchoolPerson student = new SchoolPerson();
    private static StudentInterface studentInterface = null;

    public static StudentInterface getInstence() {
        if (studentInterface == null) {
            studentInterface = new StudentInterface();
        }
        return studentInterface;
    }

    /**
     * 根据学生序号获取学生信息
     *
     * @param data
     * @return
     */
    public SchoolPerson checkDataFromStudentNo(String data) {
        int ret = 0;
        SchoolPerson schoolPerson = new SchoolPerson();
        ret = StudentFile.getInstence().FileIndexOperationFind(StudentFile.ryxh, data);
        if (ret <= 0) {
            return schoolPerson;
        }

        String ryxh = StudentFile.getInstence().GetData(StudentFile.ryxh);
        String rybm = StudentFile.getInstence().GetData(StudentFile.rybm);
        String ryxm = StudentFile.getInstence().GetData(StudentFile.ryxm);
        String rylx = StudentFile.getInstence().GetData(StudentFile.rylx);
        String kh = StudentFile.getInstence().GetData(StudentFile.kh);
        String zw = StudentFile.getInstence().GetData(StudentFile.zw);
        String mm = StudentFile.getInstence().GetData(StudentFile.mm);
        String zp = StudentFile.getInstence().GetData(StudentFile.zp);
        String mj = StudentFile.getInstence().GetData(StudentFile.mj);
        String zy = StudentFile.getInstence().GetData(StudentFile.zy);
        String sfzh = StudentFile.getInstence().GetData(StudentFile.sfzh);
        String bj = StudentFile.getInstence().GetData(StudentFile.bj);
        schoolPerson = new SchoolPerson(ryxh, rybm, ryxm, rylx, kh, zw, mm, zp, mj, sfzh, "student" , zy, bj,"","");
        student = schoolPerson;
        return schoolPerson;
    }

    /**
     * 根据卡号获取学生信息
     * @param data
     * @return
     */
    public SchoolPerson checkDataFromStudentCard(String data) {
        int ret = 0;
        SchoolPerson schoolPerson = null;

        ret = StudentFile.getInstence().FileIndexOperationFind(StudentFile.kh, data);
        if (ret <= 0) {
            return schoolPerson;
        }
        String ryxh = StudentFile.getInstence().GetData(StudentFile.ryxh);
        String rybm = StudentFile.getInstence().GetData(StudentFile.rybm);
        String ryxm = StudentFile.getInstence().GetData(StudentFile.ryxm);
        String rylx = StudentFile.getInstence().GetData(StudentFile.rylx);
        String kh = StudentFile.getInstence().GetData(StudentFile.kh);
        String zw = StudentFile.getInstence().GetData(StudentFile.zw);
        String mm = StudentFile.getInstence().GetData(StudentFile.mm);
        String zp = StudentFile.getInstence().GetData(StudentFile.zp);
        String mj = StudentFile.getInstence().GetData(StudentFile.mj);
        String zy = StudentFile.getInstence().GetData(StudentFile.zy);
        String sfzh = StudentFile.getInstence().GetData(StudentFile.sfzh);
        String bj = StudentFile.getInstence().GetData(StudentFile.bj);
        schoolPerson = new SchoolPerson(ryxh, rybm, ryxm, rylx, kh, zw, mm, zp, mj, sfzh, "student" , zy, bj,"","");
        return schoolPerson;
    }

}
