package com.hongri.multimedia.audio;

import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * Create by zhongyao on 2021/11/15
 * Description:音频资源管理类
 */
public class AudioMediaSourceManager {

    private final String TAG = "AudioMediaSourceManager";

    private MediaSource mediaSource;

    public static AudioMediaSourceManager getInstance() {
        return AudioMediaSourceManagerHolder.instance;
    }

    public static class AudioMediaSourceManagerHolder {
        public static AudioMediaSourceManager instance = new AudioMediaSourceManager();
    }

    public MediaSource buildMediaSource(Uri uri, boolean isLocalMedia) {
        DataSource.Factory dataSourceFactory;
        if (uri == null) {
            return null;
        }
        int type = Util.inferContentType(uri);
        if (isLocalMedia) {
            dataSourceFactory = new FileDataSource.Factory();
        } else {
            dataSourceFactory = new DefaultHttpDataSource.Factory();
        }
        Log.d(TAG, "buildMediaSource --- > uri:" + uri.toString() + " type:" + type);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

}
