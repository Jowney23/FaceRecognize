package com.tsl.app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.arcsoft.face.ActiveFileInfo
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.arcsoft.face.enums.RuntimeABI
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.jaeger.library.StatusBarUtil
import com.jowney.common.util.logger.L
import com.open.face.debug.DebugActivity
import com.tsl.app.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        XXPermissions.with(this) // 不适配 Android 11 可以这样写
            .permission(Permission.MANAGE_EXTERNAL_STORAGE, Permission.CAMERA,Permission.READ_PHONE_STATE)
            .request(OnPermissionCallback { permissions, all ->
                if (all) {
                    ToastUtils.show("获取所需权限成功")
                    Observable.create<Int> { emitter ->
                        val runtimeABI: RuntimeABI = FaceEngine.getRuntimeABI()

                        val start = System.currentTimeMillis()
                        val activeCode: Int = FaceEngine.activeOnline(
                            this,
                           "6uYu9gopNWGAmeo2DVgNfb6LfEqeKN44ABPQLZqirhqK",
                           "Bs5PF3ry6nSk99rPVbFQfm5MdXQXjX9nQ1dWuZmNHwvU"
                        )

                        emitter.onNext(activeCode)
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<Int> {
                            override fun onSubscribe(d: Disposable) {}
                            override fun onNext(activeCode: Int) {
                                if (activeCode == ErrorInfo.MOK) {
                                   L.v("11111111111")
                                } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                                    L.v("2222222222222222222")
                                } else {
                                    L.v("33333333333333333333333         "+activeCode)
                                }
                              this@SplashActivity.startActivity(Intent(this@SplashActivity, DebugActivity::class.java))
                                //  HttpService.startService(this)
                                finish()
                                val activeFileInfo = ActiveFileInfo()
                                val res: Int = FaceEngine.getActiveFileInfo(
                                    this@SplashActivity,
                                    activeFileInfo
                                )
                                if (res == ErrorInfo.MOK) {

                                }

                            }

                            override fun onError(e: Throwable) {
                               L.v("gggggggggggggggggggggggggg")
                            }

                            override fun onComplete() {}
                        })


                }
            })
    }

    override fun onResume() {
        super.onResume()
        L.v("我是启动页面哦！")

    }

    override fun setStatusBar() {
        StatusBarUtil.setTransparent(this)
    }
}