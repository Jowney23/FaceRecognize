package com.open.face.view;

import android.graphics.Rect;
import android.hardware.Camera;

/**
 * 绘制人脸框帮助类
 */
public class DrawFaceCoveringHelper {
    private static int nv21Width, nv21wHeight, canvasWidth, canvasHeight, cameraDisplayOrientation, cameraId;
    private static boolean isMirror;
    private static boolean mirrorHorizontal = false, mirrorVertical = false;

    /**
     * 创建一个绘制辅助类对象，并且设置绘制相关的参数
     *
     * @param nv21Width             预览宽度
     * @param nv21Height            预览高度
     * @param canvasWidth              绘制控件的宽度
     * @param canvasHeight             绘制控件的高度
     * @param cameraDisplayOrientation 旋转角度    mCamera.setDisplayOrientation(cameraDisplayOrientation);
     * @param cameraId                 相机ID
     * @param isMirror                 是否水平镜像显示（若相机是镜像显示的，设为true，用于纠正）
     * @param mirrorHorizontal         为兼容部分设备使用，水平再次镜像
     * @param mirrorVertical           为兼容部分设备使用，垂直再次镜像
     */
    public static void init(int nv21Width, int nv21Height, int canvasWidth,
                            int canvasHeight, int cameraDisplayOrientation, int cameraId,
                            boolean isMirror, boolean mirrorHorizontal, boolean mirrorVertical) {
        DrawFaceCoveringHelper.nv21Width = nv21Width;
        DrawFaceCoveringHelper.nv21wHeight = nv21Height;
        DrawFaceCoveringHelper.canvasWidth = canvasWidth;
        DrawFaceCoveringHelper.canvasHeight = canvasHeight;
        DrawFaceCoveringHelper.cameraDisplayOrientation = cameraDisplayOrientation;
        DrawFaceCoveringHelper.cameraId = cameraId;
        DrawFaceCoveringHelper.isMirror = isMirror;
        DrawFaceCoveringHelper.mirrorHorizontal = mirrorHorizontal;
        DrawFaceCoveringHelper.mirrorVertical = mirrorVertical;
    }


    /**
     * 调整人脸框用来绘制
     *
     * @param ftRect FT人脸框
     * @return 调整后的需要被绘制到View上的rect
     */
    public static Rect adjustRect(Rect ftRect) {
        int previewWidth = DrawFaceCoveringHelper.nv21Width;
        int previewHeight = DrawFaceCoveringHelper.nv21wHeight;
        int canvasWidth = DrawFaceCoveringHelper.canvasWidth;
        int canvasHeight = DrawFaceCoveringHelper.canvasHeight;
        int cameraDisplayOrientation = DrawFaceCoveringHelper.cameraDisplayOrientation;
        int cameraId = DrawFaceCoveringHelper.cameraId;
        boolean isMirror = DrawFaceCoveringHelper.isMirror;
        boolean mirrorHorizontal = DrawFaceCoveringHelper.mirrorHorizontal;
        boolean mirrorVertical = DrawFaceCoveringHelper.mirrorVertical;

        if (ftRect == null) {
            return null;
        }

        Rect rect = new Rect(ftRect);
        float horizontalRatio;
        float verticalRatio;
        if (cameraDisplayOrientation % 180 == 0) {
            horizontalRatio = (float) canvasWidth / (float) previewWidth;
            verticalRatio = (float) canvasHeight / (float) previewHeight;
        } else {
            horizontalRatio = (float) canvasHeight / (float) previewWidth;
            verticalRatio = (float) canvasWidth / (float) previewHeight;
        }
        rect.left *= horizontalRatio;
        rect.right *= horizontalRatio;
        rect.top *= verticalRatio;
        rect.bottom *= verticalRatio;

        Rect newRect = new Rect();
        switch (cameraDisplayOrientation) {
            case 0:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.left = canvasWidth - rect.right;
                    newRect.right = canvasWidth - rect.left;
                } else {
                    newRect.left = rect.left;
                    newRect.right = rect.right;
                }
                newRect.top = rect.top;
                newRect.bottom = rect.bottom;
                break;
            case 90:
                newRect.right = canvasWidth - rect.top;
                newRect.left = canvasWidth - rect.bottom;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.top = canvasHeight - rect.right;
                    newRect.bottom = canvasHeight - rect.left;
                } else {
                    newRect.top = rect.left;
                    newRect.bottom = rect.right;
                }
                break;
            case 180:
                newRect.top = canvasHeight - rect.bottom;
                newRect.bottom = canvasHeight - rect.top;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.left = rect.left;
                    newRect.right = rect.right;
                } else {
                    newRect.left = canvasWidth - rect.right;
                    newRect.right = canvasWidth - rect.left;
                }
                break;
            case 270:
                newRect.left = rect.top;
                newRect.right = rect.bottom;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.top = rect.left;
                    newRect.bottom = rect.right;
                } else {
                    newRect.top = canvasHeight - rect.right;
                    newRect.bottom = canvasHeight - rect.left;
                }
                break;
            default:
                break;
        }

        /**
         * isMirror mirrorHorizontal finalIsMirrorHorizontal
         * true         true                false
         * false        false               false
         * true         false               true
         * false        true                true
         *
         * XOR
         */
        if (isMirror ^ mirrorHorizontal) {
            int left = newRect.left;
            int right = newRect.right;
            newRect.left = canvasWidth - right;
            newRect.right = canvasWidth - left;
        }
        if (mirrorVertical) {
            int top = newRect.top;
            int bottom = newRect.bottom;
            newRect.top = canvasHeight - bottom;
            newRect.bottom = canvasHeight - top;
        }
        return newRect;
    }


}
