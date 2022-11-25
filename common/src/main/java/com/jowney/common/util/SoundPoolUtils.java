package com.jowney.common.util;


import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.jowney.common.BaseApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by Jowney on 2018/7/8.
 */

public class SoundPoolUtils {
    private static SoundPoolUtils soundPlayUtils;
    private Map<String, Integer> resourceMap = new HashMap<>();
    private SoundPool mSoundPool;


    public static SoundPoolUtils getInstance() {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPoolUtils();
        }
        return soundPlayUtils;
    }

    public void init(int maxSoundCount) {
        //AudioAttributes是一个封装音频各种属性的方法
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        SoundPool.Builder builder = new SoundPool.Builder()
                .setMaxStreams(maxSoundCount)
                .setAudioAttributes(attrBuilder.build());//加载一个AudioAttributes

        mSoundPool = builder.build();


    }

    public void loadSounds(ArrayList<Integer> list) {

        for (int resourceID : list) {
            int id = mSoundPool.load(BaseApplication.application, resourceID, 1);// 1
            resourceMap.put(String.valueOf(resourceID), id);
        }
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public void play(final int soundID) {
        int id = resourceMap.get(String.valueOf(soundID));
        mSoundPool.play(id, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void  releasSoundPool(){
        mSoundPool.release();
    }
}
