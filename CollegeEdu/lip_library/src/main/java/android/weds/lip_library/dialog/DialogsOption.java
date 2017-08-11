package android.weds.lip_library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.weds.lip_library.R;

/**
 * Created by lip on 2016/9/18.
 *
 * 自定义dialogs的配置类
 */
public class DialogsOption implements View.OnClickListener{

    /**
     * 传入的dialogs的view对象
     */
    public View customView;

    public DialogsOption() {
    }

    /**
     * 获得dialog对象
     *  @param context 上下文
     * @param resId 布局文件
     * @param title      提示标题
     * @param promit     提示语
     * @param ok         确定
     * @param cancel     取消
     * @param okBack     确定点击事件回调
     * @param cancelBack 取消点击事件回调
     * */
    public Dialog myBuilder(Context context, int resId, String title, CharSequence promit, String ok, String cancel, ButtonDialog.Callback okBack, ButtonDialog.Callback cancelBack) {
        LayoutInflater inflater = LayoutInflater.from(context);
        customView = inflater.inflate(resId, null);
        Dialogs dialog = new Dialogs(context,customView , R.style.LoadProgressDialog);
        return dialog;
    }

    @Override
    public void onClick(View v) {

    }
}
