package com.hongri.multimedia.audio.state;


import android.util.Log;

import com.hongri.multimedia.audio.AudioRecorder;
import com.hongri.multimedia.util.DoubleClickUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create by zhongyao on 2021/8/18
 * Description:
 */
public class AudioStatusManager {

    private static final String TAG = "AudioStatusManager";

    public static void setStatus(Status curStatus) {
        Log.d(TAG, "curStatus:" + curStatus);

//        if (DoubleClickUtil.isFastDoubleInvoke(0)) {
//            Log.d(TAG, "change status is so fast...");
//            return;
//        }
        switch (curStatus) {
            case STATUS_NO_READY:

                break;

            case STATUS_READY:
                //音频初始化
                AudioRecorder.getInstance().createDefaultAudio("");
                break;

            case STATUS_START:
                AudioRecorder.getInstance().startRecord(null);
                break;

            case STATUS_PAUSE:
                AudioRecorder.getInstance().pauseRecord();
                break;

            case STATUS_STOP:
                AudioRecorder.getInstance().stopRecord();
                break;

            case STATUS_CANCEL:
                AudioRecorder.getInstance().cancel();
                break;

            case STATUS_RELEASE:
                AudioRecorder.getInstance().releaseRecord();
                break;

            default:
                break;
        }
    }

    public static Status getStatus() {
        return AudioRecorder.getInstance().getStatus();
    }
}
