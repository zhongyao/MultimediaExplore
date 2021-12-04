package com.hongri.multimedia.audio.listener;

/**
 * Create by zhongyao on 2021/12/3
 * Description:
 */
public interface AudioScreenListener {
    /**
     * @param screenOn true 开屏，false 锁屏
     */
    void screenChanged(boolean screenOn);
}
