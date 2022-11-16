/*
package com.open.face.core;

import android.util.Log;

import com.example.frsdktest.CaffeMobile;
import com.yunzhi.common.base.EventTips;
import com.yunzhi.common.base.TipMessageCode;
import com.yunzhi.common.util.FileUtils;
import com.yunzhi.common.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import cn.face.sdk.CWConstant;
import cn.face.sdk.FaceCommon;
import cn.face.sdk.FaceDetTrack;
import cn.face.sdk.FaceInfo;
import cn.face.sdk.FaceInterface;
import cn.face.sdk.FaceParam;
import cn.face.sdk.FaceRecog;

import static cn.face.sdk.FaceInterface.cw_op_t.CW_OP_ALL;
import static cn.face.sdk.FaceInterface.cw_op_t.CW_OP_DET;
import static cn.face.sdk.FaceInterface.cw_op_t.CW_OP_QUALITY;

*/
/**
 * Creator: Jowney  (~._.~)
 * Date: 2019/5/10/0:12
 * Description:
 *//*

public class CWFaceHelper implements IFaceHelper {
    private static final String TAG = "CWFaceHelper";
    private static CWFaceHelper mCWFaceHelper = null;
    private int mDetectHandle = -1;
    private int mRecognizeHandle = -1;
    private int mFaceFeatureLength = 0;//人脸模型长度
    public static FaceInfo[] mFaceInfoArray;
    //该Map用来加载所有住户的特征值，key为特征值在本地保存的名称，value为特征值
    private static Map<String, byte[]> residentsFeatureGlobal = new HashMap<>();
    //应有的模版数量
    private int templateCountGlobal;
    //实际加载的模版数量
    private int loadedTemplateCountGlobal;

    public static float[] similarity = new float[1];

    public static String[] residentTmplateId = new String[1];

    //................

    */
/**
     * VIDEO模式人脸检测引擎，用于预览帧人脸追踪
     *//*

    private FaceEngine ftEngine;
    */
/**
     * 用于特征提取的引擎
     *//*

    private FaceEngine frEngine;
    */
/**
     * IMAGE模式活体检测引擎，用于预览帧人脸活体检测
     *//*

    private FaceEngine flEngine;

    public static CWFaceHelper getInstance() {
        if (mCWFaceHelper == null) {
            mCWFaceHelper = new CWFaceHelper();
        }
        return mCWFaceHelper;
    }

    @Override
    public String init() {
        //创建人脸数组 用来存储人脸
        mFaceInfoArray = new FaceInfo[10];
        for (int i = 0; i < 10; i++) {
            mFaceInfoArray[i] = new FaceInfo();
        }

        //创建识别和检测句柄
        mDetectHandle = createFaceDetectHandle(CWConstant.CW_LICENSE, CWConstant.faceMinSize, CWConstant.faceMaxSize, CWConstant.Model_PATH);
        mRecognizeHandle = createRecognizeHandle(CWConstant.CW_LICENSE, CWConstant.Model_PATH);
        if (mDetectHandle >= FaceInterface.cw_errcode_t.CW_UNKNOWN_ERR || mRecognizeHandle >= FaceInterface.cw_errcode_t.CW_UNKNOWN_ERR) {
            return "失败";
        } else {
            //获取人脸特征值需要的数组长度
            mFaceFeatureLength = FaceRecog.cwGetFeatureLength(mRecognizeHandle);
            Log.i("wxy", "init: "+mFaceFeatureLength);
            return "成功";
        }

    }

    @Override
    public String enrollTemplate(byte[] srcVideoData) {
        String ret = null;
        byte[] feature = new byte[mFaceFeatureLength];
        try {
            String newTemplateID = UUID.randomUUID().toString().replace("-", "");
            //本地保存一份
            int videoFaceCount = detectFace(srcVideoData);
            Log.i(TAG, "enrollTemplate: " + mFaceInfoArray[0].scores[0]);
            if (videoFaceCount == 1 && mFaceInfoArray[0].scores[0] >= ENROLL_THRESHOLD_VALUE) {
                Log.i(TAG, "enrollTemplate: " + mFaceInfoArray[0].scores[0]);
                //提取视频流中人脸特征值(^*_*^)
                FaceRecog.cwGetFaceFeature(mRecognizeHandle, mFaceInfoArray[0].alignedData, mFaceInfoArray[0].alignedW, mFaceInfoArray[0].alignedH, mFaceInfoArray[0].nChannels, feature);
                if (FileUtils.writeByteArrayToFile(IFaceHelper.TEMPLATE_DB_PATH_DIR + "/" + newTemplateID, feature)) {
                    ret = newTemplateID;
                } else {
                    return null;
                }

            } else {
                return null;
            }


            //内存保存一份
            residentsFeatureGlobal.put(newTemplateID, feature);
            templateCountGlobal += 1;
            loadedTemplateCountGlobal += 1;
            EventBus.getDefault().post(new EventTips<>("应加载的模板数量：" + templateCountGlobal, TipMessageCode.MESSAGE_LOADT_TEMPLATE_START));
            EventBus.getDefault().post(new EventTips<>("实际加载的模板数量：" + loadedTemplateCountGlobal, TipMessageCode.MESSAGE_LOADT_TEMPLATE_END));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return ret;
    }

    @Override
    public void updateTemplate(byte[] srcVideoData, String templateID,float faceQualityScore) {
       */
