package com.weds.collegeedu.ible;

import com.weds.collegeedu.entity.SchoolPerson;

import java.util.List;

/**
 * Created by lip on 2016/12/5.
 *
 * 数据接口
 */

public interface GetDataCallBackInterface {

    /**
     * 课程tag
     */
    public static final String SUB_CALENDAR = "subCalendar";

    /**
     * 考试tag
     */
    public static final String TEXT_INFO = "textInfo";

    /**
     * 图片路径tag
     */
    public static final String REGULAR_PHOTO = "regularPhoto";

    /**
     * 相册图片路径tag
     */
    public static final String STAND_REGULAR_PHOTO = "standRegularPhoto";

    /**
     * 视频路径tag
     */
    public static final String REGULAR_VIDEO = "regularVideo";

    /**
     * 班级信息tag
     */
    public static final String CLASS_INFO = "classInfo";

    /**
     * 当天信息tag
     */
    public static final String DAY_INFO = "dayInfo";

    /**
     * 通知tag
     */
    public static final String NOTICE = "notice";

    /**
     * 出勤tag
     */
    public static final String ATTENDANCE = "attendance";

    /**
     * 课程表
     */
    public static final String TABLE_COURSE = "tableCourse";

    /**
     * 出勤详情tag
     */
    public static final String ATTENDANCE_DETAILS = "attendanceDetails";

    /**
     * 10寸主界面tag
     */
    public static final String TEN_MAIN_TIME = "tenMainTime";

    /**
     * 21寸主界面tag
     */
    public static final String TWENTY_ONE_MAIN = "tewnty_one_main";

    /**
     * 10寸主页待机界面课程状态变换到无课程显示课表tag
     */
    public static final String SUB_CHANGE_NOSUB = "sub_change_no_sub";

    /**
     * 10寸主页待机界面课程状态变换到有课程tag
     */
    public static final String SUB_CHANGE_INSUB = "sub_change_in_sub";

    /**
     * 主页待机显示图片
     */
    public static final String SHOW_IMG_FRAG = "showImgFrag";

    /**
     * 主页待机显示视频
     */
    public static final String SHOW_VIDEO_FRAG = "showVideoFrag";

    /**
     * 根据给的人员序号获取人员具体出勤数据tag
     */
    public static final String SCHOOL_PERSON_ATTENDANCE_INFO = "sub_change_in_sub";

    /**
     * 返回list数据
     * @param data 数据
     */
     void backListSuccess(List data);

    /**
     * 返回bean类数据
     * @param data 数据
     */
     void backObjectSuccess(Object data);

    /**
     * 档案更新通知弹出框
     */
    void LoadArchivesData();

    /**
     * 弹出刷卡界面
     * @param userInfo 人员信息
     * @param result 查找结果
     */
    void SwipeCardShow(SchoolPerson userInfo,int result);

    /**
     * 其他通知
     * @param type 类型
     */
    void otherNotice(String type);

}
