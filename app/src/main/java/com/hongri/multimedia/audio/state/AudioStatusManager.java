package com.hongri.multimedia.audio.state;


import android.content.Context;
import android.util.Log;

import com.hongri.multimedia.audio.AudioRecorder;
import com.hongri.multimedia.audio.FileUtil;
import com.hongri.multimedia.audio.RecordStreamListener;
import com.hongri.multimedia.audio.listener.RecordDataListener;
import com.hongri.multimedia.audio.listener.RecordFftDataListener;
import com.hongri.multimedia.audio.listener.RecordResultListener;
import com.hongri.multimedia.audio.listener.RecordSoundSizeListener;
import com.hongri.multimedia.audio.listener.RecordStateListener;

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
        AudioRecorder.getInstance().setCurrentConfig(recordConfig);
    }

    public static RecordConfig getCurrentConfig() {
        return AudioRecorder.getInstance().getCurrentConfig();
    }

    /**
     * 录音状态监听回调
     */
    public void setRecordStateListener(RecordStateListener listener) {
        AudioRecorder.getInstance().setRecordStateListener(listener);
    }

    /**
     * 录音数据监听回调
     */
    public void setRecordDataListener(RecordDataListener listener) {
        AudioRecorder.getInstance().setRecordDataListener(listener);
    }

    /**
     * 录音可视化数据回调，傅里叶转换后的频域数据
     */
    public void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
        AudioRecorder.getInstance().setRecordFftDataListener(recordFftDataListener);
    }

    /**
     * 录音文件转换结束回调
     */
    public void setRecordResultListener(RecordResultListener listener) {
        AudioRecorder.getInstance().setRecordResultListener(listener);
    }

    /**
     * 录音音量监听回调
     */
    public void setRecordSoundSizeListener(RecordSoundSizeListener listener) {
        AudioRecorder.getInstance().setRecordSoundSizeListener(listener);
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
                AudioRecorder.getInstance().createDefaultAudio(FileUtil.getFilePath());
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
