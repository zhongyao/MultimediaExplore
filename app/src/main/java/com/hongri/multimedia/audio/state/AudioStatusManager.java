package com.hongri.multimedia.audio.state;


import android.content.Context;
import android.util.Log;

import com.hongri.multimedia.audio.AudioRecorder;
import com.hongri.multimedia.audio.RecordStreamListener;
import com.hongri.multimedia.audio.service.RecordService;
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
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static void setCurrentConfig(RecordConfig recordConfig) {
        RecordService.setCurrentConfig(recordConfig);
    }

    public static RecordConfig getCurrentConfig() {
        return RecordService.getCurrentConfig();
    }

    public static void setStatus(Status curStatus) {
        Log.d(TAG, "curStatus:" + curStatus);
        setStatus(curStatus, null);
    }

    public static void setStatus(Status curStatus, Object object) {
        if (mContext == null) {
            Log.e(TAG, "AudioStatusManager not init");
            return;
        }
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
                RecordService.createDefaultAudio(mContext, fileName);
                break;

            case STATUS_START:
                if (object instanceof RecordStreamListener) {
                    listener = (RecordStreamListener) object;
                } else {
                    listener = null;
                }
                RecordService.startRecording(mContext, listener);
                break;

            case STATUS_PAUSE:
                RecordService.pauseRecording(mContext);
                break;

            case STATUS_STOP:
                RecordService.stopRecording(mContext);
                break;

            case STATUS_CANCEL:
                RecordService.cancelRecording(mContext);
                break;

            case STATUS_RELEASE:
                RecordService.releaseRecord(mContext);
                break;

            default:
                break;
        }
    }

    public static Status getStatus() {
        return RecordService.getState();
    }
}
