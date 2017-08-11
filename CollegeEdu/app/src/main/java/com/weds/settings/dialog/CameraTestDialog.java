package com.weds.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


import com.weds.collegeedu.R;
import com.weds.collegeedu.devices.CameraDevice;
import com.weds.collegeedu.resfile.ConstantConfig;

import java.io.File;

/**
 * 摄像头测试
 */
public class CameraTestDialog extends Dialog implements View.OnClickListener{
    ImageView btnCameraClose;
    ImageView ivCameraTest;
    private View customView;

    private Context context;

    private boolean mRunning = true;

    private Thread myThread;
    private String camerafileName = ConstantConfig.AppCameraFilePath + "tmp.jpg";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage( msg );
            switch (msg.what) {
                case 1:
                    ivCameraTest.setVisibility( View.VISIBLE );
                    Bitmap bitmap = BitmapFactory.decodeFile( new File( camerafileName ).getAbsolutePath() );
                    ivCameraTest.setImageBitmap( bitmap );
                    break;
                case 0:
                    ivCameraTest.setVisibility( View.INVISIBLE );
                    break;
            }
        }
    };


    public CameraTestDialog(Context context) {
        super( context, R.style.FullScreenDialog );
        this.context = context;
        customView = LayoutInflater.from( context ).inflate(
                R.layout.dialog_camera_text, null );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( customView );
        btnCameraClose = (ImageView)customView.findViewById( R.id.btn_camera_close );
        ivCameraTest = (ImageView)customView.findViewById( R.id.iv_camera_test );
        btnCameraClose.setOnClickListener( this );
        if (myThread == null) {
            myThread = new Thread( new Runnable() {
                @Override
                public void run() {
                    while (mRunning) {
                        try {
                            if (CameraDevice.getInstence().getImageFromCameraDevice( camerafileName ) != 0) {
                                handler.sendEmptyMessage( 1 );
                            } else {
                                handler.sendEmptyMessage( 0 );
                            }
                            Thread.sleep( 500 );
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }
                }
            } );
        }
        myThread.start();
    }


    @Override
    public void dismiss() {
        super.dismiss();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera_close:
                mRunning = false;
                this.dismiss();
                break;
            default:
                break;
        }
    }
}
