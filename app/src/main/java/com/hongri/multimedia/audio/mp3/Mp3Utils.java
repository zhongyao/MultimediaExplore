package com.hongri.multimedia.audio.mp3;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.hongri.multimedia.util.FileUtil;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.util.Logger;

import java.io.IOException;

/**
 * Create by zhongyao on 2021/8/30
 * Description:
 */
public class Mp3Utils {
    private static final String TAG = Mp3Utils.class.getSimpleName();

    /**
     * 获取mp3音频的总时长 单位：ms
     *
     * @param mp3FilePath MP3文件路径
     * @return 时长
     */
    public static long getDuration(String mp3FilePath) {
        if (!FileUtil.isFileExists(mp3FilePath)) {
            return 0;
        }
        if (!mp3FilePath.endsWith(RecordConfig.RecordFormat.MP3.getExtension())) {
            return 0;
        }
        MediaExtractor mex = null;
        try {
            mex = new MediaExtractor();
            mex.setDataSource(mp3FilePath);
            MediaFormat mf = mex.getTrackFormat(0);
            long duration = mf.getLong(MediaFormat.KEY_DURATION) / 1000L;
            return duration;
        } catch (IOException e) {
            Logger.e(e, TAG, e.getMessage());
        } finally {
            if (mex != null) {
                mex.release();
            }
        }
        return 0;
    }
}
