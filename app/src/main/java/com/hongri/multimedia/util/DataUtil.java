package com.hongri.multimedia.util;

/**
 * Create by zhongyao on 2021/8/19
 * Description:
 */
public class DataUtil {

    private static final String TAG = "DataUtil";

    public static int getDb(byte[] data) {
        double sum = 0;
        double ave;
        int length = Math.min(data.length, 128);
        int offsetStart = 0;
        for (int i = offsetStart; i < length; i++) {
            sum += data[i] * data[i];
        }
        ave = sum / (length - offsetStart);
        return (int) (Math.log10(ave) * 20);
    }

    /**
     * 通过short数组获取到音量(分贝)值
     *
     * @param buffer
     * @return
     */
    public static double calculateVolumeByBytes(short[] buffer) {
        int volume = 0;
        for (int i = 0; i < buffer.length; i++) {
            //平方和除以数据总长度，得到音量大小
            volume += (buffer[i] * buffer[i]);
        }

        double mean = volume / (double) buffer.length;
        double db = 10 * Math.log10(mean);
        return db;
    }

    /**
     * 通过byte数组获取到音量(分贝)值
     *
     * @param buffer
     * @return
     */
    public static double calculateVolumeByBytes(byte[] buffer) {
        double sumVolume = 0.0;
        double avgVolume = 0.0;
        double volume = 0.0;
        for (int i = 0; i < buffer.length; i += 2) {
            int v1 = buffer[i] & 0xFF;
            int v2 = buffer[i + 1] & 0xFF;
            int temp = v1 + (v2 << 8);// 小端
            if (temp >= 0x8000) {
                temp = 0xffff - temp;
            }
            sumVolume += Math.abs(temp);
        }
        avgVolume = sumVolume / buffer.length / 2;
        volume = Math.log10(1 + avgVolume) * 10;
        return volume;
    }
}
