package com.hongri.multimedia.audio;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

/**
 * Create by zhongyao on 2021/11/12
 * Description:感应 息屏/亮屏 模式
 */
public class SensorModeManager implements SensorEventListener {

    private final String TAG = "SensorModeManager";
    private SensorManager sensorManager;
    private Sensor sensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private SensorModeManager() {
    }

    public static class SensorModeManagerHolder {
        public static SensorModeManager instance = new SensorModeManager();
    }

    public static SensorModeManager getInstance() {
        return SensorModeManagerHolder.instance;
    }


    public void init(Context context) {
        if (context == null) {
            return;
        }
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, ":SensorMode");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];

        Log.d(TAG, "onSensorChanged ---> value:" + value);

        if (AudioPlayManager.isPlaying()) {
            //音频正在播放
            if (value == sensor.getMaximumRange()) {
                AudioModeManager.getInstance().setSpeakerOn(true);
                setScreenOn();
            } else {
                AudioModeManager.getInstance().setSpeakerOn(false);
                setScreenOff();
            }
        } else {
            if (value == sensor.getMaximumRange()) {
                AudioModeManager.getInstance().setSpeakerOn(true);
                setScreenOn();
            }
        }
    }

    private void setScreenOn() {
        if (wakeLock != null) {
            wakeLock.acquire(10*60*1000L /*10 minutes*/);
        }
    }

    private void setScreenOff() {
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
