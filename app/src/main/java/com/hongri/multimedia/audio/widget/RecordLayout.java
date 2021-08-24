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

import com.hongri.multimedia.audio.RecordStreamListener;
import com.hongri.multimedia.audio.state.AudioStatusManager;
import com.hongri.multimedia.audio.state.Status;
import com.hongri.multimedia.util.DataUtil;

/**
 * Create by zhongyao on 2021/8/17
 * Description:录音布局类
 */
public class RecordLayout extends FrameLayout implements RecordStreamListener {

    private Activity activity;
    private int phoneWidth;
    private int borderWidth;
    private RecordButton recordBtn;
    private ImageView deleteBtn;
    private final long RECORD_BORDER_TIME = 1000;
    final Object mLock = new Object();
    private boolean isPressed;

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
    private long startTime, endTime;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onInterceptTouchEvent ---> event:" + event.getAction());

        if (activity == null || recordBtn == null || deleteBtn == null) {
            return false;
        }


        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isPressed = true;
                startTime = System.currentTimeMillis();
                lastTouchX = event.getX();
                lastTouchY = event.getY();

                lastRawX = event.getRawX();
                lastRawY = event.getRawY();

                if (isPointInRecordRect(lastTouchX, lastTouchY) && AudioStatusManager.getStatus() != Status.STATUS_START) {
                    recordBtn.updateLayout(true, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, recordBtnWidth / 3);
//                    recordBtn.setBackgroundResource(R.drawable.audio_record_pressed_bg);
                    AudioStatusManager.setStatus(Status.STATUS_START, this);
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
                isPressed = false;
                endTime = System.currentTimeMillis();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ((currentX > borderWidth && distanceX > (borderWidth / 4.0)) || (endTime - startTime < RECORD_BORDER_TIME)) {
                            Log.d(TAG, "trigger record cancel");
                            AudioStatusManager.setStatus(Status.STATUS_CANCEL);
                        } else {
                            Log.d(TAG, "trigger record finish");
                            AudioStatusManager.setStatus(Status.STATUS_STOP);
                        }

                        recordBtn.updateLayout(false, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, recordBtnWidth / 3);
//                        recordBtn.setBackgroundResource(R.drawable.audio_record_normal_bg);
                        deleteBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }, 100);

                break;
            case MotionEvent.ACTION_CANCEL:
                isPressed = false;
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

    @Override
    public void recordOfByte(byte[] data, int begin, int end) {
        if (!isPressed) {
            return;
        }
        double volume = DataUtil.calculateVolumeByBytes(data);
        Log.d(TAG, "volume: " + volume);

        post(new Runnable() {
            @Override
            public void run() {
                recordBtn.updateLayout(true, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, (recordBtnWidth / 3) + (float) volume * 3);
            }
        });

        synchronized (mLock) {
            try {
                mLock.wait(100); // 一秒十次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
