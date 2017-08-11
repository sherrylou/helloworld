package com.weds.settings.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.wheelview.widget.OnWheelChangedListener;
import android.weds.lip_library.wheelview.widget.OnWheelScrollListener;
import android.weds.lip_library.wheelview.widget.WheelView;
import android.weds.lip_library.wheelview.widget.adapters.AbstractWheelTextAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weds.collegeedu.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by lip on 2016/11/1.
 */

public class DateChooseWheelViewDialog extends Dialog implements View.OnClickListener {
    //控件
    private WheelView mYearWheelView;
    private WheelView mMonthWheelView;
    private WheelView mDayWheelView;
    private WheelView mHourWheelView;
    private WheelView mMinuteWheelView;
    private WheelView mSecondWheelView;
    private CalendarTextAdapter mMonthAdapter;
    private CalendarTextAdapter mDayAdapter;
    private CalendarTextAdapter mHourAdapter;
    private CalendarTextAdapter mMinuteAdapter;
    private CalendarTextAdapter mSecondAdapter;
//    private CalendarTextAdapter mSecondAdapter;
    private CalendarTextAdapter mYearAdapter;
    private TextView mTitleTextView;
    private Button mSureButton;
    private Dialog mDialog;
    private Button mCloseDialog;
    private LinearLayout mLongTermLayout;
    private TextView mLongTermTextView;

    //变量
    private ArrayList<String> arry_month = new ArrayList<String>();
    private ArrayList<String> arry_day = new ArrayList<String>();
    private ArrayList<String> arry_hour = new ArrayList<String>();
    private ArrayList<String> arry_minute = new ArrayList<String>();
    private ArrayList<String> arry_Second = new ArrayList<String>();
    private ArrayList<String> arry_year = new ArrayList<String>();

    private int nowMonthId = 0;
    private int nowDayId = 0;
    private int nowHourId = 0;
    private int nowMinuteId = 0;
    private int nowSecondId = 0;
    private int nowYearId = 0;
    private String mYearStr;
    private String mMonthStr;
    private String mDayStr;
    private String mHourStr;
    private String mMinuteStr;
    private String mSecondStr;
    private boolean mBlnBeLongTerm = false;//是否需要长期
    private boolean mBlnTimePickerGone = false;//时间选择是否显示
    private boolean mBlnDatePickerGone = false;//日期选择是否显示


    //常量
    private final int MAX_TEXT_SIZE = 18;
    private final int MIN_TEXT_SIZE = 14;

    private Context mContext;
    private DateChooseInterface dateChooseInterface;

    public DateChooseWheelViewDialog(Context context, DateChooseInterface dateChooseInterface) {
        super(context);
        this.mContext = context;
        this.dateChooseInterface = dateChooseInterface;
        mDialog = new Dialog(context, R.style.dialog);
        initView();
        initData();
    }


    private void initData() {
        initYear();
        initDate();
        initHour();
        initMinute();
        initSecond();
        initListener();
    }


    /**
     * 初始化滚动监听事件
     */
    private void initListener() {
        //年份*****************************
        mYearWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mYearAdapter);
                mYearStr = arry_year.get(wheel.getCurrentItem()) + "";
            }
        });

        mYearWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mYearAdapter);
            }
        });

        //月份********************
        mMonthWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mMonthAdapter);
//  mDateCalendarTextView.setText(" " + arry_date.get(wheel.getCurrentItem()));
                mMonthStr = arry_month.get(wheel.getCurrentItem());
            }
        });

        mMonthWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mMonthAdapter);
            }
        });

        //天数********************
        mDayWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mDayAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mDayAdapter);
