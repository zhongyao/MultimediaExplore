package com.hongri.multimedia.audio;

import com.hongri.multimedia.audio.listener.RecordDataListener;
import com.hongri.multimedia.audio.listener.RecordFftDataListener;
import com.hongri.multimedia.audio.listener.RecordResultListener;
import com.hongri.multimedia.audio.listener.RecordSoundSizeListener;
import com.hongri.multimedia.audio.listener.RecordStateListener;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.audio.state.AudioRecordStatus;

/**
 * Create by zhongyao on 2021/8/18
 * Description:
 */
public class AudioRecordManager {

    private static final String TAG = "AudioRecordManager";

    private AudioRecordManager() {
    }

    public static AudioRecordManager getInstance() {
        return AudioRecordManagerHolder.instance;
    }

    public static class AudioRecordManagerHolder {
        public static AudioRecordManager instance = new AudioRecordManager();
    }

    public void setCurrentConfig(RecordConfig recordConfig) {
        AudioRecorder.getInstance().setCurrentConfig(recordConfig);
    }

    public RecordConfig getCurrentConfig() {
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

    public void setStatus(AudioRecordStatus curAudioRecordStatus) {
        switch (curAudioRecordStatus) {
            case AUDIO_RECORD_IDLE:

                break;

            case AUDIO_RECORD_PREPARE:
                AudioRecorder.getInstance().prepareRecord();
                break;

            case AUDIO_RECORD_START:
                AudioRecorder.getInstance().startRecord();
                break;

            case AUDIO_RECORD_PAUSE:
                AudioRecorder.getInstance().pauseRecord();
                break;

            case AUDIO_RECORD_STOP:
                AudioRecorder.getInstance().stopRecord();
                break;

            case AUDIO_RECORD_CANCEL:
                AudioRecorder.getInstance().cancelRecord();
                break;

            case AUDIO_RECORD_RELEASE:
                AudioRecorder.getInstance().releaseRecord();
                break;

            default:
                break;
        }
    }

    public AudioRecordStatus getStatus() {
        return AudioRecorder.getInstance().getAudioRecordStatus();
    }
}
