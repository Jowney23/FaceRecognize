package com.tsl.app.activity.set

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.jaeger.library.StatusBarUtil
import com.tsl.app.R
import com.tsl.app.activity.BaseActivity


class SettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)
        var model: SettingActivityViewModel =
            ViewModelProvider(this).get(SettingActivityViewModel::class.java)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

    }

    override fun setStatusBar() {
        StatusBarUtil.setTransparent(this)
    }
}