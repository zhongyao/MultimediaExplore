package com.hongri.multimedia.audio.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

/**
 * Create by zhongyao on 2021/8/17
 * Description:
 */
public class RecordButton extends AppCompatButton {

    private final String TAG = "RecordButton";

    public RecordButton(@NonNull Context context) {
        super(context);
    }

    public RecordButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent ---> event:" + event.getAction());
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                break;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:

                break;

            default:

                break;
        }
        return super.onTouchEvent(event);
    }
}
