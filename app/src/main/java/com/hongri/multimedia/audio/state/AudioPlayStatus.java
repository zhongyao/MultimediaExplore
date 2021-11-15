package com.hongri.multimedia.audio.state;

/**
 * Create by zhongyao on 2021/8/18
 * Description: 音频播放状态
 */
public enum AudioPlayStatus {
    /**
     * 未开始
     */
    AUDIO_IDLE,

    /**
     * 初始化
     */
    AUDIO_READY,

    /**
     * 播放
     */
    AUDIO_START,

    /**
     * 暂停
     */
    AUDIO_PAUSE,

    /**
     * 停止
     */
    AUDIO_STOP,

    /**
     * 取消
     */
    AUDIO_CANCEL,

    /**
     * 释放
     */
    AUDIO_RELEASE
}
