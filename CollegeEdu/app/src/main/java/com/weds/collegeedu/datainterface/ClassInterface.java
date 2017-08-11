package com.weds.collegeedu.datainterface;

import com.weds.collegeedu.datafile.ClassFile;
import com.weds.collegeedu.entity.Class;

/**
 * Created by lxy on 2017/3/7.
 */

public class ClassInterface {
    private Class aClass;
    private String className="";
    private static ClassInterface classInterface = null;

    public static ClassInterface getInstence() {
        if (classInterface == null) {
            classInterface = new ClassInterface();
        }
        return classInterface;
    }

    /**
     * 加载数据到class
     * @return
     */
    public Class checkDataFromClassno(String data) {
        int ret = 0;

        ret = ClassFile.getInstence().FileIndexOperationFind(ClassFile.jxbxh, data);
        if (ret <= 0) {
            return new Class();
        }

        String jxbxh = ClassFile.getInstence().GetData(ClassFile.jxbxh);
        String mc = ClassFile.getInstence().GetData(ClassFile.mc);
        String bz = ClassFile.getInstence().GetData(ClassFile.bj);

        aClass = new Class(jxbxh, mc, bz);
        return aClass;
    }
    /**
     * 设置班级名称
     */
    public int SetClassName(String name){
        className = name;
        return 1;
    }
    /**
     * 获取班级名称
     */
    public String GetClassName(){
        return className;
    }


}
