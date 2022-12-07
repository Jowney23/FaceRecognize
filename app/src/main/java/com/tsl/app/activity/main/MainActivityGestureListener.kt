package com.tsl.app.activity.main

import android.view.GestureDetector
import android.view.MotionEvent
import com.jowney.common.util.logger.L

abstract class MainActivityGestureListener : GestureDetector.OnGestureListener {

    override fun onDown(e: MotionEvent?): Boolean {
        //  L.v("down"+e.toString())
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
        //   L.v("showPress"+e.toString())
        return
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        //  L.v("singleTapUp"+e.toString())
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        //   L.v("scroll"+e1.toString() +"   "+e2.toString() +"  "+distanceX+ "  "+distanceY)
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
      //   L.v("LongPress"+e.toString())
        return
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e2 == null || e1 == null) return false
        if (e2.x - e1.x <= 120f && e2.y - e1.y >= 300f)
            onVerticalSlide()
        return false
    }

    abstract fun onVerticalSlide()
}