package com.open.face.camera;

/**
 * Created by Jowney on 2018/7/5.
 */

public class GrayCamera extends CameraBase {
    private static GrayCamera instance;


    public static GrayCamera getInstance() {
        if (instance == null) {
            synchronized (GrayCamera.class){
                if (instance == null){
                    instance = new GrayCamera();

                }
            }
        }
        return instance;
    }

}
