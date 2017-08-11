package com.weds.collegeedu.ui.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.weds.lip_library.ui.BaseFragment;
import android.weds.lip_library.util.LogUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.weds.collegeedu.R;
import com.weds.collegeedu.bean.MediaInfo;
import com.weds.collegeedu.entity.Mulitedia;
import com.weds.collegeedu.entity.SchoolPerson;
import com.weds.collegeedu.ible.GetDataCallBackInterface;
import com.weds.collegeedu.resfile.EventConfig;
import com.weds.collegeedu.utils.UIHelper;
import com.weds.collegeedu.utils.WedsDataUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页视频fragment
 */
public class HomeVideoFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.vv_home_frag)
    VideoView vvHomeFrag;
    @Bind(R.id.rl_root)
    RelativeLayout rlRoot;
    @Bind(R.id.iv_video_error)
    ImageView ivVideoError;

    private String mParam1;
    private String mParam2;

    /**
     * 当前播放视频位置
     */
    private int position = 0;
    /**
     * 第一个播放的视频文件
     */
    private List<Mulitedia> videoPathList;
    private int currentPosition;

    private MediaInfo mediaInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventConfig.OBJ_BACK:
                    if (mediaInfo != null) {
                        initMediaView();
                    }
                    break;
            }
        }
    };

//    private SettingMotionDialog progressDialog;

    public HomeVideoFragment() {
    }

    /**
     * 或取实例
     */
    public static HomeVideoFragment newInstance() {
        HomeVideoFragment fragment = new HomeVideoFragment();
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
        LogUtils.i("视频fragment", "=======onCreateView=======");
        View view = inflater.inflate(R.layout.fragment_home_video, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        registerListener();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i("视频fragment", "=======onStart=======");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i("视频fragment", "=======onResume=======");
        if (vvHomeFrag != null && !vvHomeFrag.isPlaying() && videoPathList != null && videoPathList.size() > 0) {
            vvHomeFrag.setVideoPath(videoPathList.get(position).getName());
            vvHomeFrag.start();
            vvHomeFrag.seekTo(currentPosition);
            LogUtils.i("视频fragment", "=======" + currentPosition + "=======");
            currentPosition = -1;
        }
    }

    private void initData() {
        WedsDataUtils.getInstance().getDataFromCache(GetDataCallBackInterface.REGULAR_VIDEO, getDataCallBackInterface);
    }

    //==============数据回调==============
    GetDataCallBackInterface getDataCallBackInterface = new GetDataCallBackInterface() {
        @Override
        public void backListSuccess(List data) {

        }

        @Override
        public void backObjectSuccess(Object data) {
            if (data != null) {
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

    }

    /**
     * 初始化MediaPlay
     */
    private void initMediaView() {
        /**
         * 初始化
         */
        vvHomeFrag.requestFocus();
        videoPathList = mediaInfo.getMulitedias();
        if (mediaInfo != null && videoPathList.size() > 0) {
            vvHomeFrag.setVideoPath(videoPathList.get(0).getName());
            vvHomeFrag.start();
        }
    }

    private void registerListener() {
        vvHomeFrag.setOnPreparedListener(onPreparedListener);
        vvHomeFrag.setOnCompletionListener(onCompletionListener);
        vvHomeFrag.setOnErrorListener(onErrorListener);
        ivVideoError.setOnClickListener(this);
        ivVideoError.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //视频出错长按进入下一个视频,短按进入多媒体界面
                if (videoPathList != null && videoPathList.size() > 0) {
                    if (videoPathList.size() > ++position) {
                        vvHomeFrag.setVideoPath(videoPathList.get(position).getName());
                        vvHomeFrag.start();
                    } else {
                        vvHomeFrag.setVideoPath(videoPathList.get(0).getName());
                        vvHomeFrag.start();
                        position = 0;
                    }
                }
                return true;
            }
        });
        rlRoot.setOnClickListener(this);
    }

    //===================mediaPlayer准备好后的监听======================
    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            LogUtils.i("首页视频", "=========准备完毕========");
            rlRoot.setVisibility(View.VISIBLE);
            ivVideoError.setVisibility(View.GONE);
        }
    };

    //===================视频播放完后的监听========================
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (videoPathList != null && videoPathList.size() > 0) {
                if (videoPathList.size() > ++position) {
                    vvHomeFrag.setVideoPath(videoPathList.get(position).getName());
                    vvHomeFrag.start();
                } else {
                    vvHomeFrag.setVideoPath(videoPathList.get(0).getName());
                    vvHomeFrag.start();
                    position = 0;
                }
            }
        }
    };

    //====================视频播放出错监听======================
    MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            LogUtils.i("视频出错", "==================");
            Toast.makeText(context, "视频出错,长按切换视频", Toast.LENGTH_SHORT).show();
            rlRoot.setVisibility(View.GONE);
            ivVideoError.setVisibility(View.VISIBLE);
            return true;
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_root:
                UIHelper.toImgListActivity(context);
                break;
            case R.id.iv_video_error:
                UIHelper.toImgListActivity(context);
                break;
        }
    }

    /**
     * 重新开始video
     */
    private void restartVideo() {

        if (vvHomeFrag != null && !vvHomeFrag.isPlaying() && videoPathList != null && videoPathList.size() > 0) {
            vvHomeFrag.setVideoPath(videoPathList.get(position).getName());
            vvHomeFrag.start();
            vvHomeFrag.seekTo(currentPosition);
            LogUtils.i("视频fragment", "=======" + currentPosition + "=======");
            currentPosition = -1;
        }
//        vvHomeFrag.resume();
//        if (vvHomeFrag != null && !vvHomeFrag.isPlaying() && videoPathList != null && videoPathList.size() > 0) {
//            vvHomeFrag.setVideoPath(videoPathList.get(position).getName());
//            vvHomeFrag.start();
//            vvHomeFrag.seekTo(currentPosition);
//            LogUtils.i("视频fragment", "=======" + currentPosition + "=======");
//            currentPosition = -1;
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i("视频fragment", "------Pause------");
        if (vvHomeFrag.isPlaying()) {
            currentPosition = vvHomeFrag.getCurrentPosition();
            LogUtils.i("视频fragment", "------" + currentPosition + "------");
//            vvHomeFrag.stopPlayback();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.i("视频fragment", "------onStop------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        LogUtils.i("视频fragment", "------onDestroyView------");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }
}
