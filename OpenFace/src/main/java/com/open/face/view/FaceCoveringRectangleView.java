/*
 * Date 	: 2015.03.10 19:23:46
 * Author	: Newpub
 */
package com.open.face.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class FaceCoveringRectangleView extends View {

    private Paint paint;
    private Paint rightAnglePaint;
    private float[][] mPts;
    private float[][] mRect;

    private static final int POINTS = 4;
    private static final int COUNT = 4;
    private float mWidth;
    private float mHeight;

    private int WIDTH = 480;
    private int HEIGHT = 640;
    private int left, top, right, bottom;
    private float Width_rate = 1;
    private float Height_rate = 1;


    public FaceCoveringRectangleView(Context context) {
        super(context);

    }

    public FaceCoveringRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPts = new float[COUNT][POINTS * 2];
        mRect = new float[COUNT][2];
        //框的画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
        //直角的画笔
        rightAnglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rightAnglePaint.setColor(Color.YELLOW);
        rightAnglePaint.setStrokeWidth(7);
        rightAnglePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }



    public void setRect(int left, int top, int right, int bottom, int p) {

        if (Height_rate == 0)
            return;

        mRect[0][0] = left * Width_rate;
        mRect[0][1] = top * Height_rate;

        mRect[1][0] = right * Width_rate;
        mRect[1][1] = top * Height_rate;

        mRect[2][0] = right * Width_rate;
        mRect[2][1] = bottom * Height_rate;

        mRect[3][0] = left * Width_rate;
        mRect[3][1] = bottom * Height_rate;
        mWidth = (right - left) * Width_rate / 8;
        mHeight = (bottom - top) * Height_rate / 8;
        buildPoints();
    }

    public void setRect(int left, int top, int width, int height) {

        if (Height_rate == 0)
            return;
        this.left = left;
        this.right = left + width;
        this.top = top;
        this.bottom = top + height;

        mRect[0][0] = left * Width_rate;
        mRect[0][1] = top * Height_rate;

        mRect[1][0] = (left + width) * Width_rate;
        mRect[1][1] = top * Height_rate;

        mRect[2][0] = (left + width) * Width_rate;
        mRect[2][1] = (top + height) * Height_rate;

        mRect[3][0] = left * Width_rate;
        mRect[3][1] = (top + height) * Height_rate;
        mWidth = (width) * Width_rate / 8;
        mHeight = (height) * Height_rate / 8;
        buildPoints();
    }


    private void buildPoints() {
        int signX, signY;
        for (int i = 0; i < COUNT; i++) {
            if (i < 2)
                signY = 1;
            else
                signY = -1;
            if (i == 1 || (i == 2))
                signX = -1;
            else
                signX = 1;
            mPts[i][0] = mRect[i][0];
            mPts[i][1] = mRect[i][1] + signY * mHeight;
            mPts[i][2] = mRect[i][0];
            mPts[i][3] = mRect[i][1];
            mPts[i][4] = mRect[i][0];
            mPts[i][5] = mRect[i][1];
            mPts[i][6] = mRect[i][0] + signX * mWidth;
            mPts[i][7] = mRect[i][1];
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Height_rate = (float) getHeight() / HEIGHT;
        Width_rate = (float) getWidth() / WIDTH;

        // canvas.drawRect(left*Width_rate, top*Height_rate , right*Width_rate, bottom*Height_rate, paint);
        for (int i = 0; i < 4; i++) canvas.drawLines(mPts[i], rightAnglePaint);

        /*	paint.setColor(Color.RED);
        for (int i = 0; i < 4; i++)canvas.drawPoints(mPts[i], paint);
        */
    }

}

