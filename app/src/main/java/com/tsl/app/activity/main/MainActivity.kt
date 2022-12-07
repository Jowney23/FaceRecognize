package com.tsl.app.activity.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.jaeger.library.StatusBarUtil
import com.lxj.xpopup.XPopup
import com.open.face.camera.ColorCamera
import com.open.face.core.FaceRecognizeThread
import com.open.face.core.IFaceBusiness
import com.open.face.model.EventTips
import com.open.face.model.TipMessageCode
import com.open.face.view.ColorPreviewTextureView
import com.open.face.view.FaceCoveringCircleView
import com.tsl.app.R
import com.tsl.app.activity.BaseActivity
import com.tsl.app.activity.set.SettingActivity
import kotlinx.android.synthetic.main.activity_main_camera.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity() {
    companion object {
        var TAG = "JLogin"
    }

    private lateinit var mCameraPreview: ColorPreviewTextureView
    private lateinit var mFaceDetectView: FaceCoveringCircleView
    private lateinit var mFaceBusinessThread: FaceRecognizeThread
    private lateinit var mLottieAnimationView: LottieAnimationView
    private lateinit var mGestureDetector: GestureDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_camera)
        EventBus.getDefault().register(this)
        mGestureDetector = GestureDetector(this, object :MainActivityGestureListener(){
            override fun onVerticalSlide() {
                mFaceBusinessThread.setMode(IFaceBusiness.ENROLL_MODE)
            }

            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
                XPopup.Builder(this@MainActivity)
                    .hasStatusBarShadow(false) //.dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .autoOpenSoftInput(true)
                    .isDarkTheme(false)
                    .autoFocusEditText(true) //是否让弹窗内的EditText自动获取焦点，默认是true
                    .moveUpToKeyboard(true)   //是否移动到软键盘上面，默认为true
                    .dismissOnTouchOutside(false)
                    .hasShadowBg(true)
                    .hasBlurBg(true)
                    .asInputConfirm(
                        "进入设置界面", null, null, "请输入密码"
                    ) {
                        if (it == "1234")
                            this@MainActivity.startActivity(
                                Intent(this@MainActivity, SettingActivity::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(this@MainActivity)
                                    .toBundle()
                            )
                    }
                    .show()
            }
        })
        mCameraPreview = findViewById(R.id.amc_camera_preview)
        mFaceDetectView = findViewById(R.id.amc_face_detect_view)
        mLottieAnimationView = findViewById(R.id.amc_lottie_eyes)
        mLottieAnimationView.repeatCount = -1
        mCameraPreview.setView(mFaceDetectView)
        mFaceBusinessThread = FaceRecognizeThread("face_j")
        mFaceBusinessThread.start()
        Log.d(TAG, "onCreate")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        EventBus.getDefault().unregister(this)
        mFaceBusinessThread.destroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            Log.d(TAG, "获得焦点")
            mFaceBusinessThread.resume()
            ColorCamera.getInstance().startCamera()
        } else {
            Log.d(TAG, "失去焦点")
            mFaceBusinessThread.pause()
            ColorCamera.getInstance().stopCamera()
        }
    }

    override fun setStatusBar() {
        StatusBarUtil.setTransparent(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventBusTips(event: EventTips<String>) {
        when (event.code) {
            TipMessageCode.Message_Color_Recognize_Resume -> {
                mFaceBusinessThread.resume()
                mLottieAnimationView.pauseAnimation()
                mLottieAnimationView.visibility = View.INVISIBLE
            }

            TipMessageCode.Message_Color_Recognize_Pause -> {
                mFaceBusinessThread.pause()
                mLottieAnimationView.playAnimation()
                mLottieAnimationView.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        Log.d("Jowney", this.supportFragmentManager.backStackEntryCount.toString())
        true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }
}