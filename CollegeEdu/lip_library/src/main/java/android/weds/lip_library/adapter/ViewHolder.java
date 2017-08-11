package android.weds.lip_library.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.R;
import android.weds.lip_library.util.ImageLoaderUtils;
import android.weds.lip_library.util.StringUtils;
import android.weds.lip_library.util.TypefaceUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * 通用性极高的ViewHolder
 * Created by 火蚁 on 15/4/8.
 */
public class ViewHolder {
    // 用于存储listView item的容器
    private SparseArray<View> mViews;

    // item根view
    private View mConvertView;

    protected Context mContext;

    private static int position;

    public ViewHolder(Context context, ViewGroup parent, int layoutId) {
        this.mViews = new SparseArray();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
        this.mContext = context;
    }

    /**
     * 获取一个viewHolder
     *
     * @param context     context
     * @param convertView view
     * @param parent      parent view
     * @param layoutId    布局资源id
     * @param position    索引
     * @return
     */
    public static ViewHolder getViewHolder(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        ViewHolder.position = position;
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId);
        }

        return (ViewHolder) convertView.getTag();
    }

    public int getPosition() {
        return this.position;
    }

    // 通过一个viewId来获取一个view
    public <T extends View> T getView(int viewId) {

        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    // 返回viewHolder的容器类
    public View getConvertView() {
        return this.mConvertView;
    }

    // 给TextView设置文字
    public void setText(int viewId, String text) {
        if (StringUtils.isEmpty(text))
            return;
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    // 给TextView设置文字
    public void setText(int viewId, SpannableString text) {
        if (text == null)
            return;
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    // 给TextView设置文字
    public void setText(int viewId, int textRes) {
        TextView tv = getView(viewId);
        tv.setText(textRes);
    }

    public void setText(int viewId, String text, int emptyRes) {
        TextView tv = getView(viewId);
        if (StringUtils.isEmpty(text)) {
            tv.setText(emptyRes);
        } else {
            tv.setText(text);
        }
    }

    public void setText(int viewId, String text, String emptyText) {
        TextView tv = getView(viewId);
        if (StringUtils.isEmpty(text)) {
            tv.setText(emptyText);
        } else {
            tv.setText(text);
        }
    }


    /**
     * @param viewId      id
     * @param text        内容
     * @param semanticRes 资源
     */
    public void setTextWithSemantic(int viewId, String text, int semanticRes) {
        TextView tv = getView(viewId);
        TypefaceUtils.setSemantic(tv, text, semanticRes);
    }

    public void setTextWithOcticon(int viewId, String text, int iconRes) {
        TextView tv = getView(viewId);
        TypefaceUtils.setOcticons(tv, text, iconRes);
    }

    // 给ImageView设置图片资源
    public void setImageResource(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
    }

    private AnimateFirstDisplayListener animateFirstDisplayListener;

    /**
     * 给iv设置本地图片
     *
     * @param viewId
     * @param uri
     */
    public void setImageForFile(int viewId, String uri) {
        ImageView iv = getView(viewId);
        Log.i("图片tag", iv.getTag() + "");
        if (iv.getTag() == null || !iv.getTag().equals(uri)) {
            iv.setImageResource(R.drawable.mini_avatar);
        }
        ImageLoader.getInstance().displayImage("file:///"+uri, iv);
        iv.setTag(uri);
    }

    /**
     * 给iv设置本地bitmap
     *
     * @param viewId
     * @param uri
     */
    public void setImageBitMap(int viewId, String uri) {
        ImageView iv = getView(viewId);
        if (iv.getTag() == null || !iv.getTag().equals(uri)) {
            iv.setImageResource(R.drawable.mini_avatar);
        }
        setLocalImg(uri,iv);
        ImageLoader.getInstance().displayImage(uri, iv, ImageLoaderUtils.getOption());
        iv.setTag(uri);
    }

    /**
     * 设置图片
     * @param uri
     */
    private void setLocalImg(String uri,ImageView iv) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;//图片宽高都为原来的二分之一，即图片为原来的四分之一
        Log.i("图片轮播图片path", uri);
        Bitmap relBit = BitmapFactory.decodeFile(uri, options);
        int outWidth = options.outWidth;
        relBit.recycle();//回收内存
        BitmapFactory.Options ops = sampleSizeImg(outWidth);
        Bitmap bitmap = BitmapFactory.decodeFile(uri, ops);
        iv.setImageBitmap(bitmap);
        relBit.recycle();//回收内存
    }

    /**
     * 压缩图片
     * @param outWidth 图片宽度
     * @return
     */
    public BitmapFactory.Options sampleSizeImg(int outWidth) {
        BitmapFactory.Options ops = new BitmapFactory.Options();
        if (outWidth > 3841) {
            ops.inSampleSize = 8;
        } else if (outWidth > 2560) {
            ops.inSampleSize = 6;
        } else if (outWidth > 1080) {
            ops.inSampleSize = 4;
        } else if (outWidth > 720) {
            ops.inSampleSize = 2;
        } else {
            ops.inSampleSize = 1;
        }
        return ops;
    }

    /**
     * 图片加载监听事件
     **/
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
                    displayedImages.add(imageUri); // 将图片uri添加到集合中
                }
            }
        }
    }
}
