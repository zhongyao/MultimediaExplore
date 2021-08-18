package com.hongri.multimedia.audio.state;


import android.util.Log;

import com.hongri.multimedia.audio.AudioRecorder;

/**
 * Create by zhongyao on 2021/8/18
 * Description:
 */
public class AudioStatusManager {

    private static final String TAG = "AudioStatusManager";
    private static Status currentStatus;

    public static void setStatus(Status curStatus) {
        Log.d(TAG, "curStatus:" + curStatus);
        currentStatus = curStatus;
        switch (curStatus) {
            case STATUS_NO_READY:

                break;

            case STATUS_READY:
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
                AudioRecorder.getInstance().canel();
                break;

            default:
                break;
        }
    }

    public static Status getStatus() {
        return currentStatus;
    }
}
