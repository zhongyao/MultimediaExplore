package com.hongri.multimedia.audio.mp3;

/**
 * Create by zhongyao on 2021/8/30
 * Description: MP3音频native编解码
 */
public class Mp3Encoder {

    static {
        System.loadLibrary("mp3lame");
    }

    public native static void close();

    public native static int encode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf);

    public native static int flush(byte[] mp3buf);

    public native static void init(int inSampleRate, int outChannel, int outSampleRate, int outBitrate, int quality);

    public static void init(int inSampleRate, int outChannel, int outSampleRate, int outBitrate) {
        init(inSampleRate, outChannel, outSampleRate, outBitrate, 7);
    }
}