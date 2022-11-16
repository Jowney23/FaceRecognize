package com.open.face.model;


import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Creator: Jowney  (~._.~)
 * Date: 2018/4/27/23:35
 * Description:
 */

public class VideoFrameModel {
    private static ConcurrentLinkedQueue<byte[]> videoFrameQueue = new ConcurrentLinkedQueue<>();

    public static void addVideoFrame(byte[] byteArray) {
        if (videoFrameQueue.isEmpty()) videoFrameQueue.add(byteArray);
    }

    public static byte[] pollVideoFrame() {

        return videoFrameQueue.poll();
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
