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
 * Description: 录音按钮
 */
public class RecordButton extends AppCompatButton {

    private final String TAG = "RecordButton";
    private Paint paintInner;
    private Paint paintOuter;
    private float cx, cy;
    private boolean isSelected;
    private float innerRadius;
    private float outerRadius;
    private final String COLOR_NORMAL = "#ff4ec899";
    private final String COLOR_SELECTED = "#ff99dfc4";

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
        paintInner.setAntiAlias(true);
        paintInner.setColor(Color.parseColor(COLOR_NORMAL));

        paintOuter = new Paint();
        paintOuter.setAntiAlias(true);
        paintOuter.setColor(Color.parseColor(COLOR_SELECTED));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "innerRadius:" + innerRadius + " outerRadius:" + outerRadius);
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

    public void updateLayout(boolean isSelected, float cx, float cy, float innerRadius, float outerRadius) {
        this.isSelected = isSelected;
        this.cx = cx;
        this.cy = cy;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        invalidate();
    }
}
