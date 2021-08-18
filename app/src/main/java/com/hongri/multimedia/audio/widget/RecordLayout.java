package com.hongri.multimedia.audio.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hongri.multimedia.R;
import com.hongri.multimedia.audio.state.AudioStatusManager;
import com.hongri.multimedia.audio.state.Status;

/**
 * Create by zhongyao on 2021/8/17
 * Description:录音布局类
 */
public class RecordLayout extends FrameLayout {

    private Activity activity;
    private int phoneWidth;
    private int borderWidth;
    private RecordButton recordBtn;
    private ImageView deleteBtn;

    private final String TAG = "RecordLayout";

    public RecordLayout(@NonNull Context context) {
        super(context);
    }

    public RecordLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private float recordBtnLeftX, recordBtnRightX;
    private float recordBtnWidth, recordBtnHeight;
    private float recordBtnTopY, recordBtnBottomY;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int childCount = getChildCount();
        if (childCount >= 2) {
            View childView1 = getChildAt(0);
            if (childView1 instanceof RecordButton) {
                recordBtn = (RecordButton) childView1;

                recordBtnLeftX = recordBtn.getX();
                recordBtnWidth = recordBtn.getWidth();
                recordBtnRightX = recordBtnLeftX + recordBtnWidth;

                recordBtnTopY = recordBtn.getY();
                recordBtnHeight = recordBtn.getHeight();
                recordBtnBottomY = recordBtnTopY + recordBtnHeight;

                Log.d(TAG, "onLayout---> recordBtnLeftX:" + recordBtnLeftX + " recordBtnWidth:" + recordBtnWidth + " recordBtnRightX:" + recordBtnRightX);
                Log.d(TAG, "onLayout---> recordBtnTopY:" + recordBtnTopY + " recordBtnHeight:" + recordBtnHeight + " recordBtnBottomY:" + recordBtnBottomY);
            }
            View childView2 = getChildAt(1);
            if (childView2 instanceof ImageView) {
                deleteBtn = (ImageView) childView2;
            }
        }
    }

    public void setPhoneWidth(Activity activity, int phoneWidth) {
        this.activity = activity;
        this.phoneWidth = phoneWidth;
        borderWidth = (this.phoneWidth * 2) / 3;
    }

    private float lastTouchX, lastTouchY, lastRawX, lastRawY;
    private float currentX = 0;
    private float currentY = 0;
    private float distanceX = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onInterceptTouchEvent ---> event:" + event.getAction());

        if (activity == null || recordBtn == null || deleteBtn == null) {
            return false;
        }



        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();

                lastRawX = event.getRawX();
                lastRawY = event.getRawY();

                if (isPointInRecordRect(lastTouchX, lastTouchY)) {
                    recordBtn.setBackgroundResource(R.drawable.audio_record_pressed_bg);
                }
                Log.d(TAG, "lastTouchX:" + lastTouchX + " lastTouchY:" + lastTouchY + " lastRawX:" + lastRawX + " lastRawY:" + lastRawY);
                break;

            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                distanceX = currentX - lastTouchX;

                if (currentX > borderWidth && distanceX > (borderWidth / 4.0)) {
                    deleteBtn.setBackgroundColor(Color.parseColor("#FF0000"));
                }

                break;

            case MotionEvent.ACTION_UP:
                if (currentX > borderWidth && distanceX > (borderWidth / 4.0)) {
                    Log.d(TAG, "trigger cancel");
                    AudioStatusManager.setStatus(Status.STATUS_CANCEL);
                }

                recordBtn.setBackgroundResource(R.drawable.audio_record_normal_bg);
                deleteBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;

            default:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent ---> event:" + event.getAction());
        return super.onTouchEvent(event);
    }

    private boolean isPointInRecordRect(float pointX, float pointY) {
        if (pointX < recordBtnLeftX || pointX > recordBtnRightX || pointY < recordBtnTopY || pointY > recordBtnBottomY) {
            return false;
        }
        return true;
    }
}
