package com.open.face.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Created by Aaron on 2019-08-21.
 *
 * 绘制人脸框特效
 */
class FaceCoveringCircleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val useCenter = false

    //弧形大小偏移量
    private val faceOffset0 = 20
    private val faceOffset1 = 25
    private val faceOffset2 = 33
    private val faceOffset3 = 40

    //弧形透明度
    private val faceAlpha0 = 128
    private val faceAlpha1 = 200
    private val faceAlpha2 = 128
    private val faceAlpha3 = 220

    //弧形起始角度
    private val faceStartAngle0 = 300f
    private val faceStartAngle1 = 60f
    private val faceStartAngle2 = 240f
    private val faceStartAngle3 = 120f
    private val faceStartAngle4 = 300f

    //弧形角度偏移量
    private val faceSweepAngle0 = 240f
    private val faceSweepAngle1 = 240f
    private val faceSweepAngle2 = 240f
    private val faceSweepAngle3 = 60f
    private val faceSweepAngle4 = 60f

    //遮罩层透明度
    private val mMaskAlpha = 150


    private var mFaceRect: Rect? = null
    private val mPaint = Paint()

    private val mMaskPaint = Paint()
    private var mClearPaint = Paint()
    private var mTimeIndex: Float = 2F
    private var mMaxRect: Rect? = null
    private val mPorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)

    //人脸框已经被清楚
    private var mClearFlag: Boolean = false

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mMaxRect == null)
            mMaxRect = Rect(0, 0, width, height)
        if (mFaceRect == null) {
            mClearFlag = true
            mTimeIndex = 2F
            mClearPaint.alpha = 0
            canvas?.drawRect(mMaxRect!!, mClearPaint)
            return
        }
        mClearFlag = false
        mTimeIndex += 2
        mPaint.style = Paint.Style.STROKE  //设置空心
        mPaint.strokeWidth = 10.toFloat()

        val faceRectWidth = mFaceRect?.width() ?: 0
        val faceRectHeight = mFaceRect?.height() ?: 0
        //比较小的设置为半径
        val radius = if (faceRectHeight > faceRectWidth) {
            faceRectWidth / 2
        } else {
            faceRectHeight / 2
        }
        val cx = mFaceRect?.centerX() ?: 0
        val cy = mFaceRect?.centerY() ?: 0

        //绘制遮罩层
        //整个相机预览界面进行遮罩，透明度用来设置遮罩半透明
        canvas?.saveLayerAlpha(
            0f,
            0f,
            getWidth().toFloat(),
            getHeight().toFloat(),
            mMaskAlpha
        )
        mMaskPaint.color = Color.BLACK

        //整个相机预览界面进行遮罩
        canvas?.drawRect(mMaxRect!!, mMaskPaint)
        //重叠区域进行处理
        //只在源图像和目标图像不相交的地方绘制【源图像】，相交的地方根据目标图像的对应地方的alpha进行过滤，
        //目标图像完全不透明则完全过滤，完全透明则不过滤
        mMaskPaint.xfermode = mPorterDuffXfermode
        mMaskPaint.color = Color.BLACK
        canvas?.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), mMaskPaint)

        //自定义人脸框样式
        mPaint.flags = Paint.ANTI_ALIAS_FLAG  //抗锯齿
        mPaint.color = Color.GREEN

        //设置正方形，以便下面设置弧形为圆的弧形
        mFaceRect?.left = cx - radius
        mFaceRect?.top = cy - radius
        mFaceRect?.right = cx + radius
        mFaceRect?.bottom = cy + radius

        val rectF0 = getRectF(faceOffset0, mFaceRect)
        mPaint.strokeWidth = 4f
        mPaint.alpha = faceAlpha0
        canvas?.drawArc(
            rectF0,
            getStartAngle(faceStartAngle0, false),
            faceSweepAngle0,
            useCenter,
            mPaint
        )

        val rectF1 = getRectF(faceOffset1, mFaceRect)
        mPaint.strokeWidth = 8f
        mPaint.alpha = faceAlpha1
        canvas?.drawArc(
            rectF1,
            getStartAngle(faceStartAngle1, true),
            faceSweepAngle1,
            useCenter,
            mPaint
        )

        val rectF2 = getRectF(faceOffset2, mFaceRect)
        mPaint.strokeWidth = 3f
        mPaint.alpha = faceAlpha2
        canvas?.drawArc(
            rectF2,
            getStartAngle(faceStartAngle2, false),
            faceSweepAngle2,
            useCenter,
            mPaint
        )

        mPaint.alpha = faceAlpha3
        val rectF3 = getRectF(faceOffset3, mFaceRect)
        mPaint.strokeWidth = 9f
        canvas?.drawArc(
            rectF3,
            getStartAngle(faceStartAngle3, true),
            faceSweepAngle3,
            useCenter,
            mPaint
        )

        val rectF4 = getRectF(faceOffset3, mFaceRect)
        mPaint.strokeWidth = 9f
        canvas?.drawArc(
            rectF4,
            getStartAngle(faceStartAngle4, true),
            faceSweepAngle4,
            useCenter,
            mPaint
        )
        mMaskPaint.reset()
        canvas?.restore()
    }


    /**
     * 设置圆环偏移量
     * @param startAngle 角度
     * @param clockwise  是否顺时针
     */
    private fun getStartAngle(startAngle: Float, clockwise: Boolean): Float {
        val angle = if (clockwise) {
            startAngle + mTimeIndex
        } else {
            startAngle - mTimeIndex
        }

        return angle % 360
    }

    private fun getRectF(offset: Int, rect: Rect?): RectF {
        val rectF = RectF(rect)
        rectF.left = rectF.left - offset
        rectF.top = rectF.top - offset
        rectF.right = rectF.right + offset
        rectF.bottom = rectF.bottom + offset

        return rectF
    }


    fun setFaceRect(rect: Rect?) {
        mFaceRect = rect
        if (!mClearFlag || mFaceRect != null) invalidate()
    }

    fun getFaceRect(): Rect? = mFaceRect

}