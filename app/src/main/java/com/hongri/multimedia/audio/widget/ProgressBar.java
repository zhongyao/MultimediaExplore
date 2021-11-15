package com.hongri.multimedia.audio.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

/**
 * Create by zhongyao on 2021/8/24
 * Description:
 */
public class ProgressBar extends AppCompatButton {

    private static final String TAG = "RecordProgressBar";
    private Paint linePaintBottom;
    private Paint linePaintTop;
    private Paint circlePaint;
    private final String LINE_BOTTOM_COLOR = "#A6A6A6";
    private final String LINE_TOP_COLOR = "#000000";
    private final String CIRCLE_COLOR = "#ff0000ff";
    private boolean isInit;
    private float startX, startY, stopX, stopY;
    private TextView currentPlayTime;
    private long duration;
    private float width, height;

    public ProgressBar(@NonNull Context context) {
        super(context);

        init(context);
    }

    public ProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        Log.d(TAG, "init");
        linePaintBottom = new Paint();
        linePaintTop = new Paint();
        circlePaint = new Paint();
        linePaintBottom.setColor(Color.parseColor(LINE_BOTTOM_COLOR));
        linePaintBottom.setStrokeWidth((float) 5.0);

        linePaintTop.setColor(Color.parseColor(LINE_TOP_COLOR));
        linePaintTop.setStrokeWidth((float) 5.0);

        circlePaint.setColor(Color.parseColor(CIRCLE_COLOR));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        width = getWidth();
        height = getHeight();
        Log.d(TAG, "width:" + width + " height:" + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, startY, getWidth(), stopY, linePaintBottom);
        if (!isInit) {
            canvas.drawLine(0, startY, stopX, stopY, linePaintTop);
            canvas.drawCircle(stopX, stopY, 30, circlePaint);
        } else {
            canvas.drawCircle(0, getHeight() / (float) 2.0, 30, circlePaint);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "child--onTouchEvent---> event:" + event.getAction());
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                float stopX = event.getX();

                Log.d(TAG, "duration:" + duration + " width:" + width + " stopX:" + stopX);
                float currentSeekPosition = (duration / width) * stopX;
                Log.d(TAG, "currentSeekPosition:" + currentSeekPosition);
                if (currentPlayTime != null) {
                    currentPlayTime.setText(Math.round(currentSeekPosition) + "/" + duration + "s");
                }
                updatePlayLayout(false, 0, getHeight() / (float) 2.0, stopX, getHeight() / (float) 2.0);
                break;

            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void updatePlayLayout(boolean isInit, float startX, float startY, float stopX, float stopY) {
        Log.d(TAG, "isInit:" + isInit + " startX:" + startX + " startY:" + startY + " stopX:" + stopX + " stopY:" + stopY);
        this.isInit = isInit;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
        invalidate();
    }

    public void setCurrentPlayTimeView(TextView currentPlayTime) {
        this.currentPlayTime = currentPlayTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCurrentPosition(long currentPosition) {
        this.isInit = false;
        Log.d(TAG, "currentPosition:" + currentPosition + " duration:" + duration + " getWidth:" + getWidth());
        this.stopX = ((float)currentPosition / (float) duration) * getWidth();
        Log.d(TAG, "stopX:" + stopX);
        invalidate();
    }
}
