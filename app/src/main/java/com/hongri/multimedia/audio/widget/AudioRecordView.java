package com.hongri.multimedia.audio.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hongri.multimedia.audio.listener.RecordSoundSizeListener;
import com.hongri.multimedia.audio.AudioRecordManager;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.audio.state.AudioRecordStatus;
import com.hongri.multimedia.util.DateUtil;

/**
 * Create by zhongyao on 2021/8/17
 * Description:录音View
 */
public class AudioRecordView extends FrameLayout implements RecordSoundSizeListener {

    private Activity activity;
    private int phoneWidth;
    private int borderWidth;
    private RecordButton recordBtn;
    private ImageView deleteBtn;
    private TextView timeTv;
    private final long RECORD_BORDER_TIME = 1000;
    final Object mLock = new Object();
    private boolean isPressed;
    private RecordConfig recordConfig = new RecordConfig();

    private static final String TAG = "RecordLayout";

    public AudioRecordView(@NonNull Context context) {
        super(context);
    }

    public AudioRecordView(@NonNull Context context, @Nullable AttributeSet attrs) {
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
        if (childCount >= 3) {
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

            View childView3 = getChildAt(2);
            if (childView3 instanceof TextView) {
                timeTv = (TextView) childView3;
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
    private static final int UPDATE_TIME = 0;
    private long recordTime;
    private boolean isCancelRegion;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:
                    Log.d(TAG, "UPDATE_TIME:");
                    recordTime = 1000 + recordTime;
                    if (timeTv != null && !isCancelRegion) {
                        timeTv.setText(DateUtil.generateTime(recordTime));
                    }
                    sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                    break;

                default:

                    break;
            }
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (activity == null || recordBtn == null || deleteBtn == null) {
            return false;
        }


        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isPressed = true;
                timeTv.setText("00:00");
                if (handler != null) {
                    handler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                }
                startTime = System.currentTimeMillis();
                lastTouchX = event.getX();
                lastTouchY = event.getY();

                lastRawX = event.getRawX();
                lastRawY = event.getRawY();

                if (isPointInRecordRect(lastTouchX, lastTouchY) && AudioRecordManager.getInstance().getStatus() != AudioRecordStatus.AUDIO_RECORD_START) {
                    recordBtn.updateLayout(true, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, recordBtnWidth / 3);
                    AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_PREPARE);
                    AudioRecordManager.getInstance().setRecordSoundSizeListener(this);
                    AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_START);
                }
                Log.d(TAG, "lastTouchX:" + lastTouchX + " lastTouchY:" + lastTouchY + " lastRawX:" + lastRawX + " lastRawY:" + lastRawY);
                break;

            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                distanceX = currentX - lastTouchX;

                if (currentX > borderWidth && distanceX > (borderWidth / 4.0)) {
                    deleteBtn.setBackgroundColor(Color.parseColor("#FF0000"));
                    timeTv.setText("松手取消发送");
                    isCancelRegion = true;
                } else {
                    deleteBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    isCancelRegion = false;
                }

                break;

            case MotionEvent.ACTION_UP:
                isPressed = false;
                handler.removeCallbacksAndMessages(null);
                recordTime = 0;
                timeTv.setText("按住说话");
                endTime = System.currentTimeMillis();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ((currentX > borderWidth && distanceX > (borderWidth / 4.0)) || (endTime - startTime < RECORD_BORDER_TIME)) {
                            Log.d(TAG, "trigger record cancel");
                            if ((endTime - startTime < RECORD_BORDER_TIME)) {
                                Toast.makeText(getContext(), "时间太短", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "已取消", Toast.LENGTH_LONG).show();
                            }
                            AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_CANCEL);
                        } else {
                            Log.d(TAG, "trigger record finish");
                            AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_STOP);
                        }

                        recordBtn.updateLayout(false, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, recordBtnWidth / 3);
                        deleteBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }, 100);

                break;
            case MotionEvent.ACTION_CANCEL:
                isPressed = false;
                handler.removeCallbacksAndMessages(null);
                recordTime = 0;
                timeTv.setText("按住说话");
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
    public void onSoundSize(int volume) {
        if (!isPressed) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                recordBtn.updateLayout(true, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, (recordBtnWidth / 3) + (float) volume /** 3*/);
            }
        });
    }

    public void setRecordConfig(RecordConfig recordConfig) {
        this.recordConfig = recordConfig;
    }
}
