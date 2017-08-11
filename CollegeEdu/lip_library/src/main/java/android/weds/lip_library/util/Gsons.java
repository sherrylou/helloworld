package android.weds.lip_library.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

/**
 * Gson 工具类
 * 基本类型:Serialization
 * <p/>
 * <pre>
 * Gson gson = new Gson();
 * gson.toJson(1);            ==> prints 1
 * gson.toJson("abcd");       ==> prints "abcd"
 * gson.toJson(new Long(10)); ==> prints 10
 * int[] values = { 1 };
 * gson.toJson(values);       ==> prints [1]
 * </pre>
 * <p/>
 * 基本类型:Deserialization
 * <p/>
 * <pre>
 * int one = gson.fromJson("1", int.class);
 * Integer one = gson.fromJson("1", Integer.class);
 * Long one = gson.fromJson("1", Long.class);
 * Boolean false = gson.fromJson("false", Boolean.class);
 * String str = gson.fromJson("\"abc\"", String.class);
 * String anotherStr = gson.fromJson("[\"abc\"]", String.class);
 * </pre>
 * <p/>
 * 对象类型
 * <p/>
 * <pre>
 * new Gson().toJson(new User());
 * new Gson().fromJson(jsonString, User.class);
 * new Gson().fromJson(jsonString, new TypeToken&lt;List&lt;User&gt;&gt;() {}.getType());
 * </pre>
 *
 * @author YRain
 */
//TODO gson线程安全的单例
public class Gsons {

    private static final boolean PRETTY = false;

    private static Gson newGson() {
        return newGson(Dates.DEFAULT_FORMAT, PRETTY);
    }

    private static Gson newGson(final String dateformat) {
        return newGson(dateformat, PRETTY);
    }

    private static Gson newGson(boolean pretty) {
        return newGson(Dates.DEFAULT_FORMAT, pretty);
    }

    private static Gson newGson(final String dateformat, boolean pretty) {
        GsonBuilder gb = new GsonBuilder();
        //set dateformat
        gb.registerTypeHierarchyAdapter(Date.class, new JsonSerializer<Date>() {
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                SimpleDateFormat format = new SimpleDateFormat(dateformat);
                return new JsonPrimitive(format.format(src));
            }
        }).setDateFormat(dateformat);
        //set pretty
        if (pretty) {
            gb.setPrettyPrinting();
        }
        return gb.create();
    }

    public static String toJSONString(Object obj) {
        return newGson().toJson(obj);
    }

    public static String toJSONString(Object obj, boolean pretty) {
        return newGson(pretty).toJson(obj);
    }

    public static String toJSONStringWithPretty(Object obj) {
        return newGson(true).toJson(obj);
    }

    /**
     * 通过数组来转换
     */
    public static <T> List<T> _toObjects_with_array(String s, Class<T[]> clazz) {
        return Arrays.asList(new Gson().fromJson(s, clazz));
    }

    public static List<String> toListString(String jsonString) {
        return newGson().fromJson(jsonString, new TypeToken<List<String>>() {
        }.getType());
    }

    public static <T> T toBean(String jsonString, Class<T> clazz) {
        return newGson().fromJson(jsonString, clazz);
    }

    public static <T> T toBean(String jsonString, Type typeOfT) {
        return newGson().fromJson(jsonString, typeOfT);
    }

    public static <T> T toBean(String jsonString, TypeToken<T> typetokenOfT) {
        return newGson().fromJson(jsonString, typetokenOfT.getType());
    }

    public static <T> T toFBean(String filePath, Class<T> clazz) {
        return newGson().fromJson(filePath, clazz);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String jsonString) {
        return toBean(jsonString, Map.class);
    }

    public static Result toResult(String jsonString) {
        return toBean(jsonString, Result.class);
    }

    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        return newGson().fromJson(jsonString, new ListParameterizedType(clazz));
    }


    /**
     * 将对象转换成json格式(并自定义日期格式)
     */
    public static String toJSONStringWithDateFormat(Object ts, final String dateformat) {
        return newGson(dateformat).toJson(ts);
    }

    static class ListParameterizedType<T> implements ParameterizedType {

        private Class<T> wrapped;

        public ListParameterizedType(Class<T> wrapped) {
            this.wrapped = wrapped;
        }

        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    }
}