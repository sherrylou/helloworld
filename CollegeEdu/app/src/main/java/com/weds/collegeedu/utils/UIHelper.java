package com.weds.collegeedu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.weds.collegeedu.App;
import com.weds.collegeedu.ui.MainActivity;
import com.weds.settings.dialog.InputPswDialog;
import com.weds.settings.dialog.SettingMotionDialog;
import com.weds.settings.ible.OnMulDrawFinishCallBack;
import com.weds.settings.ui.ColorTestActivity;
import com.weds.settings.ui.ScreenTestActivity;
import com.weds.settings.ui.SettingActivity;
import com.weds.settings.ui.SettingNetActivity;
import com.weds.tenedu.ui.AlbumStandbyActivity;
import com.weds.tenedu.ui.CarouselImgActivity;
import com.weds.tenedu.ui.CourseTableDetailsActivity;
import com.weds.tenedu.ui.ExamStandbyActivity;
import com.weds.tenedu.ui.StandByActivity;

/**
 * Created by lip on 2016/12/8.
 */

public class UIHelper {

    /**
     * 跳转到10寸主界面
     *
     * @param context
     */
    public static void to10Main(Context context) {
        App.setProjectType(1);
        Intent intent = getIntentInstence(context, com.weds.tenedu.ui.MainActivity.class);
//        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 跳转到21寸主界面
     *
     * @param context
     */
    public static void to21Main(Context context) {
        App.setProjectType(0);
        Intent intent = getIntentInstence(context, MainActivity.class);
//        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 跳转到考试待机界面
     *
     * @param context
     */
    public static void toExamStandbyActivity(Context context) {
        Intent intent = getIntentInstence(context, ExamStandbyActivity.class);
//        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 跳转到图片待机界面
     *
     * @param context
     */
    public static void toAlbumStandbyActivity(Context context) {
        Intent intent = getIntentInstence(context, AlbumStandbyActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到图片list界面
     *
     * @param context
     */
    public static void toImgListActivity(Context context) {
        Intent intent = getIntentInstence(context, CarouselImgActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到Setting
     *
     * @param context
     */
    public static InputPswDialog toSettingActivity(final Context context) {
        final SettingMotionDialog settingMotionDialog = SettingMotionDialog.getInstance();
        if (App.getProjectType()==1){
            Activity activity = (Activity) context;
            if (activity instanceof StandByActivity){
                //10待机界面守护线程状态改变,防止dialog弹出后也跳转界面
                ((StandByActivity) context).isShow = false;
            }
        }
        return settingMotionDialog.showInputPswDialog(context, new OnMulDrawFinishCallBack() {
            @Override
            public void onDrawFinish() {
                if (App.getProjectType()==1){
                    Activity activity = (Activity) context;
                    if (activity instanceof StandByActivity){
                        //10待机界面守护线程状态改变,防止dialog弹出后也跳转界面
                        ((StandByActivity) context).isShow = false;
                    }
                }
                settingMotionDialog.dismissInputPswDialog();
                Intent intent = getIntentInstence(context, SettingActivity.class);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 跳转到netSetting
     *
     * @param context
     */
    public static void toSettingNet(Context context, int type) {
        Intent intent = getIntentInstence(context, SettingNetActivity.class);
        intent.putExtra("netType", type);
        context.startActivity(intent);
    }

    /**
     * 跳转到课表待机界面
     *
     * @param context
     */
    public static void toCourseTableActivity(Context context) {
//        Intent intent = getIntentInstence(context, CourseTableDetailsActivity.class);
        Intent intent = new Intent(context,CourseTableDetailsActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到颜色测试界面
     *
     * @param context
     */
    public static void toColorTest(Context context) {
        Intent intent = getIntentInstence(context, ColorTestActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转屏幕测试界面
     *
     * @param context
     */
    public static void toScreenTest(Context context) {
        Intent intent = getIntentInstence(context, ScreenTestActivity.class);
        context.startActivity(intent);
    }

    /**
     * 禁止跳转动画
     *
     * @param context
     * @param cls
     * @return
     */
    private static Intent getIntentInstence(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

}
