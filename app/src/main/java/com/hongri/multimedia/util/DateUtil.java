package com.hongri.multimedia.util;

/**
 * Create by zhongyao on 2021/8/24
 * Description:
 */
public class DateUtil {
    /**
     * 将毫秒转时分秒
     *
     * @param time
     * @return
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02dh%02dm%02ds", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }
}
