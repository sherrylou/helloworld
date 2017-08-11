package android.weds.lip_library;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.util.HashMap;
import java.util.Map;

import static com.nostra13.universalimageloader.core.assist.ImageScaleType.IN_SAMPLE_INT;

/**
 * App
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author YRain
 */
public class App extends Application {

    private static Context _context;
    private static Resources _resource;
    /**
     * 用来存储
     */
    protected static Map<String, Object> DATA = new HashMap<String, Object>();

    @Override
    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();
        _resource = _context.getResources();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
//        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler(this));
        initImageLoader();
//        dalvik.system.VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
    }

    /**
     * 为ImageLoader分配缓存空间
     *
     * @return
     */
    private void initImageLoader() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_picture_loadfailed)
                .showImageOnFail(R.drawable.ic_picture_loadfailed)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true).considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(IN_SAMPLE_INT).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(400, 400)
                // default = device screen dimensions
                .diskCacheExtraOptions(400, 400, null)
                .threadPoolSize(5)
                // default Thread.NORM_PRIORITY - 1
                .threadPriority(Thread.NORM_PRIORITY)
                // default FIFO
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                // default
                .diskCache(
                        new UnlimitedDiscCache(StorageUtils.getCacheDirectory(this, true)))
                // default
                .diskCacheSize(100 * 1024 * 1024).diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                // default
                .imageDownloader(new BaseImageDownloader(this))
                // default
                .imageDecoder(new BaseImageDecoder(false))
                // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                // default
                .defaultDisplayImageOptions(imageOptions).build();

        ImageLoader.getInstance().init(config);
    }

    public App getThis() {
        return this;
    }

    public static synchronized App getAppContext() {
        return (App) _context;
    }

    public static Context getContext() {
        return AppManager.getInstance().getCurrentActivity();
    }

    public static Resources getResource() {
        return _resource;
    }

}
