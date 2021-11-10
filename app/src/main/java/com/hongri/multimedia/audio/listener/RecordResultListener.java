package com.hongri.multimedia.audio.listener;

import java.io.File;

/**
 * 录音完成回调
 */
public interface RecordResultListener {

    /**
     * 录音文件
     *
     * @param result 录音文件
     */
    void onResult(File result);
}
