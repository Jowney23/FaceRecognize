package com.open.face.model;


import com.arcsoft.face.FaceInfo;
import com.jowney.common.util.logger.L;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Creator: Jowney  (~._.~)
 * Date: 2018/4/27/23:35
 * Description:
 */

public class VideoFrameModel {

    private static ConcurrentLinkedQueue<VideoFrame> mVideoFrameQueue = new ConcurrentLinkedQueue<>();

    public static void addVideoFrame(byte[] byteArray, FaceInfo faceInfo) {
        if (byteArray != null && faceInfo != null && mVideoFrameQueue.isEmpty()) {
            mVideoFrameQueue.add(new VideoFrame(byteArray.clone(), faceInfo.clone()));
        }

    }

    public static VideoFrame getVideoFrame() {
        return mVideoFrameQueue.poll();

    }

    public static class VideoFrame {
        private byte[] mVideoFrameData;
        private FaceInfo mFaceInfo;

        public VideoFrame(byte[] mVideoFrameData, FaceInfo mFaceInfo) {
            this.mVideoFrameData = mVideoFrameData;
            this.mFaceInfo = mFaceInfo;
        }

        public byte[] getVideoFrameData() {
            return mVideoFrameData;
        }

        public FaceInfo getFaceInfo() {
            return mFaceInfo;
        }
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