//  mDateCalendarTextView.setText(" " + arry_date.get(wheel.getCurrentItem()));
                mDayStr = arry_day.get(wheel.getCurrentItem());
            }
        });

        mDayWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mDayAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mDayAdapter);
            }
        });

        //小时***********************************
        mHourWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mHourAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mHourAdapter);
                mHourStr = arry_hour.get(wheel.getCurrentItem()) + "";
            }
        });

        mHourWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mHourAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mHourAdapter);
            }
        });

        //分钟********************************************
        mMinuteWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mMinuteAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mMinuteAdapter);
                mMinuteStr = arry_minute.get(wheel.getCurrentItem()) + "";
            }
        });

        mMinuteWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mMinuteAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mMinuteAdapter);
            }
        });
        //秒数********************************************
        mSecondWheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mSecondAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mSecondAdapter);
                mSecondStr = arry_Second.get(wheel.getCurrentItem()) + "";
            }
        });

        mSecondWheelView.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mSecondAdapter.getItemText(wheel.getCurrentItem());
                setTextViewStyle(currentText, mSecondAdapter);
            }
        });
    }

    /**
     * 初始化分钟
     */
    private void initMinute() {
        Calendar nowCalendar = Calendar.getInstance();
        int nowMinite = nowCalendar.get(Calendar.MINUTE);
        arry_minute.clear();
        for (int i = 0; i <= 59; i++) {
            if (i < 10) {
                arry_minute.add("0" + i + "");
            } else {
                arry_minute.add(i + "");
            }
            if (nowMinite == i) {
                nowSecondId = arry_Second.size() - 1;
            }
        }

        mMinuteAdapter = new CalendarTextAdapter(mContext, arry_minute, nowMinuteId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mMinuteWheelView.setVisibleItems(5);
        mMinuteWheelView.setViewAdapter(mMinuteAdapter);
        mMinuteWheelView.setCurrentItem(nowMinite);
        mMinuteStr = arry_minute.get(nowMinuteId) + "";
        setTextViewStyle(mMinuteStr, mMinuteAdapter);

    }

    /**
     * 初始化秒数
     */
    private void initSecond() {
        Calendar nowCalendar = Calendar.getInstance();
        int nowSecond = nowCalendar.get(Calendar.SECOND);
        arry_Second.clear();
        for (int i = 0; i <= 59; i++) {
            if (i < 10) {
                arry_Second.add("0" + i + "");
            } else {
                arry_Second.add(i + "");
            }
            if (nowSecond == i) {
                nowSecondId = arry_Second.size() - 1;
            }
        }

        mSecondAdapter = new CalendarTextAdapter(mContext, arry_Second, nowSecondId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mSecondWheelView.setVisibleItems(5);
        mSecondWheelView.setViewAdapter(mSecondAdapter);
        mSecondWheelView.setCurrentItem(nowSecond);
        mSecondStr = arry_Second.get(nowSecondId) + "";
        setTextViewStyle(mSecondStr, mSecondAdapter);
    }

    /**
     * 初始化时间
     */
    private void initHour() {
        Calendar nowCalendar = Calendar.getInstance();
        int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        arry_hour.clear();
        for (int i = 0; i <= 23; i++) {
            if (i < 10) {
                arry_hour.add("0" + i + "");
            } else {
                arry_hour.add(i + "");
            }

            if (nowHour == i) {
                nowHourId = arry_hour.size() - 1;
            }
        }

        mHourAdapter = new CalendarTextAdapter(mContext, arry_hour, nowHourId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mHourWheelView.setVisibleItems(5);
        mHourWheelView.setViewAdapter(mHourAdapter);
        mHourWheelView.setCurrentItem(nowHour);
        mHourStr = arry_hour.get(nowHourId) + "";
        setTextViewStyle(mHourStr, mHourAdapter);
    }

    /**
     * 初始化年
     */
    private void initYear() {
        Calendar nowCalendar = Calendar.getInstance();
        int nowYear = nowCalendar.get(Calendar.YEAR);
        arry_year.clear();
        for (int i = 0; i <= 99; i++) {
            int year = nowYear - 30 + i;
            arry_year.add(year + "");
            if (nowYear == year) {
                nowYearId = arry_year.size() - 1;
            }
        }
        mYearAdapter = new CalendarTextAdapter(mContext, arry_year, nowYearId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mYearWheelView.setVisibleItems(5);
        mYearWheelView.setViewAdapter(mYearAdapter);
        mYearWheelView.setCurrentItem(nowYearId);
        mYearStr = arry_year.get(nowYearId);
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_date_choose, null);
        mDialog.setContentView(view);
        //设置dialog大小
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.width = getContext().getResources().getDimensionPixelOffset(R.dimen.setting_picker_width);
//            lp.height = getContext().getResources().getDimensionPixelOffset(R.dimen.setting_picker_height);
//            window.setAttributes(lp);
//        }
        mYearWheelView = (WheelView) view.findViewById(R.id.year_wv);
        mMonthWheelView = (WheelView) view.findViewById(R.id.month_wv);
        mDayWheelView = (WheelView) view.findViewById(R.id.day_wv);
        mHourWheelView = (WheelView) view.findViewById(R.id.hour_wv);
        mMinuteWheelView = (WheelView) view.findViewById(R.id.minute_wv);
        mSecondWheelView = ((WheelView) view.findViewById(R.id.second_wv));
        mTitleTextView = (TextView) view.findViewById(R.id.title_tv);
        mSureButton = (Button) view.findViewById(R.id.sure_btn);
        mCloseDialog = (Button) view.findViewById(R.id.date_choose_close_btn);
        mLongTermLayout = (LinearLayout) view.findViewById(R.id.long_term_layout);
        mLongTermTextView = (TextView) view.findViewById(R.id.long_term_tv);

        mSureButton.setOnClickListener(this);
        mCloseDialog.setOnClickListener(this);
        mLongTermTextView.setOnClickListener(this);
    }

    /**
     * 初始化日期
     */
    private void initDate() {
        Calendar nowCalendar = Calendar.getInstance();
        int nowYear = nowCalendar.get(Calendar.YEAR);
        arry_day.clear();
        arry_month.clear();
        setDate(nowYear);
        //月份
        mMonthAdapter = new CalendarTextAdapter(mContext, arry_month, nowMonthId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mMonthWheelView.setVisibleItems(5);
        mMonthWheelView.setViewAdapter(mMonthAdapter);
        mMonthWheelView.setCurrentItem(nowMonthId);
        mMonthStr = arry_month.get(nowMonthId);
        setTextViewStyle(mMonthStr, mMonthAdapter);
        //天数
        mDayAdapter = new CalendarTextAdapter(mContext, arry_day, nowDayId, MAX_TEXT_SIZE, MIN_TEXT_SIZE);
        mDayWheelView.setVisibleItems(5);
        mDayWheelView.setViewAdapter(mDayAdapter);
        mDayWheelView.setCurrentItem(nowDayId);
        mDayStr = arry_day.get(nowDayId);
        setTextViewStyle(mDayStr, mDayAdapter);
    }

    public void setDateDialogTitle(String title) {
        mTitleTextView.setText(title);
    }

    /**
     * 影隐藏时间选择
     *
     * @param isGone 是否隐藏
     */
    public void setTimePickerGone(boolean isGone) {
        mBlnTimePickerGone = isGone;
        if (isGone) {
            LinearLayout.LayoutParams yearParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            yearParams.rightMargin = 22;
            LinearLayout.LayoutParams monthParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            monthParams.rightMargin = 22;
            LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            mYearWheelView.setLayoutParams(yearParams);
            mMonthWheelView.setLayoutParams(monthParams);
            mDayWheelView.setLayoutParams(dayParams);

            mHourWheelView.setVisibility(View.GONE);
            mMinuteWheelView.setVisibility(View.GONE);
            mSecondWheelView.setVisibility(View.GONE);
        } else {
            mHourWheelView.setVisibility(View.VISIBLE);
            mMinuteWheelView.setVisibility(View.VISIBLE);
            mSecondWheelView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 影隐藏时间选择
     *
     * @param isGone 是否隐藏
     */
    public void setDatePickerGone(boolean isGone) {
        mBlnDatePickerGone = isGone;
        if (isGone) {
            LinearLayout.LayoutParams hourParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            hourParams.rightMargin = 22;
            LinearLayout.LayoutParams minParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            minParams.rightMargin = 22;
            LinearLayout.LayoutParams secParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            mHourWheelView.setLayoutParams(hourParams);
            mMinuteWheelView.setLayoutParams(minParams);
            mSecondWheelView.setLayoutParams(secParams);

            mYearWheelView.setVisibility(View.GONE);
            mMonthWheelView.setVisibility(View.GONE);
            mDayWheelView.setVisibility(View.GONE);
        } else {
            mYearWheelView.setVisibility(View.VISIBLE);
            mMonthWheelView.setVisibility(View.VISIBLE);
            mDayWheelView.setVisibility(View.VISIBLE);
        }

    }

    public void showLongTerm(boolean show) {
        if (show) {
            mLongTermLayout.setVisibility(View.VISIBLE);
        } else {
            mLongTermLayout.setVisibility(View.GONE);
        }

    }


    /**
     * 将改年的所有日期写入数组
     *
     * @param year
     */
    private void setDate(int year) {
        boolean isRun = isRunNian(year);
        Calendar nowCalendar = Calendar.getInstance();
        int nowMonth = nowCalendar.get(Calendar.MONTH) + 1;
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
        for (int month = 1; month <= 12; month++) {
            arry_month.add(month + "");
            if (month == nowMonth) {
                nowMonthId = arry_month.size() - 1;
            }
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    for (int day = 1; day <= 31; day++) {
                        arry_day.add(day + "");
                        if (day == nowDay) {
                            nowDayId = arry_day.size() - 1;
                        }
                    }
                    break;
                case 2:
                    if (isRun) {
                        for (int day = 1; day <= 29; day++) {
                            arry_day.add(day + "");
                            if (day == nowDay) {
                                nowDayId = arry_day.size() - 1;
                            }
                        }
                    } else {
                        for (int day = 1; day <= 28; day++) {
                            arry_day.add(day + "");
                            if (day == nowDay) {
                                nowDayId = arry_day.size() - 1;
                            }
                        }
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    for (int day = 1; day <= 30; day++) {
                        arry_day.add(day + "");
                        if (day == nowDay) {
                            nowDayId = arry_day.size() - 1;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 判断是否是闰年
     *
     * @param year
     * @return
     */
    private boolean isRunNian(int year) {
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置文字的大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextViewStyle(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(MAX_TEXT_SIZE);
                textvew.setTextColor(mContext.getResources().getColor(R.color.text_10));
            } else {
                textvew.setTextSize(MIN_TEXT_SIZE);
                textvew.setTextColor(mContext.getResources().getColor(R.color.text_11));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_btn://确定选择按钮监听
                if (mBlnTimePickerGone) {
                    dateChooseInterface.getDateTime(strTimeToDateFormat(1, mYearStr+"-", mMonthStr +"-"+ mDayStr), mBlnBeLongTerm);
                } else {
                    dateChooseInterface.getDateTime(strTimeToDateFormat(0, mHourStr+":", mMinuteStr+":"+mSecondStr), mBlnBeLongTerm);
                }
                dismissDialog();
                break;
            case R.id.date_choose_close_btn://关闭日期选择对话框
                dismissDialog();
                break;
            case R.id.long_term_tv://选择长期时间监听
                if (!mBlnBeLongTerm) {
                    mLongTermTextView.setBackgroundResource(R.drawable.gouxuanok);
                    mBlnBeLongTerm = true;
                } else {
                    mLongTermTextView.setBackgroundResource(R.drawable.gouxuanno);
                    mBlnBeLongTerm = false;
                }
            default:
                break;
        }
    }

    /**
     * 对话框消失
     */
    private void dismissDialog() {

        if (Looper.myLooper() != Looper.getMainLooper()) {

            return;
        }

        if (null == mDialog || !mDialog.isShowing() || null == mContext
                || ((Activity) mContext).isFinishing()) {

            return;
        }

        mDialog.dismiss();
        this.dismiss();
    }

    /**
     * 显示日期选择dialog
     */
    public void showDateChooseDialog() {

        if (Looper.myLooper() != Looper.getMainLooper()) {

            return;
        }

        if (null == mContext || ((Activity) mContext).isFinishing()) {

            // 界面已被销毁
            return;
        }

        if (null != mDialog) {

            mDialog.show();
            return;
        }

        if (null == mDialog) {

            return;
        }

        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }


//    private String strTimeToDateFormat(String yearStr, String dateStr, String hourStr, String minuteStr) {
//
//        return yearStr.replace("年", "-") + dateStr.replace("月", "-").replace("日", " ")
//                + hourStr + ":" + minuteStr;
//    }

    /**
     * xx年xx月xx日xx时xx分转成yyyy-MM-dd HH:mm
     *
     * @param type
     * @param firstStr
     * @param secStr
     * @return
     */
    private String strTimeToDateFormat(int type, String firstStr, String secStr) {
        String str = "";
        str = firstStr+secStr;
//        if (type == 1) {
//            str = firstStr.replace("年", "-") + secStr.replace("月", "-").replace("日", "");
//        } else {
//            str = firstStr.replace("时", ":") + secStr.replace("分",":").replace("秒","");
//        }
        return str;
    }

    /**
     * 滚轮的adapter
     */
    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, R.id.tempValue, currentItem, maxsize, minsize);
            this.list = list;
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            String str = list.get(index) + "";
            return str;
        }
    }

    /**
     * 回调选中的时间（默认时间格式"yyyy-MM-dd HH:mm:ss"）
     */
    public interface DateChooseInterface {
        void getDateTime(String time, boolean longTimeChecked);
    }

}
