package com.hongri.multimedia.audio;

/**
 * Create by zhongyao on 2021/8/17
 * Description:
 */
public interface RecordStreamListener {
    void recordOfByte(byte[] data, int begin, int end);
}
