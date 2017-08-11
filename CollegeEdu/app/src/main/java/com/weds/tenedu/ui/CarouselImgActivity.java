package com.weds.tenedu.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.weds.lip_library.AppManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.MediaInfo;
import com.weds.collegeedu.entity.Mulitedia;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.ui.BAwakeActivity;
import com.weds.collegeedu.utils.WedsDataUtils;
import com.weds.tenedu.adapter.BaseRecyclerAdapter;
import com.weds.tenedu.adapter.RecyclerViewHolder;
import com.weds.tenedu.adapter.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarouselImgActivity extends BAwakeActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.content_iv)
    ImageView contentIv;
    @Bind(R.id.album_list_rv)
    RecyclerView albumListRv;
    @Bind(R.id.activity_carousel_img)
    LinearLayout activityCarouselImg;
    private BaseRecyclerAdapter mAdapter;
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
                    if (mediaInfo != null && mediaInfo.getMulitedias()!=null && mediaInfo.getMulitedias().size() > 0) {
                        ImageLoader.getInstance().displayImage("file:///" + mediaInfo.getMulitedias().get(0).getName(), contentIv);
                        mAdapter.replaceList(mediaInfo.getMulitedias());
                    } else {
                        //放置默认图片
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_activity_carousel_img);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        tvTitle.setText("查看相册");
        setAlbumListRv();
    }

    /**
     * 设置相册列表
     */
    private void setAlbumListRv() {
        mAdapter = new BaseRecyclerAdapter<Mulitedia>(this, new ArrayList<Mulitedia>()) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.ten_item_album_list_rv;
            }

            @Override
            public void bindData(RecyclerView.ViewHolder holder, final int position, final Mulitedia item) {
                final RecyclerViewHolder mHolder = (RecyclerViewHolder) holder;
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(albumListRv.getMeasuredWidth() / 2 - 10, albumListRv.getMeasuredWidth() / 2 - 10);
                mHolder.getImageView(R.id.item_album_iv).setLayoutParams(params);
                ImageLoader.getInstance().displayImage("file:///" + item.getName(), mHolder.getImageView(R.id.item_album_iv));
                mHolder.getImageView(R.id.item_album_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageLoader.getInstance().displayImage("file:///" + item.getName(), contentIv);
                    }
                });
            }
        };
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        albumListRv.setLayoutManager(mLayoutManager);
        albumListRv.addItemDecoration(new SpaceItemDecoration(10));
        albumListRv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
            }
        });
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.REGULAR_PHOTO, getDataCallBackInterface);
    }

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

    @OnClick({R.id.iv_back})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_back:
                AppManager.getInstance().finishActivity(this);
                break;
        }
    }


    //    private void resetAlbumSelectedFlag(int position) {
//        for (int i = 0; i < imgPathList.size(); i++) {
//            if (position == i) {
//                imgPathList.get(i).setSelected(true);
//            } else {
//                imgPathList.get(i).setSelected(false);
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
        ButterKnife.unbind(this);
    }
}
