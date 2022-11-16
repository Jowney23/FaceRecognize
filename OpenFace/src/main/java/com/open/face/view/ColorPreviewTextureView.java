package com.open.face.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;

import com.open.face.camera.CameraListener;
import com.open.face.camera.ColorCamera;


/**
 * Creator: Jowney  (~._.~)
 * Date: 2018/5/9/23:23
 * Description:
 */

public class ColorPreviewTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private final static String TAG = "PreviewTextureView";

    public ColorPreviewTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

        ColorCamera.getInstance().initCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, 0, surfaceTexture, new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {

            }

            @Override
            public void onPreview(byte[] data, Camera camera) {

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

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        ColorCamera.getInstance().destroyCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        //在主线程中运行

    }


}
