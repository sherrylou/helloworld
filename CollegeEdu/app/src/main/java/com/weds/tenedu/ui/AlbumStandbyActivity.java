package com.weds.tenedu.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.AppManager;
import android.weds.lip_library.util.Strings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.weds.collegeedu.thread.MessageEvent;
import com.weds.collegeedu.utils.WedsDataUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlbumStandbyActivity extends StandByActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.carousel_view)
    ConvenientBanner carouselView;
    @Bind(R.id.activity_album_standby)
    RelativeLayout activityAlbumStandby;

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

    /**
     * 图片地址
     */
    private List<Mulitedia> imgPathList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_activity_album_standby);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.STAND_REGULAR_PHOTO, getDataCallBackInterface);
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
            if (Strings.isNotEmpty(regular.getPlaySpace()) && mulitedias.size() > 1) {
                carouselView.startTurning(Integer.valueOf(regular.getPlaySpace()) * 1000);
            } else if (mulitedias.size() == 1) {
                //只有一张时不轮播
                carouselView.stopTurning();
            } else {
                carouselView.startTurning(3000);
            }
        } else {
            carouselView.stopTurning();
            carouselView.setPages(cbViewHolderCreator, null);
        }
    }

    //轮播图Holder
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
            imageView = new ImageView(this.contexts.get());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Mulitedia data) {
            ImageLoader.getInstance().displayImage("file:///" + data.getName(), imageView);
        }
    }

    //==============数据回调================
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {
            if (data != null && handler != null) {
                imgPathList = data;
                handler.sendEmptyMessage(EventConfig.LIST_BACK);
            }
        }

        @Override
        public void backObjectSuccess(Object data) {
            if (data != null && handler != null) {
                mediaInfo = ((MediaInfo) data);
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

    private void initView() {
        ivBack.setImageResource(R.mipmap.return_push);
    }

    @OnClick({R.id.iv_back})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back:
                AppManager.getInstance().finishActivity(this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().postSticky(new MessageEvent("finishMyLoading"));
        handler.removeCallbacksAndMessages(null);
        handler = null;
        finish();
    }
}
