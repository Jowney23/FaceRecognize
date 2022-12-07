package com.open.face.core;

import com.arcsoft.face.FaceInfo;

/**
 * Created by Jowney on 2018/7/8.
 * 人脸识别业务接口
 */

public interface IFaceBusiness {
    final static int MATCH_TO_N_MODE = 0x01;//1：N模式
    final static int MATCH_TO_ONE_MODE = 0x10;//1:1模式
    final static int ENROLL_MODE = 0x11;//人脸建模模式
    final static int JUST_FACE_DETECT = 0x100;//只检测人脸
    void matchToN(byte[] videoFrame, FaceInfo faceInfo);

    void matchToOne();

    void enrollVideoFaceInfo(byte[] videoFrame , FaceInfo faceInfo);
}
