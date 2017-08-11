package android.weds.lip_library.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.weds.lip_library.App;

/**
 * <pre>
 *
 * SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
 * editor.putString(&quot;name&quot;, &quot;user&quot;);
 * editor.putLong(&quot;id&quot;, 1l);
 * editor.putInt(&quot;age&quot;, 1);
 * editor.putFloat(&quot;money&quot;, 1f);
 * editor.putBoolean(&quot;is_female&quot;, true);
 * editor.putString(&quot;date&quot;, Dates.newDateStringOfFormatDateTime());
 * editor.commit();
 * </pre>
 *
 * @author lip
 */
public class Prefs {

    private static SharedPreferences PREF = getPreferences(App.getAppContext());
    private static SharedPreferences.Editor PREF_EDITOR = PREF.edit();

    public static void put(String key, String value) {
        PREF_EDITOR.putString(key, value);
        PREF_EDITOR.commit();
    }

    public static String get(String key) {
        return PREF.getString(key, "");
    }

//    public static void put(String key, Object obj) {
//        put(key, Gsons.toJSONString(obj));
//    }
//
//    public static <E> E get(String key, Class<E> clazz) {
//        return Gsons.toBean(get(key), clazz);
//    }

    public static void remove(String key) {
        PREF_EDITOR.remove(key).commit();
    }

    public static void clear() {
        PREF_EDITOR.clear().commit();
    }

    /**
     * 获取SharedPreferences
     * 接收一个 Context 参数，并自动使用当前应用程序的包名作为前缀来命名 SharedPreferences 文件
     */
    public static SharedPreferences getPreferences(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c);
    }

    /**
     * Context 获取 SharedPreferences
     * -------------------------------------------------------------------------------
     * name:用于指定SharedPreferences文件的名字,如果不存在则自动创建.所创建的 SharedPreferences 文件都是存放在/data/data/<packagename>/shared_prefs/目录下的。
     * mode:操作模式,主要有两种模式可以选择.
     * - Context.MODE_PRIVATE:用户只有当前的应用程序才可以对这个SharedPreferences文件进行读写
     * - Context.MODE_MULTI_PROCESS:用于会有多个进程中对同一个 SharedPreferences文件进行读写的情况
     */
    public static SharedPreferences getSharedPreferences(Context c, String name, int mode) {
        return c.getSharedPreferences(name, mode);
    }

    /**
     * Activity 获取 SharedPreferences
     * 只接收一个操作模式参数
     * -------------------------------------------------------------------------------
     * mode:操作模式,主要有两种模式可以选择.
     * - Context.MODE_PRIVATE:用户只有当前的应用程序才可以对这个SharedPreferences文件进行读写
     * - Context.MODE_MULTI_PROCESS:用于会有多个进程中对同一个 SharedPreferences文件进行读写的情况
     * -------------------------------------------------------------------------------
     * 实际调用:
     * <p/>
     * <p/>
     * <pre>
     * public SharedPreferences getPreferences(int mode) {
     *     return getSharedPreferences(getLocalClassName(), mode);
     * }
     * </pre>
     */
    public static SharedPreferences getPreferences(Activity c, int mode) {
        return c.getPreferences(mode);
    }
}
