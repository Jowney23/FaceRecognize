package com.open.face.core;

import android.os.Environment;

public class CacheHelper {
    //注意路径结尾统一都不要“/”,命名规则文件夹以DIR结尾，文件以file结尾
    public static String INIT_DATA_PATH_DIR = Environment.getExternalStorageDirectory() + "/LDFace";
    public static    String TEMPLATE_DB_PATH_DIR = INIT_DATA_PATH_DIR + "/Template";
    public static    String LICENSE_DIR = INIT_DATA_PATH_DIR + "/License";
    public static  String ENROLL_PHOTO_DB_PATH_DIR = INIT_DATA_PATH_DIR + "/ResidentInformation/TemplatePhoto";
    public static   String IDCARD_PHOTO_DB_PATH_DIR = INIT_DATA_PATH_DIR + "/ResidentInformation/IdCardPhoto";
    public static  int TEMPLATE_VERSION = 0;
    public static  float RECOGNIZE_THRESHOLD_VALUE = 0.85f; //人脸比对阈值
    public static   float ENROLL_THRESHOLD_VALUE = 0.85f; //人脸建模阈值
    public static   float FACE_QULITY_UPDATE_THRESHOLD_VALUE = 0.9f;//图片质量达到0.9 则更新模板
}
