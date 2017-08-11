package android.weds.lip_library.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.weds.lip_library.App;

/**
 * 界面帮助类
 *
 * @author lip
 */
public class UIHelper {

    /**
     * 全局web样式
     */
    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>" + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>" + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>" + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page.js\"></script>" + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>" + "<script type=\"text/javascript\">function showImagePreview(var url){window.location.url= url;}</script>" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common.css\">";
    public final static String WEB_STYLE = linkCss;

    public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

    private static final String SHOWIMAGE = "ima-api:action=showImage&data=";


    /**
     * 发送App异常崩溃报告
     */
    public static void sendAppCrashReport(final Context context) {

        DialogHelp.getConfirmDialog(context, "程序发生异常", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 退出
//                System.exit(-1);\
                Intent intent = new Intent("restartApp");
                App.getContext().sendBroadcast(intent);
            }
        }).show();
    }
}
