package android.weds.lip_library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

/**
 * 自定义对话框
 */
public class ButtonDialog {

    private Dialog dialog;

    public interface Callback {
        public void onclick();
    }

    static ButtonDialog buttonDialog;

    public static ButtonDialog getInstance() {
        if (buttonDialog == null) {
            buttonDialog = new ButtonDialog();
        }
        return buttonDialog;
    }

    /**
     * 一个按钮的对话框
     *
     * @param context 上下文
     * @param title   提示标题
     * @param promp   提示语
     * @param ok      确定
     * @param okBack  确定点击事件回调
     * @param dialogsOption  dialogs的配置类
     * @param resId  自定义dialogs的布局资源
     */
    public void showOneButtonDialog(Context context, String title, CharSequence promp, String ok, final Callback okBack,DialogsOption dialogsOption,int resId) {// "关闭后不会记录学时，您确认关闭吗？"
        final Dialog dialog = dialogsOption.myBuilder(context,resId, title, promp, ok, null, okBack, null);
        // 点击屏幕外侧，dialog不消失
        dialog.setCanceledOnTouchOutside(false);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 两个按钮的对话框
     *
     * @param context    上下文
     * @param isKeyBack  物理返回键 true消失，false不消失
     * @param title      提示标题
     * @param promit     提示语
     * @param ok         确定
     * @param cancel     取消
     * @param okBack     确定点击事件回调
     * @param cancelBack 取消点击事件回调
     * @param dialogsOption  dialogs的配置类
     * @param resId  自定义dialogs的布局资源
     */
    public void showTwoButtonDialog(Context context, boolean isKeyBack, String title, CharSequence promit, String ok, String cancel, final Callback okBack, final Callback cancelBack,DialogsOption dialogsOption,int resId) {// "关闭后不会记录学时，您确认关闭吗？"
        dialog = dialogsOption.myBuilder(context,resId, title, promit, ok, cancel, okBack, okBack);
        // 点击屏幕外侧，dialog不消失
        dialog.setCanceledOnTouchOutside(false);
        dialogsOption.myBuilder(context,resId,title,promit,ok,cancel,okBack,cancelBack);
        if (!isKeyBack) {
            // 禁用返回键
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }

}
