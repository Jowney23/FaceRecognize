package com.open.face.camera;

import android.hardware.Camera;


public interface CameraListener {
    /**
     * 当打开时执行
     * @param cameraId 相机ID
     * @param cameraClockwiseRotationValue 相机需要顺时针旋转角度
     * @param isMirror 是否镜手动像显示
     */
    void onCameraOpened(int nv21Width, int nv21Height, int canvasWidth,
                        int canvasHeight,int cameraId, int cameraClockwiseRotationValue, boolean isMirror);

    /**
     * 预览数据回调
     * @param data 预览数据
     * @param camera 相机实例
     */
    void onPreview(byte[] data);

    /**
     * 当相机关闭时执行
     */
    void onCameraClosed();

    /**
     * 当出现异常时执行
     * @param e 相机相关异常
     */
    void onCameraError(Exception e);

    /**
     * 属性变化时调用
     * @param cameraID  相机ID
     * @param cameraClockwiseRotationValue    相机旋转方向
     */
    void onCameraConfigurationChanged(int cameraID, int cameraClockwiseRotationValue);
}
