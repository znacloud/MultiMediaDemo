package com.github.znacloud.multimediademo.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Stephan on 2016/1/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;

    public CameraPreview(Context context) {
        super(context);
        inits(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        inits(context);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inits(context);
    }

    private void inits(Context pContext){
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCamera == null) return;
        Camera.Parameters parameters = mCamera.getParameters();
        //根据视图的尺寸设置预览图的尺寸
        parameters.setPreviewSize(width, height);
        mCamera.setParameters(parameters);
        requestLayout();

        // 必须调用startPreview里面更新视图
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            // 视图销毁时停止预览
            mCamera.stopPreview();
        }
    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) { return; }

        //设置新的相机实例之前确保旧的相机实例被正确的释放和关闭
        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            //TODO:根据设备支持的预览尺寸设置视图尺寸

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //每次更新了相机实例后都要调用此方法开启预览功能
            mCamera.startPreview();
        }
    }

    private void stopPreviewAndFreeCamera() {
        if (mCamera != null) {
            // 停止预览
            mCamera.stopPreview();

            // 释放设备，这样其他APP就可以继续使用相机设备
            mCamera.release();

            mCamera = null;
        }
    }
}
