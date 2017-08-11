package com.weds.collegeedu.dialog;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.weds.lip_library.dialog.Dialogs;
import android.weds.lip_library.util.Dates;
import android.weds.lip_library.util.LogUtils;
import android.weds.lip_library.util.Strings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.collegeedu.App;
import com.weds.collegeedu.R;
import com.weds.collegeedu.datainterface.CalendarInterface;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.entity.SubCalendar;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.settings.entity.MenuVariablesInfo;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lip on 2016/10/13.
 * <p/>
 * 刷卡弹出dialog配置类
 */
class SlotCardDialogOption {

    /**
     * 传入的dialogs的view对象
     */

    private SchoolPerson schoolPerson;

    private int result;

    private View21Holder view21Holder;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://没有返回数据消失
                    if (!isReturn) {
                        if (App.getProjectType() == 0) {
                            //21寸
                            view21Holder.llWrongRoom.setVisibility(View.GONE);
                            view21Holder.tvSearchFailure.setVisibility(View.VISIBLE);
                        } else {
                            view10Holder.llRealTimeSearch.setVisibility(View.GONE);
                            view10Holder.llRealTimeResult.setVisibility(View.VISIBLE);
                        }
                        LogUtils.i("发送dialog消失", "-------0--------");
                        handler.sendEmptyMessageDelayed(1, Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysSuccessUIHoldTime)) * 1000);
                    }
                    break;
                case 1://返回数据展示两秒后消失
                    LogUtils.i("发送dialog消失", "--------1-------");
                    dismissdialog();
                    break;
                case 2://显示结果5秒自动结束
                    dismissdialog();
                    break;
            }
        }
    };
    private View10Holder view10Holder;

    private void dismissdialog() {
        //必须全部释放，否则半秒一刷，很快就会内存溢出
        LogUtils.i("发送dialog消失", "--------1-------" + isRegister);
        if (isRegister) {
            context.unregisterReceiver(realTimeScheduleQueryReceive);
        }
        if (App.getProjectType() == 0) {
            view21Holder.unregisterButter();
            view21Holder = null;
        } else {
            view10Holder.unregisterButter();
            view10Holder = null;
        }
        handler.removeCallbacksAndMessages(null);
        schoolPerson = null;
        if (dialogs != null) {
            dialogs.dismiss();
        }
        dialogs = null;
        context = null;
    }

    private Dialogs dialogs;
    private Context context;

    /**
     * 获得dialog对象
     *
     * @param context      上下文
     * @param schoolPerson 数据
     * @param result
     */
    public Dialog myBuilder(Context context, SchoolPerson schoolPerson, int result) {
        this.schoolPerson = schoolPerson;
        this.result = result;
        this.context = context;
        isRegister = false;
        registerBootReceiver();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (App.getProjectType() == 0) {
            //21寸
            View customView = inflater.inflate(R.layout.slot_card_dialog_layout, null);
            init21View(customView);
            dialogs = new Dialogs(context, context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_width), context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_height), customView, android.weds.lip_library.R.style.LoadProgressDialog);
            if (result != 0) {//不用查询时，直接5秒结束
                handler.sendEmptyMessageDelayed(2, Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysSuccessUIHoldTime)) * 1000);//5秒结束
            }
        } else {
            //10寸
            View customView = inflater.inflate(R.layout.slot_card_dialog_layout, null);
            init10View(customView);
            dialogs = new Dialogs(context, context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_width), context.getResources().getDimensionPixelOffset(R.dimen.slolt_dialog_height), customView, android.weds.lip_library.R.style.LoadProgressDialog);
            if (result != 0) {//不用查询时，直接5秒结束
                handler.sendEmptyMessageDelayed(2, Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysSuccessUIHoldTime)) * 1000);//5秒结束
            }
        }
        return dialogs;
    }

    //============21寸===========
    private void init21View(View customView) {

        view21Holder = new View21Holder(customView);

        if (schoolPerson != null) {
            if (result == 1 || result == 2) {
                //正常显示
                view21Holder.llRoot.setVisibility(View.VISIBLE);
                if (schoolPerson.getType().equals("4")) {
                    view21Holder.tvType.setText("学号");
                    view21Holder.llRoot.setBackgroundResource(R.mipmap.dialog_student_back);
                } else {
                    view21Holder.tvType.setText("工号");
                    view21Holder.llRoot.setBackgroundResource(R.mipmap.dialog_teacher_back);
                }
                view21Holder.tvNo.setText(schoolPerson.getNo());
                view21Holder.tvName.setText(schoolPerson.getName());

                //设置照片
                WedsDataUtils.setLocalImg(view21Holder.ivLocalImg, schoolPerson.getPersonNo(), schoolPerson.getType());
                if (Strings.isNotEmpty(schoolPerson.getImgPath())) {
                    Bitmap bitmap = BitmapFactory.decodeFile(schoolPerson.getImgPath());
//                    view21Holder.ivCameraImg.setImageURI(Uri.fromFile(new File()));
                    if(bitmap!=null) {
                        view21Holder.ivCameraImg.setImageBitmap(bitmap);
                    }else{
                        view21Holder.ivCameraImg.setImageURI(Uri.fromFile(new File(schoolPerson.getImgPath())));
                    }
                }
            } else if (result == 0) {
                //课表查询
                view21Holder.llWrongRoom.setVisibility(View.VISIBLE);
                if (schoolPerson.getType().equals("4")) {
                    view21Holder.tvWrongType.setText("亲爱的");
                } else {
                    view21Holder.tvWrongType.setText("尊敬的");
                }
                view21Holder.tvWrongName.setText(schoolPerson.getName());
                String sendResLine = "";
                //发送实时课表
                sendRealSearch();
            }
        } else {//查无此人
            view21Holder.llNullPerson.setVisibility(View.VISIBLE);
        }
    }

    public class View21Holder {

        @Bind(R.id.iv_local_img)
        ImageView ivLocalImg;
        @Bind(R.id.iv_camera_img)
        ImageView ivCameraImg;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_type)
        TextView tvType;
        @Bind(R.id.tv_no)
        TextView tvNo;
        @Bind(R.id.ll_root)
        LinearLayout llRoot;
        @Bind(R.id.ll_null_person)
        LinearLayout llNullPerson;
        @Bind(R.id.tv_wrong_type)
        TextView tvWrongType;
        @Bind(R.id.tv_wrong_name)
        TextView tvWrongName;
        @Bind(R.id.ll_wrong_room)
        LinearLayout llWrongRoom;
        @Bind(R.id.tv_return_state)
        TextView tvReturnState;
        @Bind(R.id.tv_thisOrNext)
        TextView tvThisOrNext;
        @Bind(R.id.tv_class_name)
        TextView tvClassName;
        @Bind(R.id.ll_class_info)
        LinearLayout llClassInfo;
        @Bind(R.id.tv_return_null)
        TextView tvReturnNull;
        @Bind(R.id.tv_null)
        TextView tvNull;
        @Bind(R.id.tv_search_failure)
        TextView tvSearchFailure;

        public View21Holder(View view) {
            ButterKnife.bind(this, view);
        }

        public void unregisterButter() {
            ButterKnife.unbind(this);
        }

    }

    //============10寸===========

    private void init10View(View customView) {
        view10Holder = new View10Holder(customView);
        if (schoolPerson != null) {
            if (result == 1 || result == 2) {
                //正常显示
                view10Holder.llPersonInfo.setVisibility(View.VISIBLE);
                if (schoolPerson.getType().equals("4")) {
                    view10Holder.tvNoWord.setText("学号");
                } else {
                    view10Holder.tvNoWord.setText("工号");
                }
                view10Holder.tvNo.setText(schoolPerson.getNo());
                view10Holder.tvName.setText(schoolPerson.getName());

                //设置照片
                WedsDataUtils.setLocalImg(view10Holder.ivHeadImg, schoolPerson.getPersonNo(), schoolPerson.getType());
                if (Strings.isNotEmpty(schoolPerson.getImgPath())) {
                    Bitmap bitmap = BitmapFactory.decodeFile(schoolPerson.getImgPath());
                    if (bitmap != null) {
                        view10Holder.ivCamera.setImageBitmap(bitmap);
                    }else {
                        view10Holder.ivCamera.setImageURI(Uri.fromFile(new File(schoolPerson.getImgPath())));
                    }
                }
            } else if (result == 0) {
                view10Holder.llRealTimeSearch.setVisibility(View.VISIBLE);
                //发送实时课表
                sendRealSearch();
            }
        } else {//查无此人
            view10Holder.llErrorHint.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 发送实时查询
     */
    private void sendRealSearch() {
        StringBuilder sb = new StringBuilder();
        sb.append(1 + ",");
        sb.append(schoolPerson.getPersonNo() + ",");
        sb.append(App.getLocalDate(Dates.FORMAT_DATETIME) + ",");
        sb.append(schoolPerson.getCardNo() + ",");
        String type = "0";
        if (schoolPerson.getType().equals("4")) {
            type = "0";
        } else {
            type = "1";
        }
        sb.append(type + ",");
        String subsuji = "0";
        SubCalendar currentCalendar = CalendarInterface.getInstence().getCurrentCalendar();
        if (currentCalendar != null && !currentCalendar.getState().equals("0")) {
            subsuji = CalendarInterface.getInstence().getCurrentCalendar().getSubsuji();
        } else {
            subsuji = "0";
        }
        sb.append(subsuji);
        //发送
        LogUtils.i("查询课表", sb.toString());
        WedsDataUtils.sendDosqlInfo((byte) 5, (byte) 14, (byte) 0, sb.toString());
        handler.sendEmptyMessageDelayed(0, 4000);//四秒没有返回数据dismiss
    }

    public class View10Holder {

        @Bind(R.id.iv_head_img)
        ImageView ivHeadImg;
        @Bind(R.id.iv_camera)
        ImageView ivCamera;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_no)
        TextView tvNo;
        @Bind(R.id.ll_person_info)
        LinearLayout llPersonInfo;
        @Bind(R.id.tv_enter)
        TextView tvEnter;
        @Bind(R.id.tv_cancel)
        TextView tvCancel;
        @Bind(R.id.ll_error_hint)
        LinearLayout llErrorHint;
        @Bind(R.id.tv_no_word)
        TextView tvNoWord;
        @Bind(R.id.ll_real_time_search)
        LinearLayout llRealTimeSearch;
        @Bind(R.id.tv_result)
        TextView tvResult;
        @Bind(R.id.ll_real_time_result)
        LinearLayout llRealTimeResult;

        public View10Holder(View view) {
//            inflater.inflate(R.layout.slot_card_dialog_layout, null);
            ButterKnife.bind(this, view);
        }

        public void unregisterButter() {
            ButterKnife.unbind(this);
        }

    }

    private boolean isReturn = false;

    private class RealTimeScheduleQueryReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String resline = intent.getStringExtra(EventConfig.REALTIME_SCHEDULE_QUERY);
                if (Strings.isNotEmpty(resline)) {
                    if (App.getProjectType() == 0) {
                        //21寸
                        LogUtils.i("收到的实时查询数据", "events收到" + resline);
                        String[] split = resline.split(",", -1);
                        view21Holder.tvReturnState.setVisibility(View.GONE);
                        view21Holder.tvNull.setVisibility(View.GONE);
                        if (schoolPerson.getType().equals("4")) {
                            view21Holder.tvWrongType.setText("亲爱的");
                        } else {
                            view21Holder.tvWrongName.setText("尊敬的");
                        }
                        view21Holder.tvWrongName.setText(schoolPerson.getName());
                        if (Strings.isNotEmpty(split[4])) {
                            //如果查询到教室结果
                            view21Holder.llClassInfo.setVisibility(View.VISIBLE);
                            String isExam = CalendarInterface.getInstence().getIsExam();
                            if (isExam.equals("0")) {
                                if (split[3].equals("1")) {
                                    view21Holder.tvThisOrNext.setText("这节课");
                                } else {
                                    view21Holder.tvThisOrNext.setText("下节课");
                                }
                            } else {
                                if (split[3].equals("1")) {
                                    view21Holder.tvThisOrNext.setText("该场次");
                                } else {
                                    view21Holder.tvThisOrNext.setText("下场次");
                                }
                            }
                            view21Holder.tvClassName.setText(split[4]);
                        } else {
                            view21Holder.tvReturnNull.setVisibility(View.VISIBLE);
                        }
                    } else {
                        view10Holder.llRealTimeSearch.setVisibility(View.GONE);
                        view10Holder.llRealTimeResult.setVisibility(View.VISIBLE);
                        //10寸
                        LogUtils.i("收到的实时查询数据", "events收到" + resline);
                        String[] split = resline.split(",", -1);
                        TextView tvResult = view10Holder.tvResult;
                        String isExam = CalendarInterface.getInstence().getIsExam();
                        if (Strings.isNotEmpty(split[4])) {
                            String result = "";
                            if (isExam.equals("0")) {
                                //如果查询到教室结果
                                if (split[3].equals("1")) {
                                    result += "这节课";
                                } else {
                                    result += "下节课";
                                }
                            } else {
                                //如果查询到教室结果
                                if (split[3].equals("1")) {
                                    result += "该场次";
                                } else {
                                    result += "下场次";
                                }
                            }
                            result += "您在" + split[4] + "教室";
                            tvResult.setText(result);
                        } else {
                            if (isExam.equals("0")) {
                                tvResult.setText("对不起，未查询到您的信息");
                            } else {
                                tvResult.setText("对不起，未查询到您的信息");
                            }
                        }
                    }
                    isReturn = true;
                    handler.sendEmptyMessageDelayed(1, Integer.parseInt(MenuVariablesInfo.getInstance().getSysVariable(MenuVariablesInfo.SysSuccessUIHoldTime)) * 1000);//两秒后消失
                }
            }catch (Exception e){

                }
        }
    }

    private RealTimeScheduleQueryReceive realTimeScheduleQueryReceive;
    private boolean isRegister = false;

    private void registerBootReceiver() {
        //动态注册广播监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(EventConfig.REALTIME_SCHEDULE_QUERY);
        realTimeScheduleQueryReceive = new RealTimeScheduleQueryReceive();
        if (!isRegister) {//防止重复注册
            context.registerReceiver(realTimeScheduleQueryReceive, filter);
            isRegister = true;
        }
    }
}
