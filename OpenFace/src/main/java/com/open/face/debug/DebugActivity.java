package com.open.face.debug;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import android.view.View;


import androidx.annotation.Nullable;

import com.hjq.toast.ToastUtils;
import com.jowney.common.util.logger.L;
import com.open.face.R;
import com.open.face.camera.ColorCamera;
import com.open.face.core.ArcAlgorithmHelper;
import com.open.face.model.EventTips;
import com.open.face.model.TipMessageCode;
import com.open.face.view.ColorPreviewTextureView;
import com.open.face.view.FaceCoveringCircleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Jowney on 2018/6/26.
 */

public class DebugActivity extends Activity {
    //flag防止一直在刷新View
    private long flag;
    FaceCoveringCircleView faceCoveringView;
    ColorPreviewTextureView colorPreviewTextureView;

    boolean libraryExists = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facerecognize_debugactivitylayout);
        EventBus.getDefault().register(this);
        ApplicationInfo applicationInfo = getApplicationInfo();

        ArcAlgorithmHelper.getInstance().initEngine(this);
        colorPreviewTextureView = findViewById(R.id.facerecognize_previewTextureView);
        faceCoveringView = findViewById(R.id.facerecognize_faceDetectionView);
        colorPreviewTextureView.setView(faceCoveringView);
       /* FaceRecognizeThread tem = new FaceRecognizeThread("face_thread");
        tem.setView(faceDetectionView);
        tem.start();*/
        findViewById(R.id.facerecognize_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorCamera.getInstance().switchCamera();
            }
        });
      /*  int initOK = FsGrayRecognizeHelper.getInstance().initGrayRecognize();
        if (initOK != FsGrayRecognizeHelper.PFSBIO_OK) {
            // TODO: 2018/7/6 dialog  提示算法未升级

        }*/
/*
        //创建以后一定记得 不用的时候要销毁
        GrayCamera.getInstance().createCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

                myImageProcess.Transpose90(data, FsGrayRecognizeHelper.img_w[0], FsGrayRecognizeHelper.img_h[0], FsGrayRecognizeHelper.data_BGR_90, FsGrayRecognizeHelper.img_w_rotate90, FsGrayRecognizeHelper.img_h_rotate90);
                int vnRet = FsBioFaceJNI.getInstance().fsFcCheck(FsGrayRecognizeHelper.mContextID, FsGrayRecognizeHelper.data_BGR_90, FsGrayRecognizeHelper.img_w_rotate90[0], FsGrayRecognizeHelper.img_h_rotate90[0], FsGrayRecognizeHelper.faceRect);
                if (vnRet == FsGrayRecognizeHelper.PFSBIO_OK) {
                    faceDetectionView.setRect(FsGrayRecognizeHelper.faceRect[0], FsGrayRecognizeHelper.faceRect[2], FsGrayRecognizeHelper.faceRect[1] - FsGrayRecognizeHelper.faceRect[0], FsGrayRecognizeHelper.faceRect[3] - FsGrayRecognizeHelper.faceRect[2]);
                    faceDetectionView.invalidate();
                    flag++;
                }else {
                    if (flag!=0){
                        faceDetectionView.setRect(-10, -10, -10, -10);
                        faceDetectionView.invalidate();
                        flag=0;
                    }

                }

                FsGrayRecognizeHelper.getInstance().CreateGrayBitmapFromByteArry(FsGrayRecognizeHelper.data_BGR_90, FsGrayRecognizeHelper.img_w_rotate90[0], FsGrayRecognizeHelper.img_h_rotate90[0]);
            }
        });*/


    }


    //所有的提示事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessageTip(EventTips eventTips) {
        switch (eventTips.getCode()) {
            case TipMessageCode.MESSAGE_COLOR_FACERECOGNIZE_ERROR:
                ToastUtils.show(eventTips.getData().toString());
                break;

            default:
                break;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
      int pp =  ColorCamera.getInstance().startCamera();
        L.v("打开摄像头的结果"+pp);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ColorCamera.getInstance().stopCamera();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
