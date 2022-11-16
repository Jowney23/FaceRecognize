package com.tsl.app.activity

import android.content.Intent
import android.os.Bundle
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.jaeger.library.StatusBarUtil
import com.jowney.common.util.logger.L
import com.open.face.debug.DebugActivity
import com.tsl.app.R

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        XXPermissions.with(this) // 不适配 Android 11 可以这样写
            .permission(Permission.MANAGE_EXTERNAL_STORAGE, Permission.CAMERA)
            .request(OnPermissionCallback { permissions, all ->
                if (all) {
                    ToastUtils.show("获取所需权限成功")
                }
            })
    }

    override fun onResume() {
        super.onResume()
        L.v("我是启动页面哦！")
        startActivity(Intent(this, DebugActivity::class.java))
      //  HttpService.startService(this)
        finish()
    }

    override fun setStatusBar() {
        StatusBarUtil.setTransparent(this)
    }
}