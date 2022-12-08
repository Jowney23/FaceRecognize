package com.open.face.core;

import android.util.Log;


import com.arcsoft.face.FaceInfo;
import com.jowney.common.util.SoundPoolUtils;
import com.jowney.common.util.logger.L;
import com.open.face.R;
import com.open.face.model.EventTips;
import com.open.face.model.TipMessageCode;
import com.open.face.model.VideoFrameModel;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Jowney on 2018/7/18.
 */

public class FaceRecognizeThread extends BaseThread {
    private static final String TAG = "FaceRecognizeThread";
    VideoFrameModel.VideoFrame mVideoFrame;
    //通过该标记位切换比对模式,默认1：N模式
    private volatile int flagMode = IFaceBusiness.MATCH_TO_N_MODE;
    private long lastMatchOkTime = 0L;
    //上次比对与本次比对至少相隔matchFaceInterval毫秒
    private final int matchFaceInterval = 1500;
    private IAlgorithmHelper iAlgorithmHelper = ArcAlgorithmHelper.getInstance();

    private IFaceBusiness mIFaceBusiness = new IFaceBusiness() {
        @Override
        public void matchToN(byte[] videoFrame, FaceInfo faceInfo) {

            //现在是1:N模式哦亲╭(╯3╰)╮
            if (System.currentTimeMillis() - lastMatchOkTime < matchFaceInterval) {
                return;
            }
            Long a = System.currentTimeMillis();
            String vnRet = iAlgorithmHelper.identifyFaceFeature(videoFrame, faceInfo);
            Long b = System.currentTimeMillis() - a;
            Log.v("FaceRecognizeThread","1:N需要的时间：" + b);
            //匹配到你了哦！（^*_*^）
            if (vnRet != null) {
                lastMatchOkTime = System.currentTimeMillis();
                SoundPoolUtils.getInstance().play(R.raw.ding);
                EventBus.getDefault().post(new EventTips<>("您已注册", TipMessageCode.MESSAGE_COLOR_1_N_SUCCESS));
            }
        }

        @Override
        public void matchToOne() {
            Log.v(TAG, "1:1模式");
        }

        @Override
        public void enrollVideoFaceInfo(byte[] videoFrame, FaceInfo faceInfo) {
            //现在是建模模式哦亲╭(╯3╰)╮
            final String vnRet = iAlgorithmHelper.enrollFaceFeatureNV21(videoFrame, faceInfo);//成功返回模板号
            if (vnRet != null) {
                SoundPoolUtils.getInstance().play(R.raw.registerok);
            } else {
                SoundPoolUtils.getInstance().play(R.raw.ding);
                Log.i(TAG, "enrollFaceInfor: 建模失败");
            }
            flagMode = IFaceBusiness.MATCH_TO_N_MODE;
        }
    };

    /**
     * 在启动线程前，需要先设置previewTextureView、faceDetectionView
     *
     * @param name
     */
    public FaceRecognizeThread(String name) {
        super(name);

    }

    @Override
    protected void execute() {
        faceRecognize(mIFaceBusiness);
    }


    /**
     * @param iFaceBusiness 1、检测到人脸后需要进行1:1 或者 1:N比对。
     *                      2、若未读到身份证信息则用1：N ；
     *                      3、若读到身份证信息则使用1:1。使用完1:1后记得要修改flagForMatchMode重新回到1:N模式
     */
    private void faceRecognize(final IFaceBusiness iFaceBusiness) {
        mVideoFrame = VideoFrameModel.getVideoFrame();
        if (mVideoFrame != null) {
            //选择比对模式哦！（^*_*^）
            switch (flagMode) {
                case IFaceBusiness.MATCH_TO_N_MODE:
                    iFaceBusiness.matchToN(mVideoFrame.getVideoFrameData(), mVideoFrame.getFaceInfo());
                    break;
                case IFaceBusiness.MATCH_TO_ONE_MODE:
                    //         iFaceBusiness.matchToOne();
                    break;
                case IFaceBusiness.ENROLL_MODE:
                    iFaceBusiness.enrollVideoFaceInfo(mVideoFrame.getVideoFrameData(), mVideoFrame.getFaceInfo());
                    break;
                case IFaceBusiness.JUST_FACE_DETECT:
                default:
                    break;
            }

        }
    }

    public void setMode(int flagMode) {
        this.flagMode = flagMode;
    }


}