/* String ret = null;
        byte[] feature = new byte[mFaceFeatureLength];
        try {
            if (faceQualityScore >= FACE_QULITY_UPDATE_THRESHOLD_VALUE) {
                Log.i(TAG, "enrollTemplate: " + mFaceInfoArray[0].scores[0]);
                //提取视频流中人脸特征值(^*_*^)
                FaceRecog.cwGetFaceFeature(mRecognizeHandle, mFaceInfoArray[0].alignedData, mFaceInfoArray[0].alignedW, mFaceInfoArray[0].alignedH, mFaceInfoArray[0].nChannels, feature);

                if (FileUtils.writeByteArrayToFile(IFaceHelper.TEMPLATE_DB_PATH_DIR + "/" + templateID, feature)) {
                    ret = newTemplateID;
                } else {
                    return null;
                }

            } else {
                return null;
            }


            //内存保存一份
            residentsFeatureGlobal.put(newTemplateID, feature);
            templateCountGlobal += 1;
            loadedTemplateCountGlobal += 1;
            EventBus.getDefault().post(new EventTips<>("应加载的模板数量：" + templateCountGlobal, TipMessageCode.MESSAGE_LOADT_TEMPLATE_START));
            EventBus.getDefault().post(new EventTips<>("实际加载的模板数量：" + loadedTemplateCountGlobal, TipMessageCode.MESSAGE_LOADT_TEMPLATE_END));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return ret;*//*

    }

    @Override
    public void loadAllTemplate() {
        //加载所有模版数据
        new Thread(new Runnable() {
            @Override
            public void run() {

                String[] mTemplateNames;
                //应有的模版数量
                templateCountGlobal = 0;
                //实际加载的模版数量
                loadedTemplateCountGlobal = 0;
                int version = 0;
                Boolean vnRet;
                //初始化文件
                File mDir = new File(INIT_DATA_PATH_DIR);//YZ文件夹
                if (!mDir.isDirectory() && !mDir.mkdir()) return;


                File mTemplateDir = new File(TEMPLATE_DB_PATH_DIR);//模版文件夹
                if (!mTemplateDir.isDirectory() && !mTemplateDir.mkdir()) return;


                //获取所有模版的名字,名字就是改模板的ID，所以名字要用数字
                mTemplateNames = mTemplateDir.list();
                //模板的数量
                templateCountGlobal = mTemplateNames.length;
                EventBus.getDefault().post(new EventTips<>("应加载的模板数量：" + templateCountGlobal, TipMessageCode.MESSAGE_LOADT_TEMPLATE_START));
                for (String mTemplateName : mTemplateNames) {
                    byte[] feature = new byte[mFaceFeatureLength];
                    //将每个模板都加载到内存中。某个模板在加载的过程中可能会出现异常，异常如果出现就重新加载这个模板

                    try {
                        if (mTemplateName.contains("-")) {
                            version = Integer.valueOf(mTemplateName.split("-")[0]);
                        } else {
                            version = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    if (version != TEMPLATE_VERSION) {
                        //TODO: 2018/7/6 提示模版需要升级
                        break;
                    }
                    //加载模板
                    try {
                        Log.i("wxy", "本地加载run: 人脸模板长度："+mFaceFeatureLength);
                        vnRet = FileUtils.readByteArrayFromFile(IFaceHelper.TEMPLATE_DB_PATH_DIR + "/" + mTemplateName, feature, mFaceFeatureLength);
                        if (vnRet) {
                            residentsFeatureGlobal.put(mTemplateName, feature);
                        } else {
                            LogUtils.v("卧槽！模板号为：" + mTemplateName + " 的老兄模板加载失败了");
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: 2018/7/6 如果有人的模板为空应该提示
                        LogUtils.v("卧槽！模板号为：" + mTemplateName + "的老兄加载时发生了异常");
                        continue;
                    }
                    //模版加载成功后 跳出循环 继续下一个
                    loadedTemplateCountGlobal++;

                }
                // TODO: 2018/7/6  显示实际加载的模版数量loadedTemplateCount
                EventBus.getDefault().post(new EventTips<>("实际加载的模板数量：" + loadedTemplateCountGlobal, TipMessageCode.MESSAGE_LOADT_TEMPLATE_END));
            }
        }).start();
    }

    @Override
    public void deleteTemplate(String templateName) {

    }

    @Override
    public int detectFace(byte[] videoPicture) {
        return FaceDetTrack.cwFaceDetection(mDetectHandle, videoPicture, CWConstant.VIDEO_W, CWConstant.VIDEO_H, FaceInterface.cw_img_form_t.CW_IMAGE_NV21, FaceInterface.cw_img_angle_t.CW_IMAGE_ANGLE_270
                , FaceInterface.cw_img_mirror_t.CW_IMAGE_MIRROR_NONE, CW_OP_ALL, mFaceInfoArray);
    }

    */
