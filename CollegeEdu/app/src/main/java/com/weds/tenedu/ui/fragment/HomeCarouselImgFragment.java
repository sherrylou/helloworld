package com.weds.tenedu.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.Strings;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.MediaInfo;
import com.weds.collegeedu.entity.Mulitedia;
import com.weds.collegeedu.entity.Regular;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.UIHelper;
import com.weds.collegeedu.utils.WedsDataUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HomeCarouselImgFragment extends BaseFragment {
    @Bind(R.id.carousel_view)
    ConvenientBanner carouselView;
    private Context mContext;

    /**
     * 图片地址
     */
    private MediaInfo mediaInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.OBJ_BACK:
                    if (mediaInfo != null) {
                        setCarouselView();
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ten_fragment_home_album, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
//        setCarouselView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 设置轮播图效果
     */
    private void setCarouselView() {
        //设置加载显示的banner广告轮播图
        List<Mulitedia> mulitedias = mediaInfo.getMulitedias();
        Regular regular = mediaInfo.getRegular();
        if (mulitedias != null && mulitedias.size() > 0 && regular != null) {
            carouselView.setPages(cbViewHolderCreator, mulitedias).setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
            if (Strings.isNotEmpty(regular.getPlaySpace()) && mulitedias.size()>1) {
                carouselView.startTurning(Integer.valueOf(regular.getPlaySpace()) * 1000);
            } else if(mulitedias.size()==1){
                //只有一张时不轮播
                carouselView.stopTurning();
            }else {
                carouselView.startTurning(3000);
            }
        } else {
            carouselView.stopTurning();
            carouselView.setPages(cbViewHolderCreator,null);
        }
    }

    CBViewHolderCreator<LocalImageHolderView> cbViewHolderCreator = new CBViewHolderCreator<LocalImageHolderView>() {
        @Override
        public LocalImageHolderView createHolder() {
            return new LocalImageHolderView();
        }
    };

    public class LocalImageHolderView implements Holder<Mulitedia> {
        private ImageView imageView;
        private WeakReference<Context> contexts;
        @Override
        public View createView(final Context context) {
            this.contexts = (WeakReference<Context>) new WeakReference<>(context);
            imageView = new ImageView(this.contexts.get());//不用弱引用时间长了（比如坑爹的十几天）会内存泄露
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundColor(Color.GRAY);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelper.toImgListActivity(contexts.get());
                }
            });
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Mulitedia data) {
                imageView.setImageURI(Uri.fromFile(new File(data.getName())));
//            ImageLoader.getInstance().displayImage("file:///" + data.getName(), imageView);
        }
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.REGULAR_PHOTO, getDataCallBackInterface);
    }

    //==============数据回调================
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
            if (data != null) {
                mediaInfo = (MediaInfo) data;
                handler.sendEmptyMessage(EventConfig.OBJ_BACK);
            }
        }

        @Override
        public void LoadArchivesData() {

        }

        @Override
        public void SwipeCardShow(SchoolPerson userInfo, int result) {

        }

        @Override
        public void otherNotice(String type) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }
}
