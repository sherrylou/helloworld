package com.weds.collegeedu.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.LogUtils;
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
import com.weds.collegeedu.utils.WedsDataUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.weds.collegeedu.ible.GetDataCallBackInterface.REGULAR_PHOTO;

/**
 * 轮播图fragment
 */
public class HomeCarouselImgFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.cb_img)
    ConvenientBanner cbImg;
    @Bind(R.id.iv_default_img)
    ImageView ivDefaultImg;

    private String mParam1;
    private String mParam2;

    private MediaInfo mediaInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.OBJ_BACK:
                    if (mediaInfo != null) {
                        setData2Cb(mediaInfo);
                    }
                    break;
            }
            ;
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeCarouselImgFragment.
     */
    public static HomeCarouselImgFragment newInstance() {
        HomeCarouselImgFragment fragment = new HomeCarouselImgFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_carousel_img, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(REGULAR_PHOTO, getDataCallBack);
    }

    private void initView() {

    }

    //================数据回调===================
    GetDataCallBackInterface getDataCallBack = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(final List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
            if (data != null) {
                mediaInfo = (MediaInfo) data;
                handler.sendEmptyMessageDelayed(EventConfig.OBJ_BACK, 100);
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

    /**
     * 设置轮播图数据到cb上
     *
     * @param mediaInfo
     */
    private void setData2Cb(MediaInfo mediaInfo) {
        List<Mulitedia> mulitedias = mediaInfo.getMulitedias();
        Regular regular = mediaInfo.getRegular();
        if (mulitedias != null && mulitedias.size() > 0 && regular != null) {
            cbImg.setVisibility(View.VISIBLE);
            ivDefaultImg.setVisibility(View.GONE);
            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().clearMemoryCache();
            cbImg.setPages(new CBViewHolderCreator<LocalImageHolderView>() {
                @Override
                public LocalImageHolderView createHolder() {
                    return new LocalImageHolderView();
                }
            }, mulitedias).setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
            cbImg.setScrollDuration(1000);
            cbImg.setCanLoop(true);
            if (Strings.isNotEmpty(regular.getPlaySpace()) && mulitedias.size() > 1) {
                cbImg.startTurning(Integer.valueOf(regular.getPlaySpace()) * 1000);
            } else if (mulitedias.size() == 1) {
                //只有一张时不轮播
                cbImg.stopTurning();
            } else {
                cbImg.startTurning(3000);
            }
            if (mulitedias.size() == 1) {
                //如果只有一张，b
                cbImg.setCanLoop(false);
            }
        } else {
            cbImg.setVisibility(View.GONE);
            ivDefaultImg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 轮播图片holder
     */
    public class LocalImageHolderView implements Holder<Mulitedia> {
        private ImageView imageView;
        private WeakReference<Context> context;

        @Override
        public View createView(final Context context) {
            this.context = (WeakReference<Context>) new WeakReference<>(context);
            imageView = new ImageView(this.context.get());//不用弱引用时间长了（比如坑爹的十几天）会内存泄露
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);
            LogUtils.i("图片轮播图片path", imageView + "---");
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Mulitedia mulitedia) {
            ImageLoader.getInstance().displayImage("file:///" + mulitedia.getName(), imageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }
}