/**
     * 1:N
     *
     * @return
     *//*

    @Override
    public boolean identifyFace() {
        float similarityTem = 0;
        //第一步生成特征值
        byte[] feature = new byte[mFaceFeatureLength];
        LogUtils.v("比对份数：" +"开始获取特征值");
        int result = FaceRecog.cwGetFaceFeature(mRecognizeHandle, mFaceInfoArray[0].alignedData, mFaceInfoArray[0].alignedW, mFaceInfoArray[0].alignedH, mFaceInfoArray[0].nChannels, feature);

        // int result = CaffeMobile.getInstance().FFFeaExtract(FeatureHandle[0], videoBGR, faceW, faceH, videoFaceFeature, maxFaceRectArray);
        if (result != 0) {
            LogUtils.v("比对份数：" +"开始获取特征值失败");
            return false;
        }
        LogUtils.v("比对份数：" +"开始获取特征值城功");
        //第二步存储住户模版的residentsFeature中去比对
        Map.Entry<String, byte[]> entry;
        Iterator<Map.Entry<String, byte[]>> residentIterator = residentsFeatureGlobal.entrySet().iterator();
        while (residentIterator.hasNext()) {
            entry = residentIterator.next();
            FaceRecog.cwComputeMatchScore(mRecognizeHandle, feature, entry.getValue(), 1, similarity);
            LogUtils.v("比对份数：" + similarity[0]);
            if (similarity[0] > similarityTem) {
                similarityTem = similarity[0];
                residentTmplateId[0] = entry.getKey();
            }
        }

        if (similarityTem >= RECOGNIZE_THRESHOLD_VALUE) {
             //  LogUtils.v("比对份数：" + similarity[0]);
            return true;
        } else {
            return false;
        }
    }

    */
/**
     * 1:1
     *
     * @param idCardPicture
     * @param videoPicture
     * @return
     *//*

    @Override
    public Double identifyFace(byte[] idCardPicture, byte[] videoPicture) {
        return null;
    }

    @Override
    public void destroy() {
        if (mRecognizeHandle != -1) {
            int ret = FaceRecog.cwReleaseRecogHandle(mRecognizeHandle);
            if (ret == FaceInterface.cw_errcode_t.CW_SDKLIT_OK)
                mRecognizeHandle = -1;
        }

        if (mDetectHandle != -1) {
            int ret = FaceDetTrack.cwReleaseDetHandle(mDetectHandle);
            if (ret == FaceInterface.cw_errcode_t.CW_SDKLIT_OK)
                mDetectHandle = -1;
        }

    }

    @Override
    public String switchErrorReason(int flag) {
        return null;
    }

    private int createFaceDetectHandle(String license, int faceMinSize, int faceMaxSize, String modelPath) {
        // 检测配置文件路径，默认放置到sdcard根目录
        String sDetModelPath = new StringBuilder(modelPath).append(File.separator).append("_configs_frontend_x86_arm.xml").toString();
        int ret = FaceDetTrack.cwCreateDetHandle(sDetModelPath, license);
        if (ret >= FaceInterface.cw_errcode_t.CW_UNKNOWN_ERR) {
            EventBus.getDefault().post(new EventTips<String>("算法获取句柄时出错:" + ret, TipMessageCode.MESSAGE_COLOR_FACERECOGNIZE_ERROR));
            return ret;
        }
        // 创建句柄后设置检测参数
        FaceParam faceParam = new FaceParam();
        FaceDetTrack.cwGetFaceParam(ret, faceParam);
        faceParam.minSize = faceMinSize;
        faceParam.maxSize = faceMaxSize;
        FaceDetTrack.cwSetFaceParam(ret, faceParam, sDetModelPath);
        return ret;

    }

    private int createRecognizeHandle(String license, String modelPath) {
        String sRecogModelPath = new StringBuilder(modelPath).append(File.separator).append("CWR_Config_1_1.xml").toString();
        int ret = FaceRecog.cwCreateRecogHandle(sRecogModelPath, license, 0);
        Log.i(TAG, "createRecognizeHandle: " + ret);
        if (ret >= FaceInterface.cw_errcode_t.CW_UNKNOWN_ERR) {
            EventBus.getDefault().post(new EventTips<String>("算法获取句柄时出错" + ret, TipMessageCode.MESSAGE_COLOR_FACERECOGNIZE_ERROR));
            return ret;
        }
        mFaceFeatureLength = FaceRecog.cwGetFeatureLength(ret);
        return ret;
    }
}
*/
