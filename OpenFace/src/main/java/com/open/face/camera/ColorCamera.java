package com.open.face.camera;

/**
 * Created by Jowney on 2018/7/5.
 * ColorCamera代表 CAMERA_FACING_BACK 后置摄像头
 */

public class ColorCamera extends CameraBase {

    private static ColorCamera instance;


    public static ColorCamera getInstance() {
        if (instance == null) {
            synchronized (ColorCamera.class){
                if (instance == null){
                    instance = new ColorCamera();

                }
            }
        }
        return instance;
    }


}
