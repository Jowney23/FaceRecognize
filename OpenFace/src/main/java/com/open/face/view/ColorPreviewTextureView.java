package com.open.face.view;

import android.content.Context;
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
import com.open.face.model.EventTips;
import com.open.face.model.TipMessageCode;
import com.open.face.model.VideoFrameModel;

import org.greenrobot.eventbus.EventBus;


/**
 * Creator: Jowney  (~._.~)
 * Date: 2018/5/9/23:23
 * Description:
 */

public class ColorPreviewTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private final static String TAG = "PreviewTextureView";
    private FaceCoveringCircleView mFaceCoveringView;
    //最近一次检测到人脸的时间
    private Long mLastTimeDetectFace = 0L;
    //间隔这么长时间未检测到人脸，需要发送“休息信号”
    private int mInterval = 5000;
    //是否已经发送了“休息信号”
    private boolean mIsSendSleepMessage = false;
    //每次检测到人脸时 丢弃第一帧，因为第一帧是人动态调整的过程，采集的人脸较模糊，或者不完整
    private int mAbandonFrameNumber = 1;

    public ColorPreviewTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        ColorCamera.getInstance().initCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, width, height, 0, surfaceTexture, new CameraListener() {
            @Override
            public void onCameraOpened(int nv21Width, int nv21Height, int canvasWidth,
                                       int canvasHeight, int cameraId, int cameraClockwiseRotationValue, boolean isMirror) {
                Log.d(TAG, "打开相机:  nv21Width:" + nv21Width + "  nv21Height:" + nv21Width + "  canvasWidth:" + canvasWidth + "  canvasHeight:" + canvasHeight);
                DrawFaceCoveringHelper.init(nv21Width, nv21Height, canvasWidth, canvasHeight, cameraClockwiseRotationValue, cameraId, false, false, false);
            }

            @Override
            public void onPreview(byte[] data) {
                //   Log.d(TAG, "当前线程:" + Thread.currentThread().getName() + " onPreview: " + data.toString());
                FaceInfo maxFaceInfo = ArcAlgorithmHelper.getInstance().detectMaxFace(data);
                if (maxFaceInfo != null) {
                    //开始画人脸框了哦(^_^)
                    if (mIsSendSleepMessage) {
                        EventBus.getDefault().post(new EventTips<String>("Have_Face", TipMessageCode.Message_Color_Recognize_Resume));
                        mIsSendSleepMessage = false;
                    }
                    Rect ret = DrawFaceCoveringHelper.adjustRect(maxFaceInfo.getRect());
                    mFaceCoveringView.setFaceRect(ret);
                    if (mAbandonFrameNumber != 0) {
                        mAbandonFrameNumber--;
                        return;
                    }
                    VideoFrameModel.addVideoFrame(data, maxFaceInfo);
                    mLastTimeDetectFace = System.currentTimeMillis();

                } else {
                    // 检测到人脸
                    mAbandonFrameNumber = 1;
                    mFaceCoveringView.setFaceRect(null);
                    if (System.currentTimeMillis() - mLastTimeDetectFace >= mInterval && !mIsSendSleepMessage) {
                        EventBus.getDefault().post(new EventTips<String>("Long_Time_No_Face", TipMessageCode.Message_Color_Recognize_Pause));
                        mIsSendSleepMessage = true;
                    }
                }
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
        this.mFaceCoveringView = faceCoveringView;
    }
}
