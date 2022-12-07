package com.tsl.app.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    var isFirstPress = true
    var mLastPressTime: Long = 0 //上次按键时间
    var mGoBackThreshold: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        //保持亮屏
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //隐藏状态栏
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY //(修改这个选项，可以设置不同模式)
                    //使用下面三个参数，可以使内容显示在system bar的下面，防止system bar显示或
                    //隐藏时，Activity的大小被resize。
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // 隐藏导航栏和状态栏
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    abstract fun setStatusBar();

    override fun onBackPressed() {
        super.onBackPressed()
        //如果当前activity是根activity则阻拦返回
       /* if (isTaskRoot) {
            super.onBackPressed()
            ToastUtils.show("根Activity")
        }
        if (isFirstPress) {
            isFirstPress = false
            mLastPressTime = System.currentTimeMillis()
        } else {
            isFirstPress = true
            if ((System.currentTimeMillis() - mLastPressTime) < mGoBackThreshold) super.onBackPressed()
            else ToastUtils.show("WO! 快速点击两次退出")
        }*/
    }
}