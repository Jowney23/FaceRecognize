package com.open.face.core;

import android.content.Context;
import android.os.Environment;

import com.arcsoft.face.FaceInfo;

/**
 * Creator: Jowney  (~._.~)
 * Date: 2019/5/10/0:10
 * Description:算法助手 将算法常规用法接口化
 */
public interface IAlgorithmHelper {


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
    String enrollFaceFeature(byte[] srcVideoData, FaceInfo faceInfo);

    /**
     * 更新本地模板
     *
     * @param srcVideoData
     */
    void updateFaceFeature(byte[] srcVideoData, String templateID, float faceQualityScore);

    /**
     * 加载本地人脸模板，1：N时使用
     */
    void loadAllFaceFeature();

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
    FaceInfo detectFace(byte[] videoPicture);

    /**
     * 1：N
     *
     * @return
     */

    boolean identifyFaceFeature(byte[] videoFrame,FaceInfo faceInfo);

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
     * 错误信息
     *
     * @param flag
     * @return
     */
    String switchErrorReason(int flag);
}
