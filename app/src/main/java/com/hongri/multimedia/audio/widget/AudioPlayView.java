package com.hongri.multimedia.audio.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

import com.hongri.multimedia.R;
import com.hongri.multimedia.audio.AudioPlayer;
import com.hongri.multimedia.audio.AudioPlayManager;
import com.hongri.multimedia.audio.AudioRecordManager;
import com.hongri.multimedia.audio.state.AudioPlayStatus;

import java.util.HashMap;

/**
 * Create by zhongyao on 2021/8/24
 * Description:播放录音
 */
public class AudioPlayView extends FrameLayout implements View.OnTouchListener {

    private final String TAG = "AudioPlayView";
    private ImageView playIv;
    private ProgressBar progressBar;
    private TextView playTime;
    private static long duration;
    private TextView currentPlayTime;
    private float progressBarLeftX, progressBarRightX, progressBarWidth;
    private float progressBarTopY, progressBarBottomY, progressBarHeight;
    private long currentPosition, contentPosition, contentBufferedPosition;

    //    private String audioUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pauseRecordDemo" + "/wav/" + "20210830045802.wav";
    private String audioUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Record" + "/zhongyao/" + "record_20211110_19_25_41.mp3";
//    private String audioUrl = "http://file.kuyinyun.com/group1/M00/90/B7/rBBGdFPXJNeAM-nhABeMElAM6bY151.mp3";
//    private String audioUrl = "http://192.168.1.102:1231/music/a.mp3";
//    private String audioUrl = "https://www.twle.cn/static/i/song.mp3";

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AudioPlayer.WHAT_DURATION:
                    duration = (long) msg.obj;
                    if (playTime != null) {
                        playTime.setText((duration + "s"));
                    }

                    if (progressBar != null) {
                        progressBar.setDuration(duration);
                    }
                    break;

                case AudioPlayer.WHAT_POSITION:
                    if (msg.obj instanceof HashMap) {
                        HashMap<String, Long> hashMap = (HashMap<String, Long>) msg.obj;
                        currentPosition = hashMap.get("currentPosition");
                        contentPosition = hashMap.get("contentPosition");
                        contentBufferedPosition = hashMap.get("contentBufferedPosition");
                        Log.d(TAG, "handler---> currentPosition:" + currentPosition + " contentPosition:" + contentPosition + " contentBufferedPosition:" + contentBufferedPosition);
                        progressBar.setCurrentPosition(currentPosition);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public AudioPlayView(@NonNull Context context) {
        super(context);
    }

    public AudioPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
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
                playIv = (ImageView) childView1;
            }

            View childView2 = getChildAt(1);
            if (childView2 instanceof ProgressBar) {
                progressBar = (ProgressBar) childView2;

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

            if (playIv != null) {
                playIv.setOnTouchListener(this);
            }

            if (progressBar != null && currentPlayTime != null) {
                progressBar.setCurrentPlayTimeView(currentPlayTime);
            }
        }
    }

    private void audioPrepare(Uri uri) {
        AudioPlayManager.setStatus(getContext(), handler, AudioPlayStatus.AUDIO_PREPARE, uri);
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
                    Log.d(TAG, "Touch事件传递至子View");
                    //不拦截事件，传递给子ProgressBar消费
                    return false;
                } else {
                    return true;
                }
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch");
        int id = v.getId();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (id == R.id.playIv) {

                    audioUrl = AudioRecordManager.getInstance().getAudioPath();
                    Log.d(TAG, "audioUrl:" + audioUrl);
                    if (TextUtils.isEmpty(audioUrl)) {
                        Toast.makeText(getContext(), "请先录制音频文件", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    audioPrepare(Uri.parse(audioUrl));
                    AudioPlayManager.setStatus(AudioPlayStatus.AUDIO_START);
                    return true;
                }
                break;
            default:
                break;
        }

        return false;
    }

    public void onRelease() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
