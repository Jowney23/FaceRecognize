package com.open.face.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.arcsoft.face.FaceInfo;

/**
 * Creator: Jowney  (~._.~)
 * Date: 2019/5/10/0:10
 * Description:算法助手 将算法常规用法接口化
 */
public interface IAlgorithmHelper {

    void activateAsync(String appId, String sdkKey, IActivateCallBack callBack);

    /**
     * 初始化算法
     *
     * @return 返回初始化结果
     */
    String initEngine(Context applicationContext);


    /**
     * 注册模板接口
     *
     * @param srcVideoData
     * @return
     */
    String enrollFaceFeatureNV21(byte[] srcVideoData, FaceInfo faceInfo);

    String enrollFaceFeatureBitmap(Bitmap bitmap);

    /**
     * 更新本地模板
     *
     * @param srcVideoData
     */
    void updateFaceFeature(byte[] srcVideoData, String templateID, float faceQualityScore);

    /**
     * 加载本地人脸模板，1：N时使用
     */
    void loadAllFaceFeatureAsync();

    /**
     * 删除模板
     *
     * @param templateName
     */
    void deleteFaceFeature(String templateName);

    /**
     * @param videoPicture
     * @return 人脸个数
     */
    FaceInfo detectMaxFace(byte[] videoPicture);

    /**
     * 1：N
     *
     * @return
     */

    String identifyFaceFeature(byte[] videoFrame, FaceInfo faceInfo);

    /**
     * 1:1
     *
     * @param idCardPicture
     * @param videoPicture
     * @return
     */
    Double identifyFaceFeature(byte[] idCardPicture, byte[] videoPicture);

    /**
     * 销毁算法
     */
    void unInitEngine();

    /**
     * 错误信息回调
     *
     * @return
     */
    void setErrorCallBack(IErrorCallBack iErrorCallBack);
}

interface IActivateCallBack {
    void callback(int code);
}

interface IErrorCallBack {
    void algorithmHelperError(String message);
}