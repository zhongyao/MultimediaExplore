package com.hongri.multimedia.audio.state;


import android.content.Context;
import android.util.Log;

import com.hongri.multimedia.audio.RecordStreamListener;
import com.hongri.multimedia.audio.listener.RecordDataListener;
import com.hongri.multimedia.audio.listener.RecordFftDataListener;
import com.hongri.multimedia.audio.listener.RecordResultListener;
import com.hongri.multimedia.audio.listener.RecordSoundSizeListener;
import com.hongri.multimedia.audio.listener.RecordStateListener;
import com.hongri.multimedia.audio.service.RecordService;

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

    /**
     * 录音状态监听回调
     */
    public void setRecordStateListener(RecordStateListener listener) {
        RecordService.setRecordStateListener(listener);
    }

    /**
     * 录音数据监听回调
     */
    public void setRecordDataListener(RecordDataListener listener) {
        RecordService.setRecordDataListener(listener);
    }

    /**
     * 录音可视化数据回调，傅里叶转换后的频域数据
     */
    public void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
        RecordService.setRecordFftDataListener(recordFftDataListener);
    }

    /**
     * 录音文件转换结束回调
     */
    public void setRecordResultListener(RecordResultListener listener) {
        RecordService.setRecordResultListener(listener);
    }

    /**
     * 录音音量监听回调
     */
    public void setRecordSoundSizeListener(RecordSoundSizeListener listener) {
        RecordService.setRecordSoundSizeListener(listener);
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
