package com.hongri.multimedia.audio.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private Paint paintInner;
    private Paint paintOuter;
    private float cx, cy;
    private float innerRadius;
    private float outerRadius;

    public RecordButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RecordButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        paintInner = new Paint();
        paintOuter = new Paint();
        paintInner.setColor(Color.parseColor("#FF0000"));
        paintOuter.setColor(Color.parseColor("#00FF00"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(cx, cy, outerRadius, paintOuter);
        canvas.drawCircle(cx, cy, innerRadius, paintInner);
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

    public void updateLayout(float cx, float cy, float innerRadius, float outerRadius) {
        this.cx = cx;
        this.cy = cy;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        invalidate();
    }
}
