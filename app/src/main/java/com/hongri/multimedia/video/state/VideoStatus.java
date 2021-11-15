package com.hongri.multimedia.video.state;

/**
 * Create by zhongyao on 2021/11/15
 * Description: 视频播放状态
 */
public enum VideoStatus {
    /**
     * 未开始
     */
    VIDEO_IDLE,

    /**
     * 初始化
     */
    VIDEO_PREPARE,

    /**
     * 播放
     */
    VIDEO_START,

    /**
     * 暂停
     */
    VIDEO_PAUSE,

    /**
     * 停止
     */
    VIDEO_STOP,

    /**
     * 取消
     */
    VIDEO_CANCEL,

    /**
     * 释放
     */
    VIDEO_RELEASE
}
