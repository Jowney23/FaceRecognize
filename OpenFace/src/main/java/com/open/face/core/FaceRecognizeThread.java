/*
package com.open.face.core;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;


import com.jowney.common.util.logger.L;
import com.open.face.model.EventTips;
import com.open.face.model.TipMessageCode;
import com.open.face.view.FaceDetectionView;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import cn.face.sdk.CWConstant;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;



*/
/**
 * Created by Jowney on 2018/7/18.
 *//*


public class FaceRecognizeThread extends BaseThread {
    private static final String TAG = "FaceRecognizeThread";
    private FaceDetectionView faceDetectionView;

    private int bgrBytesLength = CWConstant.VIDEO_H * CWConstant.VIDEO_W * 3 / 2;
    private byte[] videoBGRForFaceCheck = new byte[bgrBytesLength];
    private int videoFaceCount;
    private long lastCheckFaceTime;
    //时隔多少毫秒检测一次人脸
    private int checkFaceInterval = 50;
    private boolean isCheckedFace;
    //通过该标记位切换比对模式,默认1：N模式
    private int flagMode = MATCH_TO_N_MODE;
    private long lastMatchOkTime;
    //上次比对与本次比对至少相隔matchFaceInterval毫秒
    private int matchFaceInterval = 1500;
    private IFaceHelper iFaceHelper = CWFaceHelper.getInstance();

    */
/**
     * 在启动线程前，需要先设置previewTextureView、faceDetectionView
     *
     * @param name
     *//*

    public FaceRecognizeThread(String name) {
        super(name);

    }

    @Override
    public void execute() {
        faceRecognize(new IFaceRecognize() {
            @Override
            public void matchToN() {

                //现在是1:N模式哦亲╭(╯3╰)╮
                if (System.currentTimeMillis() - lastMatchOkTime < matchFaceInterval) {
                    return;
                }

                //  boolean vnRet = FsColorRecognizeHelper.getInstance().identifyFace(videoBGRForFaceCheck, FsColorRecognizeHelper.videoBitmapW, FsColorRecognizeHelper.videoBitmapH, FsColorRecognizeHelper.similarity, FsColorRecognizeHelper.residentTmplateId);
                boolean vnRet = iFaceHelper.identifyFace();
                //匹配到你了哦！（^*_*^）
                if (vnRet) {
                    lastMatchOkTime = System.currentTimeMillis();
                    EventBus.getDefault().post(new EventTips<>("您已注册", TipMessageCode.MESSAGE_COLOR_1_N_SUCCESS));
                 */
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
                            });*//*



                }
                //
            }

            @Override
            public void matchToOne() {
                L.v("1:1模式");
                if (IdCardBean.idCardBitmap == null) {
                    return;
                }
                byte[] idCardBGR = BitmapUtils.bitmap2BGR(IdCardBean.idCardBitmap);
                Double dd = FsColorRecognizeHelper.getInstance().identifyFace(idCardBGR, videoBGRForFaceCheck);

                if (dd != null && dd > 0.55) {
                    */
/*比对成功保存访客通行记录*//*

                    saveFkjVisitor();
                    EventBus.getDefault().post(new EventTips<>("1:1比对成功", TipMessageCode.MESSAGE_COLOR_1_1_SUCCESS));

                } else {
                    EventBus.getDefault().post(new EventTips<>("1:1比对失败", TipMessageCode.MESSAGE_COLOR_1_1_FAILURE));
                }
                flagMode = MATCH_TO_N_MODE;
            }

            @Override
            public void enrollFaceInfor() {
                //现在是建模模式哦亲╭(╯3╰)╮
                final String vnRet = iFaceHelper.enrollTemplate(videoBGRForFaceCheck);//成功返回模板号
                if (vnRet != null) {
                    SoundPoolUtils.getInstance().play(R.raw.ding);
                    flagMode = MATCH_TO_N_MODE;
                } else {
                    Log.i(TAG, "enrollFaceInfor: 建模失败");
                }
        */
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
                });*//*

            }
        });


    }


    */
