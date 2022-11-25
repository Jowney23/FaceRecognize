
package com.open.face.core;


import android.content.Context;
import android.nfc.Tag;
import android.os.FileUtils;
import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.jowney.common.util.FileTool;
import com.open.face.model.EventTips;
import com.open.face.model.TipMessageCode;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Creator: Jowney  (~._.~)
 * Date: 2019/5/10/0:12
 * Description:
 * <p>
 * VIDEO模式人脸检测引擎，用于预览帧人脸追踪
 * <p>
 * 用于特征提取的引擎
 * <p>
 * IMAGE模式活体检测引擎，用于预览帧人脸活体检测
 * <p>
 * 1:N
 *
 * @return 1:1
 * @return
 */
//虹软算法助手
public class ArcAlgorithmHelper implements IAlgorithmHelper {
    private static final String TAG = "ArcAlgorithmHelper";
    private static ArcAlgorithmHelper mArcAlgorithmHelper = null;
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
    private static final int MAX_DETECT_NUM = 10;
    /**
     * VIDEO模式人脸检测引擎，用于预览帧人脸追踪
     */
    private FaceEngine ftEngine;
    /**
     * 用于特征提取的引擎
     */
    private FaceEngine frEngine;
    /**
     * IMAGE模式活体检测引擎，用于预览帧人脸活体检测
     */
    private FaceEngine flEngine;
    /**
     * 人脸比对
     */
    private FaceEngine fcEngine;

    private int ftInitCode = -1;
    private int frInitCode = -1;
    private int flInitCode = -1;

    public List<FaceInfo> faceInfoList = new ArrayList<>();

    public static ArcAlgorithmHelper getInstance() {
        if (mArcAlgorithmHelper == null) {
            mArcAlgorithmHelper = new ArcAlgorithmHelper();
        }
        return mArcAlgorithmHelper;
    }

    @Override
    public String initEngine(Context applicationContext) {
        ftEngine = new FaceEngine();
        ftInitCode = ftEngine.init(applicationContext, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_270_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_DETECT);

        frEngine = new FaceEngine();
        frInitCode = frEngine.init(applicationContext, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION);

        flEngine = new FaceEngine();
        flInitCode = flEngine.init(applicationContext, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_LIVENESS);


        fcEngine = new FaceEngine();
        int engineCode = fcEngine.init(applicationContext, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY, 16, 1, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);

        Log.i(TAG, "initEngine:  init: " + ftInitCode);

        if (ftInitCode != ErrorInfo.MOK) {
           /* String error = getString(R.string.specific_engine_init_failed, "ftEngine", ftInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);*/
        }
        if (frInitCode != ErrorInfo.MOK) {
           /* String error = getString(R.string.specific_engine_init_failed, "frEngine", frInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);*/
        }
        if (flInitCode != ErrorInfo.MOK) {
           /* String error = getString(R.string.specific_engine_init_failed, "flEngine", flInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);*/
        }
        return "";
    }


