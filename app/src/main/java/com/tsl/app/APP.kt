package com.tsl.app

import android.content.Context
import com.jowney.common.BaseApplication
import com.jowney.common.net.RetrofitMaster
import com.jowney.common.util.SoundPoolUtils
import com.jowney.player.player.AndroidMediaPlayerFactory
import com.jowney.player.player.VideoViewConfig
import com.jowney.player.player.VideoViewManager
import com.tsl.app.repository.net.api.ServerApi
import com.tsl.app.repository.net.api.ServerURL


class APP : BaseApplication() {
    companion object {
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this;
        //加载声音
        val list: ArrayList<Int> = arrayListOf(
            com.open.face.R.raw.loading,
            com.open.face.R.raw.ding,
            com.open.face.R.raw.registerok
        )
        SoundPoolUtils.getInstance().init(2)
        SoundPoolUtils.getInstance().loadSounds(list)

    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }
}