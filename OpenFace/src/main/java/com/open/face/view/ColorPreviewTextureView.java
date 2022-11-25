package com.open.face.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.arcsoft.face.FaceInfo;
import com.open.face.camera.CameraListener;
import com.open.face.camera.ColorCamera;
import com.open.face.core.ArcAlgorithmHelper;


/**
 * Creator: Jowney  (~._.~)
 * Date: 2018/5/9/23:23
 * Description:
 */

public class ColorPreviewTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private final static String TAG = "PreviewTextureView";
    private FaceCoveringCircleView faceCoveringView;

    public ColorPreviewTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable:  预览画面大小   宽:" + width + "  高:" + height);

        ColorCamera.getInstance().initCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, 1, 2, 3, 4, 0, surfaceTexture, new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                DrawHelper.init(640, 480, 960, 1280, displayOrientation, 1, false, false, false);
            }

            @Override
            public void onPreview(byte[] data, Camera camera) {
                //   Log.d(TAG, "当前线程:" + Thread.currentThread().getName() + " onPreview: " + data.toString());
                FaceInfo maxFaceInfo = ArcAlgorithmHelper.getInstance().detectFace(data);
                if (maxFaceInfo != null) {
                    //开始画人脸框了哦(^_^)
                    Rect ret = DrawHelper.adjustRect(maxFaceInfo.getRect());
                    faceCoveringView.setFaceRect(ret);
                } else {
                    // 检测到人脸个数为0 // 检测过程中出错  // 检测到的人脸质量不合格
                    faceCoveringView.setFaceRect(null);
                }
                //   VideoFrameModel.addVideoFrame(data);
            }

            @Override
            public void onCameraClosed() {

            }

            @Override
            public void onCameraError(Exception e) {

            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

            }
        });
        ColorCamera.getInstance().startCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.v(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        ColorCamera.getInstance().destroyCamera();
        Log.v(TAG, "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        //在主线程中运行
    }

    public void setView(FaceCoveringCircleView faceCoveringView) {

        this.faceCoveringView = faceCoveringView;
    }
}
