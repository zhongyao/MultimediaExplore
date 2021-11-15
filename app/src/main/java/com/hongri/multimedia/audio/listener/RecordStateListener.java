package com.hongri.multimedia.audio.listener;
import com.hongri.multimedia.audio.state.AudioRecordStatus;

/**
 * @author zhaolewei on 2018/7/11.
 */
public interface RecordStateListener {

    /**
     * 当前的录音状态发生变化
     *
     * @param state 当前状态
     */
    void onStateChange(AudioRecordStatus state);

    /**
     * 录音错误
     *
     * @param error 错误
     */
    void onError(String error);

}
