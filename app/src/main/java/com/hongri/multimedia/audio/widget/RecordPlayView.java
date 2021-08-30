package com.hongri.multimedia.audio.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Create by zhongyao on 2021/8/24
 * Description:播放录音
 */
public class RecordPlayView extends FrameLayout {

    private final String TAG = "RecordPlayView";
    private ImageView playAnimIv;
    private RecordProgressBar progressBar;
    private TextView playTime;
    private TextView currentPlayTime;
    private float progressBarLeftX, progressBarRightX, progressBarWidth;
    private float progressBarTopY, progressBarBottomY, progressBarHeight;

    public RecordPlayView(@NonNull Context context) {
        super(context);
    }

    public RecordPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int childCount = getChildCount();
        if (childCount >= 4) {
            View childView1 = getChildAt(0);
            if (childView1 instanceof ImageView) {
                playAnimIv = (ImageView) childView1;
            }

            View childView2 = getChildAt(1);
            if (childView2 instanceof RecordProgressBar) {
                progressBar = (RecordProgressBar) childView2;

                progressBarLeftX = progressBar.getX();
                progressBarWidth = progressBar.getWidth();
                progressBarRightX = progressBarLeftX + progressBarWidth;

                progressBarTopY = progressBar.getY();
                progressBarHeight = progressBar.getHeight();
                progressBarBottomY = progressBarTopY + progressBarHeight;

                progressBar.updatePlayLayout(true, progressBarLeftX, progressBarHeight / 2, progressBarWidth, progressBarHeight / 2);

                Log.d(TAG, "progressBarLeftX:" + progressBarLeftX + " progressBarWidth:" + progressBarWidth + " progressBarRightX:" + progressBarRightX + "\nprogressBarTopY:" + progressBarTopY + " progressBarHeight:" + progressBarHeight + " progressBarBottomY:" + progressBarBottomY);
            }

            View childView3 = getChildAt(2);
            if (childView3 instanceof TextView) {
                playTime = (TextView) childView3;
            }

            View childView4 = getChildAt(3);
            if (childView4 instanceof TextView) {
                currentPlayTime = (TextView) childView4;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d(TAG, "parent--onInterceptTouchEvent---> event:" + event.getAction());
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                float stopX = event.getX();
                float stopY = event.getY();
                if (stopX > progressBarLeftX && stopX < progressBarRightX) {
                    //不拦截事件，传递给子ProgressBar消费
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;

            default:

                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "parent--onTouchEvent---> event:" + event.getAction());
        return super.onTouchEvent(event);
    }
}
