package com.weds.settings.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.weds.lip_library.util.LogUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.weds.collegeedu.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lip on 2016/12/16.
 * <p>
 * 数字键盘view
 */

public class NumBerKeyBoardView extends FrameLayout {


    @Bind(R.id.et_show_number)
    EditText tvShowNumber;
    @Bind(R.id.number_1)
    TextView number1;
    @Bind(R.id.number_2)
    TextView number2;
    @Bind(R.id.number_3)
    TextView number3;
    @Bind(R.id.number_4)
    TextView number4;
    @Bind(R.id.number_5)
    TextView number5;
    @Bind(R.id.number_6)
    TextView number6;
    @Bind(R.id.number_7)
    TextView number7;
    @Bind(R.id.number_8)
    TextView number8;
    @Bind(R.id.number_9)
    TextView number9;
    @Bind(R.id.symbol_heng)
    TextView symbolHeng;
    @Bind(R.id.symbol_dian)
    TextView symbolDian;
    @Bind(R.id.symbol_empty)
    TextView symbolEmpty;
    @Bind(R.id.number_clear_last)
    TextView numberClearLast;
    @Bind(R.id.number_0)
    TextView number0;
    @Bind(R.id.number_enter)
    TextView numberEnter;

    public List<View> getTextViewList() {
        return textViewList;
    }

    private List<View> textViewList = new ArrayList<>();

    public NumBerKeyBoardView(Context context) {
        super(context);
    }

    public NumBerKeyBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumBerKeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.setting_number_keyboard_layout, this);
        ButterKnife.bind(this);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NumBerKeyBoardView, defStyleAttr, 0);
        int anInt = a.getInt(R.styleable.NumBerKeyBoardView_inputType, 1);
        if (anInt == 0) {
            tvShowNumber.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        a.recycle();
        textViewList.add(number1);
        textViewList.add(number2);
        textViewList.add(number3);
        textViewList.add(number4);
        textViewList.add(number5);
        textViewList.add(number6);
        textViewList.add(number7);
        textViewList.add(number8);
        textViewList.add(number9);
        textViewList.add(symbolHeng);
        textViewList.add(symbolDian);
        textViewList.add(symbolEmpty);
        textViewList.add(numberClearLast);
        textViewList.add(number0);
        textViewList.add(numberEnter);
        //EditText键盘弹出取消
        tvShowNumber.setFocusable(false);
    }

    @OnClick({R.id.number_0, R.id.number_1, R.id.number_2, R.id.number_3, R.id.number_4, R.id.number_5, R.id.number_6, R.id.number_7, R.id.number_8, R.id.number_9, R.id.symbol_heng, R.id.symbol_dian, R.id.symbol_empty, R.id.number_clear_last, R.id.number_enter})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.number_0:
                setTextOnShowView(number0);
                break;
            case R.id.number_1:
                setTextOnShowView(number1);
                break;
            case R.id.number_2:
                setTextOnShowView(number2);
                break;
            case R.id.number_3:
                setTextOnShowView(number3);
                break;
            case R.id.number_4:
                setTextOnShowView(number4);
                break;
            case R.id.number_5:
                setTextOnShowView(number5);
                break;
            case R.id.number_6:
                setTextOnShowView(number6);
                break;
            case R.id.number_7:
                setTextOnShowView(number7);
                break;
            case R.id.number_8:
                setTextOnShowView(number8);
                break;
            case R.id.number_9:
                setTextOnShowView(number9);
                break;
            case R.id.symbol_dian:
                setTextOnShowView(symbolDian);
                break;
            case R.id.symbol_empty:
                setTextOnShowView(symbolEmpty);
                break;
            case R.id.symbol_heng:
                setTextOnShowView(symbolHeng);
                break;
            case R.id.number_clear_last:
                deleteChar();
                break;
            case R.id.number_enter:
                onKeyInputFinishListener.keyInputFinish(tvShowNumber.getText().toString());
                break;
        }
    }

    public interface OnKeyInputFinishListener {
        void keyInputFinish(String text);
    }

    OnKeyInputFinishListener onKeyInputFinishListener;

    /**
     * 设置输入完成监听
     *
     * @param onKeyInputFinishListener
     */
    public void setOnKeyInputFinishListener(OnKeyInputFinishListener onKeyInputFinishListener) {
        this.onKeyInputFinishListener = onKeyInputFinishListener;
    }

    /**
     * 删除一个字符
     */
    private void deleteChar() {
        String text = tvShowNumber.getText().toString();
        if (text.length() == 0) {
            return;
        }
        char[] chars = text.toCharArray();
        tvShowNumber.setText(chars, 0, chars.length - 1);
    }

    /**
     * 设置文本到显示区
     *
     * @param clickText
     */
    private void setTextOnShowView(TextView clickText) {
        tvShowNumber.append(clickText.getText());
    }
}
