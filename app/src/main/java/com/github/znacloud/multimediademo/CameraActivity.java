package com.github.znacloud.multimediademo;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.znacloud.multimediademo.view.CameraPreview;

/**
 * Created by Stephan on 2016/1/15.
 */
public class CameraActivity extends AppCompatActivity{
    private static final int K_STATE_FROZEN = 0;
    private static final int K_STATE_PREVIEW = 1;
    private static final int K_STATE_BUSY = 2;
    private CameraPreview mPreview;
    private Handler mHandler;
    private Camera mCamera;
    private Button mCaptureBtn;
    private int mPreviewState;
    private Camera.PictureCallback rawCallBack = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mPreviewState = K_STATE_FROZEN;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mPreview = (CameraPreview)findViewById(R.id.cp_preview);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCamera != null){
                    mCamera.autoFocus(null);
                }
            }
        });
        mHandler = new Handler();
        mCaptureBtn = (Button)findViewById(R.id.btn_shutter);
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPreviewState == K_STATE_FROZEN){
                    mCamera.startPreview();
                    mPreviewState = K_STATE_PREVIEW;
                }else if(mPreviewState == K_STATE_PREVIEW){
                    mCamera.takePicture(null,rawCallBack,null);
                    mPreviewState = K_STATE_BUSY;
                }
            }
        });

        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)){
            //开启自动聚焦模块
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(safeCameraOpen(0)){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mPreview != null){
                                mPreview.setCamera(mCamera);
                            }
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    protected void onPause() {
        releaseCameraAndPreview();
        super.onPause();
    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
            if(mCamera != null){
                mCamera.setDisplayOrientation(90);
            }
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        if(mPreview != null) {
            mPreview.setCamera(null);
        }
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
