package com.open.face.camera;

import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;


import androidx.annotation.NonNull;

import com.jowney.common.util.logger.L;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jowney on 2018/4/27.
 */

public abstract class CameraBase implements Camera.PreviewCallback {
    private final static String TAG = "CameraBase";

    private static final int TEXTURE_NAME = 10;
    private final static int SUCCESS = 1;
    private final static int FAILURE = 0;
    private int mPreviewWidth = 640; // default 1440
    private int mPreviewHeight = 480; // default 1080
    private int mCanvasWidth = 0;
    private int mCanvasHeight = 0;

    private float mPreviewScale = mPreviewHeight * 1f / mPreviewWidth;

    private Camera mCamera;
    private Camera.Parameters mParameters;
    private SurfaceTexture mSurfaceTexture;
    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private int mCameraId = -1;
    private CameraListener mCameraListener;
    public byte[] mPreviewBuffer;
    /**
     * 预览时 Camera采集数据直接显示在屏幕上角度不一定对，所以需要调用
     * mCamera.setDisplayOrientation(mCameraClockwiseRotationValue);
     * Camera数据顺时针旋转该值后显示在屏幕上
     */
    private int mCameraClockwiseRotationValue;
    /**
     * 该值代表使用者期望Camera数据预览的方向
     * 该值经过计算后才能得到mCameraClockwiseRotationValue
     */
    private int mDisplayOrientation;

    /**
     * 初始化Camera需要的数据
     * @param cameraID
     * @param previewWidth NV21数据的宽
     * @param previewHeight NV21数据的高
     * @param canvasWidth 预览View的宽
     * @param canvasHeight 预览View的高
     * @param displayOrientation 画面显示的方向，以竖屏手机为例，0代表期望竖直显示画面，1代表逆时针旋转90度，以此类推
     * @param surfaceTexture
     * @param cameraListener
     */
    public void initCamera(
              int cameraID
            , int previewWidth
            , int previewHeight
            , int canvasWidth
            , int canvasHeight
            , int displayOrientation
            , @NonNull SurfaceTexture surfaceTexture
            , @NonNull CameraListener cameraListener) {
        this.mCameraListener = cameraListener;
        this.mCameraId = cameraID;
        mDisplayOrientation = displayOrientation;
        if (mCamera != null) {
            destroyCamera();
        }
        if (surfaceTexture == null) {
            mSurfaceTexture = new SurfaceTexture(TEXTURE_NAME);
        } else {
            mSurfaceTexture = surfaceTexture;
        }
    }

    /**
     * 需要预览；需要视频流回调
     * <p>
     * 有的设备需要同时打开两个摄像头，前置和后置摄像头和屏幕是同方向
     * 前置摄像头时彩色摄像头
     * 后置摄像头时红外摄像头
     *
     * @return
     */
    public synchronized int startCamera() {
        //防止多次调用OpenCamera
        if (mCamera != null) return FAILURE;
        try {
            mCamera = Camera.open(mCameraId);
            Camera.getCameraInfo(mCameraId, mCameraInfo);
            initConfig();
            setDisplayOrientation(mDisplayOrientation);
        } catch (RuntimeException runtimeException) {
            Log.i(TAG, "openCamera " + runtimeException.getMessage());
        }


        if (mCamera == null) {
            return FAILURE;
        }
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            if (mPreviewBuffer == null) {
                mPreviewBuffer = new byte[mPreviewWidth * mPreviewHeight * 3 / 2];
            }

            mCamera.setPreviewCallback(this); // 设置预览的回调

            mCamera.startPreview();
            mCameraListener.onCameraOpened(null, mCameraId, mCameraClockwiseRotationValue, false);
            return SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            destroyCamera();
            return FAILURE;
        }

    }

    /**
     * 摄像头打开后，可以切换摄像头
     */
    public void switchCamera() {
        mCameraId = 1 - mCameraId;
        stopCamera();
        startCamera();

    }


    private void initConfig() {
        Log.v(TAG, "initConfig");
        try {
            mParameters = mCamera.getParameters();
            // 如果摄像头不支持这些参数都会出错的，所以设置的时候一定要判断是否支持
            List<String> supportedFlashModes = mParameters.getSupportedFlashModes();
            if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF); // 设置闪光模式
            }
            List<String> supportedFocusModes = mParameters.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
            }
            mParameters.setPreviewFormat(ImageFormat.NV21); // 设置预览图片格式
            mParameters.setPictureFormat(ImageFormat.JPEG); // 设置拍照图片格式
            mParameters.setExposureCompensation(0); // 设置曝光强度
            Camera.Size previewSize = getSuitableSize(mParameters.getSupportedPreviewSizes());
            mPreviewWidth = previewSize.width;
            mPreviewHeight = previewSize.height;
            mParameters.setPreviewSize(mPreviewWidth, mPreviewHeight); // 设置预览图片大小
            Log.d(TAG, "previewWidth: " + mPreviewWidth + ", previewHeight: " + mPreviewHeight);
            Camera.Size pictureSize = getSuitableSize(mParameters.getSupportedPictureSizes());
            mParameters.setPictureSize(pictureSize.width, pictureSize.height);
            Log.d(TAG, "pictureWidth: " + pictureSize.width + ", pictureHeight: " + pictureSize.height);
            mCamera.setParameters(mParameters); // 将设置好的parameters添加到相机里
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Camera.Size getSuitableSize(List<Camera.Size> sizes) {
        int minDelta = Integer.MAX_VALUE; // 最小的差值，初始值应该设置大点保证之后的计算中会被重置
        int index = 0; // 最小的差值对应的索引坐标
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size previewSize = sizes.get(i);
            Log.v(TAG, "SupportedPreviewSize, width: " + previewSize.width + ", height: " + previewSize.height);
            // 找到一个与设置的分辨率差值最小的相机支持的分辨率大小
            if (previewSize.width * mPreviewScale == previewSize.height) {
                int delta = Math.abs(mPreviewWidth - previewSize.width);
                if (delta == 0) {
                    return previewSize;
                }
                if (minDelta > delta) {
                    minDelta = delta;
                    index = i;
                }
            }
        }
        return sizes.get(index); // 默认返回与设置的分辨率最接近的预览尺寸
    }

    /**
     * 要求相机显示的方向，必须设置，否则显示的图像方向会错误
     */
    private void setDisplayOrientation(int rotation) {
        // int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCameraClockwiseRotationValue = (mCameraInfo.orientation + degrees) % 360;
            mCameraClockwiseRotationValue = (360 - mCameraClockwiseRotationValue) % 360;  // compensate the mirror
        } else {  // back-facing
            mCameraClockwiseRotationValue = (mCameraInfo.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(mCameraClockwiseRotationValue);
    }

/*

    public void takePicture() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Log.i(TAG, "onPictureTaken: ");
            }
        });
    }
*/

    public void stopCamera() {
        synchronized (this) {
            if (mCamera == null) {
                return;
            }
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            if (mCameraListener != null) {
                mCameraListener.onCameraClosed();
            }
        }
    }

    /**
     * @return 彻底销毁Camera 包括初始化的参数
     */
    public int destroyCamera() {
        try {
            if (mCamera == null) {
                return SUCCESS;
            }
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCameraListener = null;
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            if (mSurfaceTexture != null) {
                mSurfaceTexture.release();
                mSurfaceTexture = null;
            }
            mCameraId = -1;
            mCamera = null;
            mPreviewBuffer = null;
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return FAILURE;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCameraListener.onPreview(data, camera);
    }
}
