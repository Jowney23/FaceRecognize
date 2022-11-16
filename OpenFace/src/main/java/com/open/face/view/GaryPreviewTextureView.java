package com.open.face.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;

import com.open.face.camera.CameraListener;
import com.open.face.camera.GrayCamera;
import com.open.face.model.VideoFrameModel;


/**
 * Creator: Jowney  (~._.~)
 * Date: 2019/5/26/17:39
 * Description:
 */
public class GaryPreviewTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private final static String TAG = "PreviewTextureView";

    public GaryPreviewTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        GrayCamera.getInstance().initCamera(Camera.CameraInfo.CAMERA_FACING_BACK, 0, surfaceTexture, new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {

            }

            @Override
            public void onPreview(byte[] data, Camera camera) {
                VideoFrameModel.addVideoFrame(data);
               /* if (BaseApp.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    return true;
                } else {
                    return false;
                }*/
              /*  byte[] bufferBGR = new byte[921600];
                byte[] desForYUV420spRotate = new byte[460800];
                BitmapUtils.yuv420spRotate90(desForYUV420spRotate,data, FsColorRecognizeHelper.videoBitmapH,FsColorRecognizeHelper.videoBitmapW);
                BitmapUtils.yuv420spToBGR(bufferBGR,desForYUV420spRotate,FsColorRecognizeHelper.videoBitmapW,FsColorRecognizeHelper.videoBitmapH);
                VideoFrameModel.addVideoFrame(bufferBGR);*/
/*
这段代码不要删除，困惑的时候可以看一下

                Camera.Size size = camera.getParameters().getPreviewSize();
                try {
                   */
/* byte[] des = new byte[640*480*3/2];
                    BitmapUtils.yuv420spRotate90(des,data, size.width,size.height);*//*

                    Log.i(TAG, "onPreviewFrame: " +size.height+size.width);
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width,size.height , null);
                    if (image != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                        BitmapUtils.saveBitmap(bmp, Environment.getExternalStorageDirectory() + "/" + "test/", System.currentTimeMillis()+"");
                        stream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
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
        GrayCamera.getInstance().startCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

        GrayCamera.getInstance().destroyCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        //在主线程中运行

    }


}