package com.open.face.model;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Creator: Jowney  (~._.~)
 * Date: 2018/4/27/23:35
 * Description:
 */

public class VideoFrameModel {
    private static byte[] videoFrameData;
    private static Lock lock = new ReentrantLock();

    public static synchronized void addVideoFrame(byte[] byteArray) {

        videoFrameData = byteArray;

    }

    public static synchronized byte[] getVideoFrame() {

        return videoFrameData;

    }





/*
    public synchronized Bitmap getVideoFrameBitmap(int w, int h) {
        if (videoFrameData == null){
            return null;
        }
     *//*    long startTime = System.currentTimeMillis();
       FaceRect[] faceRects = IflytekUtils.trackFace(videoFrameData,640,480,1,0);
        if (faceRects.length>0){
            Log.i("TTP", "getVideoFrameBitmap: "+faceRects[0].toString());
        }
        Log.i("TTP", "getVideoFrameBitmap: 检测人脸耗时"+(System.currentTimeMillis()-startTime));*//*
        return BitMapUtils.rawByteArray2RGBABitmap2(videoFrameData, w, h);
    }*/

}