    @Override
    public String enrollFaceFeature(byte[] srcVideoData, FaceInfo faceInfo) {
        String ret = null;
        FaceFeature faceFeature = new FaceFeature();
        try {
            String newTemplateID = UUID.randomUUID().toString().replace("-", "");
            //提取视频流中人脸特征值(^*_*^)
            long frStartTime = System.currentTimeMillis();
            int frCode = frEngine.extractFaceFeature(srcVideoData, 640, 480, FaceEngine.CP_PAF_NV21, faceInfo, faceFeature);
            if (frCode == ErrorInfo.MOK) {
                Log.i(TAG, "提取人脸特征耗时：" + (System.currentTimeMillis() - frStartTime) + "ms");

            } else {
                return null;
            }
            if (FileTool.writeByteArrayToFile(CacheHelper.TEMPLATE_DB_PATH_DIR + "/" + newTemplateID, faceFeature.getFeatureData())) {
                ret =  newTemplateID;
            } else {
                return null;
            }

            //内存保存一份
            residentsFeatureGlobal.put(newTemplateID, faceFeature.getFeatureData());
            templateCountGlobal += 1;
            loadedTemplateCountGlobal += 1;
            EventBus.getDefault().post(new EventTips<>("应加载的模板数量：" + templateCountGlobal, TipMessageCode.MESSAGE_LOADT_TEMPLATE_START));
            EventBus.getDefault().post(new EventTips<>("实际加载的模板数量：" + loadedTemplateCountGlobal, TipMessageCode.MESSAGE_LOADT_TEMPLATE_END));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return  ret;
    }

    @Override
    public void updateFaceFeature(byte[] srcVideoData, String templateID, float faceQualityScore) {
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

        return ret;*/
    }

    @Override
    public void loadAllFaceFeature() {
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
                File mDir = new File(CacheHelper.INIT_DATA_PATH_DIR);//YZ文件夹
                if (!mDir.isDirectory() && !mDir.mkdir()) return;


                File mTemplateDir = new File(CacheHelper.TEMPLATE_DB_PATH_DIR);//模版文件夹
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

                    if (version != CacheHelper.TEMPLATE_VERSION) {
                        //TODO: 2018/7/6 提示模版需要升级
                        break;
                    }
                    //加载模板
                    try {
                        Log.i("wxy", "本地加载run: 人脸模板长度：" + mFaceFeatureLength);
                        vnRet = FileTool.readByteArrayFromFile(CacheHelper.TEMPLATE_DB_PATH_DIR + "/" + mTemplateName, feature, mFaceFeatureLength);
                        if (vnRet) {
                            residentsFeatureGlobal.put(mTemplateName, feature);
                        } else {
                            //  LogUtils.v("卧槽！模板号为：" + mTemplateName + " 的老兄模板加载失败了");
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: 2018/7/6 如果有人的模板为空应该提示
                        Log.v(TAG, "卧槽！模板号为：" + mTemplateName + "的老兄加载时发生了异常");
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
    public void deleteFaceFeature(String templateName) {

    }

    /**
     * 人脸检测建议在Camera回调函数中执行
     * @param nv21
     * @return
     */
    @Override
    public FaceInfo detectFace(byte[] nv21) {
        long ftStartTime = System.currentTimeMillis();
        //detectFaces内部有调用faceInfoList的clear
        int code = ftEngine.detectFaces(nv21, 640, 480, FaceEngine.CP_PAF_NV21, faceInfoList);
        if (code == ErrorInfo.MOK) {
         //   Log.i(TAG, "人脸检测耗时: " + (System.currentTimeMillis() - ftStartTime) + "ms");

        } else {
         //   Log.v(TAG,"错误码："+code);
            return null;
        }
        //只保留最大人脸信息
        if (faceInfoList == null || faceInfoList.size() < 1) {
            return null;
        }
        FaceInfo maxFaceInfo = faceInfoList.get(0);
        for (FaceInfo faceInfo : faceInfoList) {
            if (faceInfo.getRect().width() > maxFaceInfo.getRect().width()) {
                maxFaceInfo = faceInfo;
            }
        }
        return maxFaceInfo;
    }


    /**
     * 1:N
     *
     * @return
     */
    @Override
    public boolean identifyFaceFeature(byte[] videoFrame,FaceInfo faceInfo) {
        float similarityTem = 0;
        //第一步生成特征值
        FaceFeature tempFaceFeature = new FaceFeature();
        FaceSimilar faceSimilar = new FaceSimilar();
        Log.v(TAG, "开始1：N");
        int frCode = frEngine.extractFaceFeature(videoFrame, 640, 480, FaceEngine.CP_PAF_NV21, faceInfo, tempFaceFeature);
        if (frCode == ErrorInfo.MOK) {

        } else {

        }
        //第二步存储住户模版的residentsFeature中去比对
        Map.Entry<String, byte[]> entry;
        Iterator<Map.Entry<String, byte[]>> residentIterator = residentsFeatureGlobal.entrySet().iterator();
        while (residentIterator.hasNext()) {
            entry = residentIterator.next();
            fcEngine.compareFaceFeature(tempFaceFeature, new FaceFeature(entry.getValue()), faceSimilar);
            //   FaceRecog.cwComputeMatchScore(mRecognizeHandle, feature, entry.getValue(), 1, similarity);
            Log.v(TAG, "比对份数：" + faceSimilar.getScore());
            if (faceSimilar.getScore() > similarityTem) {
                similarityTem = faceSimilar.getScore();
                residentTmplateId[0] = entry.getKey();
            }
        }

        if (similarityTem >= CacheHelper.RECOGNIZE_THRESHOLD_VALUE) {
            Log.v(TAG, "比对份数：" + similarity[0]);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 1:1
     *
     * @param idCardPicture
     * @param videoPicture
     * @return
     */
    @Override
    public Double identifyFaceFeature(byte[] idCardPicture, byte[] videoPicture) {
        return null;
    }

    @Override
    public void unInitEngine() {
        if (ftInitCode == ErrorInfo.MOK && ftEngine != null) {
            synchronized (ftEngine) {
                int ftUnInitCode = ftEngine.unInit();
                Log.i(TAG, "unInitEngine: " + ftUnInitCode);
            }
        }
        if (frInitCode == ErrorInfo.MOK && frEngine != null) {
            synchronized (frEngine) {
                int frUnInitCode = frEngine.unInit();
                Log.i(TAG, "unInitEngine: " + frUnInitCode);
            }
        }
        if (flInitCode == ErrorInfo.MOK && flEngine != null) {
            synchronized (flEngine) {
                int flUnInitCode = flEngine.unInit();
                Log.i(TAG, "unInitEngine: " + flUnInitCode);
            }
        }
    }

    @Override
    public String switchErrorReason(int flag) {
        return null;
    }

}
