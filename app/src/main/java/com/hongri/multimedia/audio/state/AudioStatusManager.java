package com.hongri.multimedia.audio.state;


import android.util.Log;

import com.hongri.multimedia.audio.AudioRecorder;
import com.hongri.multimedia.audio.RecordStreamListener;
import com.hongri.multimedia.util.DoubleClickUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create by zhongyao on 2021/8/18
 * Description:
 */
public class AudioStatusManager {

    private static final String TAG = "AudioStatusManager";
    private static String fileName;
    private static RecordStreamListener listener = null;

    public static void setStatus(Status curStatus) {
        Log.d(TAG, "curStatus:" + curStatus);
        setStatus(curStatus, null);
    }

    public static void setStatus(Status curStatus, Object object) {
        switch (curStatus) {
            case STATUS_NO_READY:

                break;

            case STATUS_READY:
                //音频初始化
                if (object instanceof String) {
                    fileName = (String) object;
                } else {
                    fileName = "";
                }
                AudioRecorder.getInstance().createDefaultAudio(fileName);
                break;

            case STATUS_START:
                if (object instanceof RecordStreamListener) {
                    listener = (RecordStreamListener) object;
                } else {
                    listener = null;
                }
                AudioRecorder.getInstance().startRecord(listener);
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