/**
     * @param IFaceRecognize 1、检测到人脸后需要进行1:1 或者 1:N比对。
     *                       2、若未读到身份证信息则用1：N ；
     *                       3、若读到身份证信息则使用1:1。使用完1:1后记得要修改flagForMatchMode重新回到1:N模式
     *//*

    private void faceRecognize(final IFaceRecognize IFaceRecognize) {

        //时隔checkFaceInterval检测人脸(^_^)
        if (System.currentTimeMillis() - lastCheckFaceTime < checkFaceInterval) {
            return;
        }
        lastCheckFaceTime = System.currentTimeMillis();
        try {

            videoBGRForFaceCheck = VideoFrameModel.pollVideoFrame();
            if (videoBGRForFaceCheck == null) {
                return;
            }
            videoFaceCount = CWFaceHelper.getInstance().detectFace(videoBGRForFaceCheck);
            // Log.i(TAG, "faceRecognize: "+CWFaceHelper.mFaceInfoArray[0].scores[0]);

        } catch (Exception e) {
            LogUtils.v("出错了！");
        }
        if (videoFaceCount >= 1) {
            //检测到人脸了哦(^_^)
            isCheckedFace = true;
            //找出最大人脸
            // TODO: 2019/5/11  最大人脸功能未实现
            //  getMaxFacePoint();

            //开始画人脸框了哦(^_^)
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {
                    faceDetectionView.setRect(CWFaceHelper.mFaceInfoArray[0].x+40, CWFaceHelper.mFaceInfoArray[0].y+30, CWFaceHelper.mFaceInfoArray[0].width+40, CWFaceHelper.mFaceInfoArray[0].height+30);

                    faceDetectionView.invalidate();
                }
            }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
            //选择比对模式哦！（^*_*^）
            switch (flagMode) {
                case MATCH_TO_N_MODE:
                    IFaceRecognize.matchToN();
                    break;
                case MATCH_TO_ONE_MODE:
                    //         IFaceRecognize.matchToOne();
                    break;
                case ENROLL_MODE:
                    IFaceRecognize.enrollFaceInfor();
                    break;
                case JUST_FACE_DETECT:
                    break;
                default:
                    break;
            }

        } else {
            // 检测到人脸个数为0 // 检测过程中出错  // 检测到的人脸质量不合格
            //   LogUtils.v(" 检测到人脸个数为0, 检测过程中出错, 检测到的人脸质量不合格 ");
            if (isCheckedFace) {
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        faceDetectionView.setRect(0, 0, 0, 0);
                        faceDetectionView.invalidate();
                        isCheckedFace = false;
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();

            }
        }


    }

    private void saveFkjVisitor() {
        FkjVisitorRegInfo fkjVisitorRegInfo = new FkjVisitorRegInfo();
        String personId = UUID.randomUUID().toString();
        long passTime = System.currentTimeMillis() / 1000;
        fkjVisitorRegInfo.setPersonId(personId);
        //身份证信息
        fkjVisitorRegInfo.setName(IdCardBean.name);
        fkjVisitorRegInfo.setGender(IdCardBean.genderCode); //数据库中统一保存男女Code，男:"1"  女:"2"
        fkjVisitorRegInfo.setCardNo(IdCardBean.identity);
        fkjVisitorRegInfo.setNation(IdCardBean.nation);
        fkjVisitorRegInfo.setBirthday(IdCardBean.birdthDay);
        fkjVisitorRegInfo.setCardAddr(IdCardBean.address);
        fkjVisitorRegInfo.setIssuingUnit(IdCardBean.signOffice);
        fkjVisitorRegInfo.setCardStartTime(IdCardBean.startDate);
        fkjVisitorRegInfo.setCardEndTime(IdCardBean.endDate);
        //图片信息
        fkjVisitorRegInfo.setCardImg(BitmapUtils.saveBitmap(IdCardBean.idCardBitmap, Environment.getExternalStorageDirectory().toString() + "/" + "YZFace" + "/" + "passLog" + "/" + "visitor" + "/" + "idCardPhoto" + "/", String.valueOf(passTime * 1000)) + String.valueOf(passTime * 1000) + ".jpg");
        fkjVisitorRegInfo.setSceneImg(BitmapUtils.saveBitmap(BitmapUtils.bgrToBitmap(videoBGRForFaceCheck, FsColorRecognizeHelper.videoBitmapW, FsColorRecognizeHelper.videoBitmapH), Environment.getExternalStorageDirectory().toString() + "/" + "YZFace" + "/" + "passLog" + "/" + "visitor" + "/" + "scenePhoto" + "/", String.valueOf(passTime * 1000)) + String.valueOf(passTime * 1000) + ".jpg");
        //通信信息
        fkjVisitorRegInfo.setRegTime(passTime);
        //数据上传标志位
        fkjVisitorRegInfo.setStatus(1);//1:在访客注册 0：在访客注销
        fkjVisitorRegInfo.setRsyncYzStatus(0);
        fkjVisitorRegInfo.setRsyncHJStatus(0);
        ModelFactory.createDataSource().saveVisitor(fkjVisitorRegInfo);
    }

    private void saveResidentPassLog(Resident resident) {
        PassLog passLog = new PassLog();
        long passTime = System.currentTimeMillis() / 1000;
        //云智平台需要保存的数据
        passLog.setCardNo(resident.getCardNo());
        passLog.setPassTime(passTime);
        passLog.setGenderCode(resident.getGender());
        passLog.setName(resident.getName());
        passLog.setPersonType(1);
        passLog.setSyncStateYz(0);
        //汇聚平台需要的数据
        passLog.setRsyncHJStatus(0);
        passLog.setPersonId(resident.getPersonId());
        passLog.setScenePhotoPath(BitmapUtils.saveBitmap(BitmapUtils.bgrToBitmap(videoBGRForFaceCheck, FsColorRecognizeHelper.videoBitmapW, FsColorRecognizeHelper.videoBitmapH), Environment.getExternalStorageDirectory().toString() + "/" + "YZFace" + "/" + "passLog" + "/" + "resident" + "/" + "scenePhoto" + "/", String.valueOf(passTime * 1000)) + String.valueOf(passTime * 1000) + ".jpg");


        ModelFactory.createLogDataSouce().savePassLog(passLog);
    }

    private void saveResident(Resident resident, long templateName) {
        if (resident == null) return;
        String personId = UUID.randomUUID().toString();
        resident.setPersonId(personId);
        resident.setIRTemplate(FsColorRecognizeHelper.TEMPLATE_DB_PATH + templateName);
        resident.setIRTemplateId(templateName);
        resident.setIRTemplateVersion(0);
        resident.setStatus("0");
        //身份证信息（重新赋值一遍，防止后台通过Excle表导入的大量数据只有“姓名”和“身份证号”）
        resident.setName(IdCardBean.name);
        resident.setGender(IdCardBean.genderCode); //数据库中统一保存男女Code，男:"1"  女:"2"
        resident.setCardNo(IdCardBean.identity);
        resident.setNation(IdCardBean.nation);
        resident.setBirthday(IdCardBean.birdthDay);
        resident.setCardAddr(IdCardBean.address);
        resident.setIssuingUnit(IdCardBean.signOffice);
        resident.setCardStartTime(IdCardBean.startDate);
        resident.setCardEndTime(IdCardBean.endDate);
        //图片信息
        resident.setCardImg(BitmapUtils.saveBitmap(IdCardBean.idCardBitmap, IDCARD_PHOTO_DB_PATH, personId) + personId + ".jpg");
        resident.setIRPhoto(BitmapUtils.saveBitmap(BitmapUtils.bgrToBitmap(videoBGRForFaceCheck, FsColorRecognizeHelper.videoBitmapW, FsColorRecognizeHelper.videoBitmapH), ENROLL_PHOTO_DB_PATH, personId) + personId + ".jpg");

        ModelFactory.createDataSource().saveResident(resident);

    }

    public void setView(FaceDetectionView faceDetectionView) {

        this.faceDetectionView = faceDetectionView;
    }


    public void setMode(int flagMode) {
        this.flagMode = flagMode;
    }

    private void getMaxFacePoint() {
        double maxFaceArea = 0;
        int maxFaceIndex = 0;
        for (int i = 0; i < videoFaceCount; i++) {
            double temArea = FsColorRecognizeHelper.rectarrayVideo[i * 22 + 2] * FsColorRecognizeHelper.rectarrayVideo[i * 22 + 3];
            if (temArea > maxFaceArea) {
                maxFaceArea = temArea;
                maxFaceIndex = i;
            }
        }
        for (int i = 0; i < 22; i++) {
            maxFaceRectArray[i] = FsColorRecognizeHelper.rectarrayVideo[maxFaceIndex * 22 + i];//left
        }

    }


    public static class IdCardBean {
        public static String name;
        public static String gender;
        public static String nation;
        public static String genderCode;
        public static String birdthDay;
        public static String address;
        public static String identity; //身份证号
        public static String signOffice;  //签发证机关
        public static String startDate;
        public static String endDate;
        public static Bitmap idCardBitmap;
        public static String nationCode;
        public static String idCardPhotoPath;
        public static String scenePhotoPath;
    }
}
*/
