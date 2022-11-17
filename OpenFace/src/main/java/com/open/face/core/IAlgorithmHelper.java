package com.open.face.core;

import android.os.Environment;

/**
 * Creator: Jowney  (~._.~)
 * Date: 2019/5/10/0:10
 * Description:算法助手 将算法常规用法接口化
 */
public interface IAlgorithmHelper {
    //注意路径结尾统一都不要“/”,命名规则文件夹以DIR结尾，文件以file结尾
    String INIT_DATA_PATH_DIR = Environment.getExternalStorageDirectory() + "/LDFace";
    String TEMPLATE_DB_PATH_DIR = INIT_DATA_PATH_DIR + "/Template";
    String LICENSE_DIR = INIT_DATA_PATH_DIR+"/License";
    String ENROLL_PHOTO_DB_PATH_DIR = INIT_DATA_PATH_DIR + "/ResidentInformation/TemplatePhoto";
    String IDCARD_PHOTO_DB_PATH_DIR = INIT_DATA_PATH_DIR + "/ResidentInformation/IdCardPhoto";
    int TEMPLATE_VERSION = 0;
    float RECOGNIZE_THRESHOLD_VALUE = 0.85f; //人脸比对阈值
    float ENROLL_THRESHOLD_VALUE = 0.85f; //人脸建模阈值
    float FACE_QULITY_UPDATE_THRESHOLD_VALUE = 0.9f;//图片质量达到0.9 则更新模板

    /**
     * 初始化算法
     *
     * @return 返回初始化结果
     */
    String init();

    /**
     * 生成模板
     *
     * @param srcVideoData
     * @return
     */
    String enrollTemplate(byte[] srcVideoData);

    /**
     * 更新本地模板
     *
     * @param srcVideoData
     */
    void updateTemplate(byte[] srcVideoData, String templateID, float faceQualityScore);

    /**
     * 加载本地人脸模板，1：N时使用
     */
    void loadAllTemplate();

    /**
     * 删除模板
     *
     * @param templateName
     */
    void deleteTemplate(String templateName);

    /**
     * @param videoPicture
     * @return 人脸个数
     */
    int detectFace(byte[] videoPicture);

    /**
     * 1：N
     *
     * @return
     */

    boolean identifyFace();

    /**
     * 1:1
     *
     * @param idCardPicture
     * @param videoPicture
     * @return
     */
    Double identifyFace(byte[] idCardPicture, byte[] videoPicture);

    /**
     * 销毁算法
     */
    void destroy();

    /**
     * 错误信息
     *
     * @param flag
     * @return
     */
    String switchErrorReason(int flag);
}
