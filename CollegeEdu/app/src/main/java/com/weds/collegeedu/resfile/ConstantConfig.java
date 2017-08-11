package com.weds.collegeedu.resfile;

/**
 * Created by lip on 2016/11/26.
 *
 * 配置类
 */

public class ConstantConfig {

    public static final String PrivatePartition = "/mnt/private/";// 32M vfat   可以存放掉电文件
    public static final String AppPartition = "/mnt/app/";//    64M  ext4   可以存放不经常修改的应用数据 ,usb重新烧写后保留此区数据
    public static final String UserPartition = "/mnt/sdcard/";//  2.5G vfat   可以存放用户数据
    public static final String SdCardOnePartition = "/mnt/extsd0/";//   外置sd卡
    public static final String SdCardTwoPartition = "/mnt/extsd1/";//  外置sd卡
    public static final String UDiskPartition = "/mnt/usbhost1/";// 外置u盘

//    /**
//     * 应用根目录文件
//     */
//    private static final String ApplicationRootPath = FileUtils.getSDCardPath() + "/weds/kq42/";
//
//    public static final String MULTIMEDIA_DISK_PATH = ApplicationRootPath + "pictures/";
//
//    public static final String MULTIMEDIA_DISK_VIDEO_PATH = ApplicationRootPath + "vedio/";


    public static final String RepairFile = UDiskPartition + "update/repair.apk";
    public static final String AppPackageName = "com.weds.CollegeEdu";
    //在线升级包路径
    public static final String OnlineRepairFile = UserPartition + "weds/repair.zip";
    /**
     * 应用根目录文件
     */
    public static final String AppRootPath = UserPartition + "weds/kq42/";
    /**
     * 档案目录文件
     */
    public static final String AppArchivePath = AppRootPath + "note/archives/";

    /**
     * 档案照片路径
     */
    public static final String AppArchivePhotoPath = AppRootPath + "note/photo/";
    /**
     * 档案指纹路径
     */
    public static final String AppArchiveFingerPath = AppRootPath + "note/finger/";
    /**
     * 拍照照片路径
     */
    public static final String AppCameraFilePath = AppRootPath + "note/frame/";
    /**
     * 考勤记录路径
     */
    public static final String AppRecordFilePath = AppRootPath + "note/record/";

    /**
     * 宣传图片路径
     */
    public static final String AppPicturePath = AppRootPath + "pictures/";
    /**
     * 宣传视频路径
     */
    public static final String AppVideoPath = AppRootPath + "vedio/";

    /**
     * 应用掉电保护文件夹
     */
    public static final String ApplicationSafePath = PrivatePartition + "note/safe/";
    /**
     * 班级信息tag
     */
    public static final String CLASS_INFO_TAG = "classInfo";

    /**
     * 多媒体轮播图tag
     */
    public static final String CAROUSE_IMG = "carouseImg";

    /**
     * 当天信息tag
     */
    public static final String DAY_INFO = "dayInfo";

    /**
     * 当天信息tag
     */
    public static final String NOTICE_TAG = "noticeTag";

    /**
     * 课表tag
     */
    public static final String TABLE_LIST = "TableListTag";

    /**
     * 出勤tag
     */
    public static final String ATTENDANCE_TAG = "AttendanceTag";

    /**
     * 应用版本号
     */
    public static final String[] AppVersion={"D Android-3.10.00.00.20170330-BPG BP2201.20170324001", "D Android-3.10.00.00.20170330-BDG BD1011.20170324001"};
    /**
     * 终端机型号
     */
    public static final String[] DevicesModel = {"21 ", "10 "};

}
