package com.tsl.app.activity

import android.animation.Animator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.google.zxing.integration.android.IntentIntegrator
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.jaeger.library.StatusBarUtil
import com.jowney.common.net.RetrofitMaster
import com.jowney.common.util.SoundPoolUtils
import com.jowney.common.util.SpTool
import com.jowney.common.util.logger.L
import com.jowney.player.player.AndroidMediaPlayerFactory
import com.jowney.player.player.VideoViewConfig
import com.jowney.player.player.VideoViewManager
import com.open.face.core.ArcAlgorithmHelper
import com.tsl.app.BuildConfig
import com.tsl.app.R
import com.tsl.app.activity.main.MainActivity
import com.tsl.app.bean.GlobalConstant
import com.tsl.app.repository.net.api.ServerApi
import com.tsl.app.repository.net.api.ServerURL
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope

class SplashActivity : BaseActivity() {
    lateinit var mLottieAnimationView: LottieAnimationView
    var mActiveCode: Int = -1
    var mStreamId: Int = -1
    var mPermissionIsOk = false
    var mAppId: String = ""
    var mSdkKey: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mAppId = SpTool.getParam(this, GlobalConstant.SP_KEY_APP_ID, "") as String
        mSdkKey = SpTool.getParam(this, GlobalConstant.SP_KEY_SDK_KEY, "") as String
        mLottieAnimationView = findViewById(R.id.as_lottie_egg)
        mLottieAnimationView.repeatMode = LottieDrawable.RESTART
        mLottieAnimationView.repeatCount = -1
        mLottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
                if (!mPermissionIsOk) {
                    ToastUtils.show("请重新授予软件权限")
                    return
                }
                if (mActiveCode == ErrorInfo.MOK || mActiveCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(
                            Intent(this@SplashActivity, MainActivity::class.java),
                            ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity)
                                .toBundle()
                        )
                    }
                } else {
                    IntentIntegrator(this@SplashActivity).initiateScan();
                }
            }
        })

        XXPermissions.with(this) // 不适配 Android 11 可以这样写
            .permission(
                Permission.MANAGE_EXTERNAL_STORAGE,
                Permission.CAMERA,
                Permission.READ_PHONE_STATE
            )
            .request(OnPermissionCallback { permissions, all ->
                if (all) {
                    //  ToastUtils.show("获取所需权限成功")
                    mPermissionIsOk = true
                }
            })
    }

    override fun onResume() {
        super.onResume()
        initGlobal()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLottieAnimationView.removeAllAnimatorListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (IntentIntegrator.REQUEST_CODE == requestCode) {
            val result =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "扫描结果为空", Toast.LENGTH_LONG).show()
                } else {
                    val qrContent: String = result.contents
                    if (qrContent == null || qrContent == "") {
                        ToastUtils.show("请确认二维码是否正确")
                        return
                    }
                    val qrContentSplit: List<String> = qrContent.split("&&")
                    if (qrContentSplit == null || qrContentSplit.size != 2)
                        return
                    mAppId = qrContentSplit[0]
                    mSdkKey = qrContentSplit[1]
                    SpTool.setParam(this, GlobalConstant.SP_KEY_APP_ID, mAppId)
                    SpTool.setParam(this, GlobalConstant.SP_KEY_SDK_KEY, mSdkKey)
                    L.v(qrContent)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            mStreamId = SoundPoolUtils.getInstance().play(com.open.face.R.raw.loading)
            mLottieAnimationView.playAnimation()
        } else {
            SoundPoolUtils.getInstance().stop(mStreamId)
            mLottieAnimationView.pauseAnimation()
        }


    }

    override fun setStatusBar() {
        StatusBarUtil.setTransparent(this)
    }

    private fun initGlobal() {
        Observable.create<Int> { emitter ->
            //算法激活
            val activeCode: Int = FaceEngine.activeOnline(
                this,
                mAppId,
                mSdkKey
            )
            //引擎初始化
            ArcAlgorithmHelper.getInstance().initEngine(this)
            //加载人脸特征值，有本地文件操作，需要先授予读取权限
            ArcAlgorithmHelper.getInstance().loadAllFaceFeatureSync()
            //初始化全局控件
            //网络模块儿初始化
            RetrofitMaster.getInstance().init(ServerURL.URL_BASE, ServerApi::class.java, null)
            //视频模块儿
            //播放器配置，注意：此为全局配置，按需开启
            VideoViewManager.setConfig(
                VideoViewConfig.newBuilder()
                    .setLogEnabled(BuildConfig.DEBUG) //调试的时候请打开日志，方便排错
                    .setPlayerFactory(AndroidMediaPlayerFactory.create()) //                .setPlayerFactory(AndroidMediaPlayerFactory.create()) //不推荐使用，兼容性较差
                    // 设置自己的渲染view，内部默认TextureView实现
                    //                .setRenderViewFactory(SurfaceRenderViewFactory.create())
                    // 根据手机重力感应自动切换横竖屏，默认false
                    //                .setEnableOrientation(true)
                    // 监听系统中其他播放器是否获取音频焦点，实现不与其他播放器同时播放的效果，默认true
                    //                .setEnableAudioFocus(false)
                    // 视频画面缩放模式，默认按视频宽高比居中显示在VideoView中
                    //                .setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
                    // 适配刘海屏，默认true
                    //                .setAdaptCutout(false)
                    // 移动网络下提示用户会产生流量费用，默认不提示，
                    // 如果要提示则设置成false并在控制器中监听STATE_START_ABORT状态，实现相关界面，具体可以参考PrepareView的实现
                    //                .setPlayOnMobileNetwork(false)
                    // 进度管理器，继承ProgressManager，实现自己的管理逻辑
                    //                .setProgressManager(new ProgressManagerImpl())
                    .build()
            )
            emitter.onNext(activeCode)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(activeCode: Int) {
                    mActiveCode = activeCode
                }

                override fun onError(e: Throwable) {
                    L.v(e.message?:"err")
                }

                override fun onComplete() {}
            })

    }
}