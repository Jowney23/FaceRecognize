package com.open.face.core;

import android.graphics.Rect;
import android.util.Log;


import com.arcsoft.face.FaceInfo;
import com.jowney.common.util.SoundPoolUtils;
import com.open.face.R;
import com.open.face.model.EventTips;
import com.open.face.model.TipMessageCode;
import com.open.face.model.VideoFrameModel;
import com.open.face.view.DrawHelper;
import com.open.face.view.FaceCoveringRectangleView;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Jowney on 2018/7/18.
 */

public class FaceRecognizeThread extends BaseThread {
    private static final String TAG = "FaceRecognizeThread";
    private FaceCoveringRectangleView faceCoveringRectangleView;

    private int bgrBytesLength = 640 * 480 * 3 / 2;
    private byte[] videoBGRForFaceCheck = new byte[bgrBytesLength];
    private int videoFaceCount;
    private long lastCheckFaceTime;
    //时隔多少毫秒检测一次人脸ms
    private int checkFaceInterval = 20;
    private boolean isCheckedFace;
    //通过该标记位切换比对模式,默认1：N模式
    private int flagMode = IFaceBusiness.MATCH_TO_N_MODE;
    private long lastMatchOkTime;
    //上次比对与本次比对至少相隔matchFaceInterval毫秒
    private int matchFaceInterval = 1500;
    private IAlgorithmHelper iAlgorithmHelper = ArcAlgorithmHelper.getInstance();

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
        faceRecognize(new IFaceBusiness() {
            @Override
            public void matchToN(byte[] videoFrame, FaceInfo faceInfo) {

                //现在是1:N模式哦亲╭(╯3╰)╮
                if (System.currentTimeMillis() - lastMatchOkTime < matchFaceInterval) {
                    return;
                }

                //  boolean vnRet = FsColorRecognizeHelper.getInstance().identifyFace(videoBGRForFaceCheck, FsColorRecognizeHelper.videoBitmapW, FsColorRecognizeHelper.videoBitmapH, FsColorRecognizeHelper.similarity, FsColorRecognizeHelper.residentTmplateId);
                boolean vnRet = iAlgorithmHelper.identifyFaceFeature(videoFrame, faceInfo);
                //匹配到你了哦！（^*_*^）
                if (vnRet) {
                    lastMatchOkTime = System.currentTimeMillis();
                    EventBus.getDefault().post(new EventTips<>("您已注册", TipMessageCode.MESSAGE_COLOR_1_N_SUCCESS));
                 /*   ModelFactory.createDataSource().queryResidentByTemplateId(Long.valueOf(FsColorRecognizeHelper.residentTmplateId[0]))
                            .subscribe(new Consumer<List<Resident>>() {
                                @Override
                                public void accept(List<Resident> residents) throws Exception {
                                    if (residents == null || residents.size() == 0) {
                                        return;
                                    }
                                    EventBus.getDefault().post(new EventTips<>(residents.get(0).getName(), TipMessageCode.MESSAGE_COLOR_1_N_SUCCESS));
                                    //    ToastUtils.show(BaseApp.getContext(), "您是：" + residents.get(0).getName() + " 您的比对分数" + FsColorRecognizeHelper.similarity[0]);
                                    //    LogUtils.v("您是：" + residents.get(0).getName() + " 您的比对分数" + FsColorRecognizeHelper.similarity[0])
                                    saveResidentPassLog(residents.get(0));

                                }
                            });*/


                }
                //
            }

            @Override
            public void matchToOne() {
                Log.v(TAG, "1:1模式");
            }

            @Override
            public void enrollFaceInfo(byte[] videoFrame, FaceInfo faceInfo) {
                //现在是建模模式哦亲╭(╯3╰)╮
                final String vnRet = iAlgorithmHelper.enrollFaceFeature(videoFrame, faceInfo);//成功返回模板号
                if (vnRet != null) {
                    SoundPoolUtils.getInstance().play(R.raw.ding);
                    flagMode = IFaceBusiness.MATCH_TO_N_MODE;
                } else {
                    Log.i(TAG, "enrollFaceInfor: 建模失败");
                }
        /*        //查找未注销的居民信息
                ModelFactory.createDataSource().queryUnlogoutFkjResidentByCardNO(IdCardBean.identity).subscribe(new Consumer<List<Resident>>() {
                    @Override
                    public void accept(List<Resident> residents) throws Exception {
                        // 1、点击隐藏键强制 -> 建模（刷身份证后不管之前是否建模，都将强制建模）
                        // 2、Excel表格导入 -> 建模（刷身正证后如果之前已经建模则，不会建模）
                        // 3、同步后台数据   -> 建模（刷身份证后如果之前已经建模，不会建模）
                        Resident resident;
                        if (residents.size() == 0) {
                            //数据库中没有该身份证信息，一定是点击隐藏键强制建模模式,因为未将居民信息提前导入数据库所以可能会出现查询redidents长度为0的的情况
                            resident = new Resident();
                        } else {
                            //数据库有该身份证信息
                            resident = residents.get(0);
                            if (resident.getIRTemplateId() != 0) {
                                //一定是点击隐藏键强制建模模式（重新建模模式）
                                FileUtils.deleteFile(resident.getIRTemplate());
                                FileUtils.deleteFile(resident.getCardImg());
                                FileUtils.deleteFile(resident.getIRPhoto());
                                FsColorRecognizeHelper.getInstance().deleteTemplate(String.valueOf(resident.getIRTemplateId()));
                            }
                        }
                        saveResident(resident, vnRet);

                    }
                });*/
            }
        });


    }


    /**
     * @param iFaceBusiness 1、检测到人脸后需要进行1:1 或者 1:N比对。
     *                      2、若未读到身份证信息则用1：N ；
     *                      3、若读到身份证信息则使用1:1。使用完1:1后记得要修改flagForMatchMode重新回到1:N模式
     */
    private void faceRecognize(final IFaceBusiness iFaceBusiness) {
        FaceInfo maxFaceInfo = null;
        //时隔checkFaceInterval检测人脸(^_^)
        /*if (System.currentTimeMillis() - lastCheckFaceTime < checkFaceInterval) {
            return;
        }*/
        lastCheckFaceTime = System.currentTimeMillis();
        try {

            videoBGRForFaceCheck = VideoFrameModel.getVideoFrame();
            if (videoBGRForFaceCheck == null) {
                /*faceDetectionView.setRect(0, 0, 0, 0);
                faceDetectionView.postInvalidate();*/
                return;
            }
            videoFaceCount = 1;
            maxFaceInfo = iAlgorithmHelper.detectFace(videoBGRForFaceCheck);
            //   Log.i(TAG, "faceRecognize: "+maxFaceInfo.toString());

        } catch (Exception e) {
            //     Log.v(TAG, "出错了！"+e.getMessage());
        }
        if (maxFaceInfo != null) {
            //检测到人脸了哦(^_^)
            isCheckedFace = true;
            //找出最大人脸
            // TODO: 2019/5/11  最大人脸功能未实现
            //  getMaxFacePoint();

            //开始画人脸框了哦(^_^)
            Rect ret = DrawHelper.adjustRect(maxFaceInfo.getRect());
            faceCoveringRectangleView.setRect(ret.left +50, ret.top +50, ret.right -ret.left -100, ret.bottom - ret.top-100);

            faceCoveringRectangleView.postInvalidate();
            //选择比对模式哦！（^*_*^）
            switch (flagMode) {
                case IFaceBusiness.MATCH_TO_N_MODE:
                    iFaceBusiness.matchToN(videoBGRForFaceCheck, maxFaceInfo);
                    break;
                case IFaceBusiness.MATCH_TO_ONE_MODE:
                    //         iFaceBusiness.matchToOne();
                    break;
                case IFaceBusiness.ENROLL_MODE:
                    iFaceBusiness.enrollFaceInfo(videoBGRForFaceCheck, maxFaceInfo);
                    break;
                case IFaceBusiness.JUST_FACE_DETECT:
                    break;
                default:
                    break;
            }

        } else {
            // 检测到人脸个数为0 // 检测过程中出错  // 检测到的人脸质量不合格
            //   LogUtils.v(" 检测到人脸个数为0, 检测过程中出错, 检测到的人脸质量不合格 ");
            faceCoveringRectangleView.setRect(0, 0, 0, 0);
            faceCoveringRectangleView.postInvalidate();
        }


    }

    public void setView(FaceCoveringRectangleView faceCoveringRectangleView) {

        this.faceCoveringRectangleView = faceCoveringRectangleView;
    }


    public void setMode(int flagMode) {
        this.flagMode = flagMode;
    }


}
