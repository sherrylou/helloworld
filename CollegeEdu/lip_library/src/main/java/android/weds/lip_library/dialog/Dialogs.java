package android.weds.lip_library.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.weds.lip_library.R;

public class Dialogs extends Dialog {
    private static int default_width = 160; // 默认宽度
    private static int default_height = 120;// 默认高度

    public Dialogs(Context context, View layout, int style) {
        this(context, default_width, default_height, layout, style);
    }

    public Dialogs(Context context, int width, int height, View layout, int style) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = width;
        params.height = height;
        window.setAttributes(params);
    }
}
