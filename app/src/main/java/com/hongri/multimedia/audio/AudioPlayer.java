package com.hongri.multimedia.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.hongri.multimedia.audio.state.AudioPlayStatus;

import java.util.HashMap;

/**
 * Create by zhongyao on 2021/8/30
 * Description:音频播放器
 */
public class AudioPlayer {

    private final String TAG = "AudioPlayer";
    private ExoPlayer player;
    private DefaultDataSourceFactory dataSourceFactory;
    private MediaSource mediaSource;
    //播放状态
    private AudioPlayStatus audioStatus = AudioPlayStatus.AUDIO_IDLE;
    private boolean playWhenReady;
    private long duration;
    public static final int WHAT_DURATION = 0;
    public static final int WHAT_POSITION = 1;
    //    private Handler handler;
    private long currentPosition, contentPosition, contentBufferedPosition;
    private Handler handlerInner;
    public static boolean isLocalResource = false;

    private AudioPlayer() {
//        createDefaultPlayer();
    }

    public AudioPlayStatus getAudioStatus() {
        return audioStatus;
    }

    public static class AudioPlayerHolder {
        public static AudioPlayer instance = new AudioPlayer();
    }

    public static AudioPlayer getInstance() {
        return AudioPlayerHolder.instance;
    }


    @SuppressLint("HandlerLeak")
    public void createDefaultPlayer(Context context, Handler handler, Uri uri) {
        if (player == null) {
            player = new SimpleExoPlayer.Builder(context).build();
        }
        mediaSource = AudioMediaSourceManager.getInstance().buildMediaSource(uri, isLocalResource);
        if (mediaSource == null || player == null) {
            return;
        }

        if (handlerInner == null) {
            handlerInner = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Message msgOuter = new Message();
                    if (msg.what == WHAT_POSITION) {
                        currentPosition = player.getCurrentPosition() / 1000;
                        contentPosition = player.getContentPosition() / 1000;
                        contentBufferedPosition = player.getContentBufferedPosition() / 1000;
                        Log.d(TAG, "-----> currentPosition:" + currentPosition + " contentPosition:" + contentPosition + " contentBufferedPosition:" + contentBufferedPosition);
                        HashMap<String, Long> hashMap = new HashMap<>();
                        hashMap.put("currentPosition", currentPosition);
                        hashMap.put("contentPosition", contentPosition);
                        hashMap.put("contentBufferedPosition", contentBufferedPosition);
                        msgOuter.what = WHAT_POSITION;
                        msgOuter.obj = hashMap;
                        handler.sendMessage(msgOuter);
                        if (currentPosition < duration) {
                            sendEmptyMessageDelayed(WHAT_POSITION, 300);
                        }
                    } else if (msg.what == WHAT_DURATION) {
                        msgOuter.obj = msg.obj;
                        handler.sendMessage(msgOuter);
                    }
                }
            };
        }

        mediaSource.addEventListener(handlerInner, new MediaSourceEventListener() {

            @Override
            public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                Log.d(TAG, "onLoadStarted ---> duration:" + duration);
            }

            @Override
            public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                mediaSource.removeEventListener(this);

                //发送时长消息
                duration = player.getDuration() / 1000;
                Message msg = new Message();
                msg.what = WHAT_DURATION;
                msg.obj = duration;
                handlerInner.sendMessage(msg);

                //发送position消息
                Message msgPos = new Message();
                msgPos.what = WHAT_POSITION;
                handlerInner.sendMessage(msgPos);

                Log.d(TAG, "onLoadCompleted ---> duration:" + duration);
            }
        });
        player.setMediaSource(mediaSource);
        player.addListener(new Player.Listener() {

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Log.d(TAG, "onPlayWhenReadyChanged---> playWhenReady:" + playWhenReady);
                AudioPlayer.this.playWhenReady = playWhenReady;
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Log.d(TAG, "onPlaybackStateChanged---> playbackState:" + playbackState);
                switch (playbackState) {
                    case Player.STATE_READY:
                        Log.d(TAG, "STATE_READY");
                        break;
                    case Player.STATE_BUFFERING:
                        Log.d(TAG, "STATE_BUFFERING");
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "STATE_ENDED");
                        audioStatus = AudioPlayStatus.AUDIO_STOP;
                        break;
                    case Player.STATE_IDLE:
                        Log.d(TAG, "STATE_IDLE");
                        audioStatus = AudioPlayStatus.AUDIO_IDLE;
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Log.d(TAG, "onIsPlayingChanged---> isPlaying:" + isPlaying);
                if (isPlaying) {
                    audioStatus = AudioPlayStatus.AUDIO_START;
                } else {

                }
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onIsLoadingChanged---> isLoading:" + isLoading);
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Log.d(TAG, "onPositionDiscontinuity---> oldPosition:" + oldPosition + " newPosition:" + newPosition);
            }


            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG, "onPlayerError---> onPlayerError:" + error != null ? error.toString() : "");
            }

            @Override
            public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
                Log.d(TAG, "audioAttributes --- > " + audioAttributes.toString());

            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Log.d(TAG, "mediaMetadata --- > title:" + mediaMetadata.title);
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Log.d(TAG, "timeLine --- > " + timeline.toString() + " reason:" + reason);
            }
        });

        player.prepare();
    }

    public long getDuration() {
        return duration;
    }

    public Player getPlayer() {
        return player;
    }

    public void play() {
        Log.d(TAG, "play");
        if (player == null) {
            return;
        }
        if (playWhenReady) {
            if (!player.isPlaying()) {
                player.setPlayWhenReady(true);
            }
        } else {
            player.prepare();
            player.setPlayWhenReady(true);
        }
        audioStatus = AudioPlayStatus.AUDIO_START;
    }

    public void pause() {
        Log.d(TAG, "pause");
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        }
        audioStatus = AudioPlayStatus.AUDIO_PAUSE;
    }

    public void stop() {
        Log.d(TAG, "stop");
        if (player == null) {
            return;
        }
        player.stop();
        audioStatus = AudioPlayStatus.AUDIO_STOP;
    }

    public void cancel() {
        Log.d(TAG, "cancel");
    }

    public void release() {
        Log.d(TAG, "release");
        if (player == null) {
            return;
        }
        player.release();
        if (handlerInner != null) {
            handlerInner.removeCallbacksAndMessages(null);
        }
        audioStatus = AudioPlayStatus.AUDIO_RELEASE;
    }
}
