package com.hongri.multimedia.audio.state;

/**
 * Create by zhongyao on 2021/8/18
 * Description:
 */
public enum Status {
    //未开始
    STATUS_NO_READY,
    //预备
    STATUS_READY,
    //录音
    STATUS_START,
    //暂停
    STATUS_PAUSE,
    //停止
    STATUS_STOP,
    //取消
    STATUS_CANCEL,
    //释放
    STATUS_RELEASE
}
