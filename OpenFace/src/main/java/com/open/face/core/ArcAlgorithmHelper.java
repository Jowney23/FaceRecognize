
package com.open.face.core;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.jowney.common.BaseApplication;
import com.jowney.common.util.FileTool;
import com.jowney.common.util.logger.L;
import com.open.face.camera.ColorCamera;
import com.open.face.model.EventTips;
import com.open.face.model.TipMessageCode;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;


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
    //该Map用来加载所有住户的特征值，key为特征值在本地保存的名称，value为特征值
    private static Map<String, byte[]> residentsFeatureGlobal = new HashMap<>();
    //应有的模版数量
    private int featureCountGlobal;
    //实际加载的模版数量
    private int loadedFeatureCountGlobal;
    //创建一个对象用于存储特征库中的特征，防止频繁创建对象
    private FaceFeature oldFaceFeature = new FaceFeature();
    private static final int MAX_DETECT_NUM = 2;
    /**
     * VIDEO模式人脸检测引擎，用于预览帧人脸追踪
     */
    private FaceEngine fDetectEngine;
    /**
     * 用于特征提取的引擎
     */
    private FaceEngine fFeatureEngine;
    /**
     * IMAGE模式活体检测引擎，用于预览帧人脸活体检测
     */
    private FaceEngine fLiveEngine;
    /**
     * 人脸比对
     * 该引擎同时也负责图片建模，因为图片人脸角度是0，该引擎设置的检测角度为0
     * 角度设置对模型比对无影响
     */
    private FaceEngine fCompareEngine;

    private int fDetectInitCode = -1;
    private int fFeatureInitCode = -1;
    private int fLiveInitCode = -1;

    public List<FaceInfo> faceInfoList = new ArrayList<>();
    private IErrorCallBack iErrorCallBack;

    public static ArcAlgorithmHelper getInstance() {
        if (mArcAlgorithmHelper == null) {
            mArcAlgorithmHelper = new ArcAlgorithmHelper();
        }
        return mArcAlgorithmHelper;
    }


    @Override
    public String initEngine(Context applicationContext) {
        //人脸追踪引擎
        fDetectEngine = new FaceEngine();
        fDetectInitCode = fDetectEngine.init(applicationContext, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_270_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_DETECT);
        //特征值提取引擎
        fFeatureEngine = new FaceEngine();
        fFeatureInitCode = fFeatureEngine.init(applicationContext, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_270_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION);
        //活体检测引擎
        fLiveEngine = new FaceEngine();
        fLiveInitCode = fLiveEngine.init(applicationContext, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_270_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_LIVENESS);


        fCompareEngine = new FaceEngine();
        int engineCode = fCompareEngine.init(applicationContext, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY, 16, 1, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);

        Log.i(TAG, "initEngine:  init: " + fDetectInitCode);

        if (fDetectInitCode != ErrorInfo.MOK) {
           /* String error = getString(R.string.specific_engine_init_failed, "ftEngine", ftInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);*/
        }
        if (fFeatureInitCode != ErrorInfo.MOK) {
           /* String error = getString(R.string.specific_engine_init_failed, "frEngine", frInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);*/
        }
        if (fLiveInitCode != ErrorInfo.MOK) {
           /* String error = getString(R.string.specific_engine_init_failed, "flEngine", flInitCode);
            Log.i(TAG, "initEngine: " + error);
            showToast(error);*/
        }
        return "";
    }

    @Override
    public void activateAsync(String appId, String sdkKey, IActivateCallBack activateCallBack) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int activateCode = FaceEngine.activeOnline(
                        BaseApplication.application,
                        appId,
                        sdkKey
                );
                activateCallBack.callback(activateCode);
            }
        });

    }

    /**
     * 用于注册视频中人脸
     */
    @Override
    public String enrollFaceFeatureNV21(byte[] srcVideoData, FaceInfo faceInfo) {
        String ret;
        FaceFeature faceFeature = new FaceFeature();
        try {
            String newTemplateID = UUID.randomUUID().toString().replace("-", "");
            //提取视频流中人脸特征值(^*_*^)
            long frStartTime = System.currentTimeMillis();
            int frCode = fFeatureEngine.extractFaceFeature(srcVideoData, ColorCamera.getInstance().getNV21Width(), ColorCamera.getInstance().getNV21Height(), FaceEngine.CP_PAF_NV21, faceInfo, faceFeature);
            if (frCode == ErrorInfo.MOK) {
                Log.i(TAG, "提取人脸特征耗时：" + (System.currentTimeMillis() - frStartTime) + "ms");

            } else {
                return null;
            }
            if (FileTool.writeByteArrayToFile(CacheHelper.TEMPLATE_DB_PATH_DIR + "/" + newTemplateID, faceFeature.getFeatureData())) {
                ret = newTemplateID;
            } else {
                return null;
            }

            //内存保存一份
            residentsFeatureGlobal.put(newTemplateID, faceFeature.getFeatureData());
            featureCountGlobal += 1;
            loadedFeatureCountGlobal += 1;
            EventBus.getDefault().post(new EventTips<>("应加载的模板数量：" + featureCountGlobal, TipMessageCode.MESSAGE_LOAD_TEMPLATE_START));
            EventBus.getDefault().post(new EventTips<>("实际加载的模板数量：" + loadedFeatureCountGlobal, TipMessageCode.MESSAGE_LOAD_TEMPLATE_END));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    /**
     * 用于注册照片人脸
     *
     * @return 是否注册成功
     */
    @Override
    public String enrollFaceFeatureBitmap(Bitmap bitmap) {
        synchronized (this) {
            featureCountGlobal += 1;
            //对齐函数中有判断bitmap宽高是否为4的倍数，如果是的话返回原bitmap，不是的话返回新的
            bitmap = ArcSoftImageUtil.getAlignedBitmap(bitmap, false);
            if (bitmap == null) return "";
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            byte[] bgr24 = ArcSoftImageUtil.createImageData(bitmap.getWidth(), bitmap.getHeight(), ArcSoftImageFormat.BGR24);
            int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
            if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) return "";

            String ret;
            String newTemplateID = UUID.randomUUID().toString().replace("-", "");
            long frStartTime = System.currentTimeMillis();
            //人脸检测
            List<FaceInfo> faceInfoList = new ArrayList<>();
            int code = fCompareEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
            if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                FaceFeature faceFeature = new FaceFeature();
                FaceInfo maxFaceInfo = faceInfoList.get(0);
                for (FaceInfo faceInfo : faceInfoList) {
                    if (faceInfo.getRect().width() > maxFaceInfo.getRect().width()) {
                        maxFaceInfo = faceInfo;
                    }
                }
                //特征提取
                int frCode = fCompareEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, maxFaceInfo, faceFeature);
                if (frCode == ErrorInfo.MOK) {
                    Log.i(TAG, "图片->提取人脸特征耗时：" + (System.currentTimeMillis() - frStartTime) + "ms");

                } else {
                    Log.e(TAG, "提取特征值失败" + frCode);
                    return null;
                }
                if (FileTool.writeByteArrayToFile(CacheHelper.TEMPLATE_DB_PATH_DIR + "/" + newTemplateID, faceFeature.getFeatureData())) {
                    ret = newTemplateID;
                } else {
                    return null;
                }

                //内存保存一份
                residentsFeatureGlobal.put(newTemplateID, faceFeature.getFeatureData());
                loadedFeatureCountGlobal += 1;
                L.v("应有的模板数量: " + featureCountGlobal + "      实际加载的模板数量:" + loadedFeatureCountGlobal);
               /* EventBus.getDefault().post(new EventTips<>("应加载的模板数量：" + featureCountGlobal, TipMessageCode.MESSAGE_LOAD_TEMPLATE_START));
                EventBus.getDefault().post(new EventTips<>("实际加载的模板数量：" + loadedFeatureCountGlobal, TipMessageCode.MESSAGE_LOAD_TEMPLATE_END));
*/
            } else {
                Log.e(TAG, "registerBgr24: no face detected, code is " + code);
                return "";
            }
            return ret;
        }

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

    public void loadAllFaceFeatureSync() {
        String[] mTemplateNames;
        //应有的模版数量
        featureCountGlobal = 0;
        //实际加载的模版数量
        loadedFeatureCountGlobal = 0;
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
        featureCountGlobal = mTemplateNames.length;
        EventBus.getDefault().post(new EventTips<>("应加载的模板数量：" + featureCountGlobal, TipMessageCode.MESSAGE_LOAD_TEMPLATE_START));
        for (String mTemplateName : mTemplateNames) {
            byte[] feature = new byte[FaceFeature.FEATURE_SIZE];
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
                Log.i("wxy", "本地加载run: 人脸模板长度：" + FaceFeature.FEATURE_SIZE);
                vnRet = FileTool.readByteArrayFromFile(CacheHelper.TEMPLATE_DB_PATH_DIR + "/" + mTemplateName, feature, FaceFeature.FEATURE_SIZE);
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
            loadedFeatureCountGlobal++;

        }
        // TODO: 2018/7/6  显示实际加载的模版数量loadedTemplateCount
        EventBus.getDefault().post(new EventTips<>("实际加载的模板数量：" + loadedFeatureCountGlobal, TipMessageCode.MESSAGE_LOAD_TEMPLATE_END));
    }

    @Override
    public void loadAllFaceFeatureAsync() {
        //加载所有模版数据
        Executors.newSingleThreadExecutor().execute(
                () -> {
                    String[] mTemplateNames;
                    //应有的模版数量
                    featureCountGlobal = 0;
                    //实际加载的模版数量
                    loadedFeatureCountGlobal = 0;
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
                    featureCountGlobal = mTemplateNames.length;
                    EventBus.getDefault().post(new EventTips<>("应加载的模板数量：" + featureCountGlobal, TipMessageCode.MESSAGE_LOAD_TEMPLATE_START));
                    for (String mTemplateName : mTemplateNames) {
                        byte[] feature = new byte[FaceFeature.FEATURE_SIZE];
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
                            Log.i("wxy", "本地加载run: 人脸模板长度：" + FaceFeature.FEATURE_SIZE);
                            vnRet = FileTool.readByteArrayFromFile(CacheHelper.TEMPLATE_DB_PATH_DIR + "/" + mTemplateName, feature, FaceFeature.FEATURE_SIZE);
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
                        loadedFeatureCountGlobal++;

                    }
                    // TODO: 2018/7/6  显示实际加载的模版数量loadedTemplateCount
                    EventBus.getDefault().post(new EventTips<>("实际加载的模板数量：" + loadedFeatureCountGlobal, TipMessageCode.MESSAGE_LOAD_TEMPLATE_END));
                }
        );

    }

    @Override
    public void deleteFaceFeature(String templateName) {

    }

    /**
     * 人脸检测建议在Camera回调函数中执行
     *
     * @param nv21
     * @return
     */
    @Override
    public FaceInfo detectMaxFace(byte[] nv21) {
        long ftStartTime = System.currentTimeMillis();
        //detectFaces内部有调用faceInfoList的clear
        int code = fDetectEngine.detectFaces(nv21, ColorCamera.getInstance().getNV21Width(), ColorCamera.getInstance().getNV21Height(), FaceEngine.CP_PAF_NV21, faceInfoList);
        if (code == ErrorInfo.MOK) {
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
        //  Log.i(TAG, "人脸检测耗时: " + (System.currentTimeMillis() - ftStartTime) + "ms");
        return maxFaceInfo;
    }

    /**
     * 1:N
     *
     * @return
     */
    @Override
    public String identifyFaceFeature(byte[] videoFrame, FaceInfo faceInfo) {
        //第一步生成特征值
        String residentFeatureIdTem = null;
        float similarityTem = 0;
        FaceFeature newFaceFeature = new FaceFeature();
        FaceSimilar faceSimilar = new FaceSimilar();
        // Log.v(TAG, "开始1：N");
        int frCode = fFeatureEngine.extractFaceFeature(videoFrame, ColorCamera.getInstance().getNV21Width(), ColorCamera.getInstance().getNV21Height(), FaceEngine.CP_PAF_NV21, faceInfo, newFaceFeature);
        if (frCode == ErrorInfo.MOK) {
            //第二步存储住户模版的residentsFeature中去比对
            for (Map.Entry<String, byte[]> entry : residentsFeatureGlobal.entrySet()) {
                oldFaceFeature.setFeatureData(entry.getValue());
                fCompareEngine.compareFaceFeature(newFaceFeature, oldFaceFeature, faceSimilar);
                // Log.v(TAG, "匹配中分数：" + faceSimilar.getScore());
                if (faceSimilar.getScore() > similarityTem) {
                    similarityTem = faceSimilar.getScore();
                    residentFeatureIdTem = entry.getKey();
                    //没有将库中数据全部匹配完，但是分数达到阈值了，直接返回
                    if (similarityTem >= CacheHelper.RECOGNIZE_THRESHOLD_VALUE){
                        Log.v(TAG, "不一定是最高分，但该分数大于阈值：" + similarityTem);
                        return residentFeatureIdTem;
                    }
                }
            }
            Log.v(TAG, "最高分：" + similarityTem);
            if (similarityTem >= CacheHelper.RECOGNIZE_THRESHOLD_VALUE) {
                return residentFeatureIdTem;
            } else {
                return null;
            }
        } else {
            return null;
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
        if (fDetectInitCode == ErrorInfo.MOK && fDetectEngine != null) {
            synchronized (fDetectEngine) {
                int ftUnInitCode = fDetectEngine.unInit();
                Log.i(TAG, "unInitEngine: " + ftUnInitCode);
            }
        }
        if (fFeatureInitCode == ErrorInfo.MOK && fFeatureEngine != null) {
            synchronized (fFeatureEngine) {
                int frUnInitCode = fFeatureEngine.unInit();
                Log.i(TAG, "unInitEngine: " + frUnInitCode);
            }
        }
        if (fLiveInitCode == ErrorInfo.MOK && fLiveEngine != null) {
            synchronized (fLiveEngine) {
                int flUnInitCode = fLiveEngine.unInit();
                Log.i(TAG, "unInitEngine: " + flUnInitCode);
            }
        }
    }

    @Override
    public void setErrorCallBack(IErrorCallBack iErrorCallBack) {
        this.iErrorCallBack = iErrorCallBack;
    }
}
