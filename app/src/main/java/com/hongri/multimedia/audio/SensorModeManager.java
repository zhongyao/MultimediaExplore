package com.hongri.multimedia.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

import com.hongri.multimedia.audio.listener.AudioScreenListener;
/**
 * Create by zhongyao on 2021/11/16
 * Description:感应及息屏/亮屏 模式管理类
 */
public class SensorModeManager implements SensorEventListener {

    private final String TAG = "SensorModeManager";
    private SensorManager sensorManager;
    private Sensor sensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private boolean hasInitSuccess;
    private AudioScreenListener screenListener;

    private SensorModeManager() {
    }

    public void setScreenListener(AudioScreenListener screenListener) {
        this.screenListener = screenListener;
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

        Log.d(TAG, "init ---> hasInitSuccess:" + hasInitSuccess);
        if (hasInitSuccess) {
            return;
        }

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, ":SensorMode");

        /* 注册屏幕唤醒时的广播 */
        IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        context.registerReceiver(mScreenOReceiver, mScreenOnFilter);

        /* 注册机器锁屏时的广播 */
        IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        context.registerReceiver(mScreenOReceiver, mScreenOffFilter);

        hasInitSuccess = true;
    }

    /**
     * 唤屏 / 息屏广播监听者
     */
    private BroadcastReceiver mScreenOReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("android.intent.action.SCREEN_ON")) {
                Log.d(TAG, "—— SCREEN_ON ——");
                if (screenListener != null) {
                    screenListener.screenChanged(true);
                }
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                Log.d(TAG ,"—— SCREEN_OFF ——");
                if (screenListener != null) {
                    screenListener.screenChanged(false);
                }

            }
        }
    };

    /**
     * 距离感应监听
     * @param event
     */
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

    /**
     * 设置亮屏时间
     */
    private void setScreenOn() {
        if (wakeLock != null) {
            wakeLock.acquire(10*60*1000L);
        }
    }

    /**
     * 设置息屏
     */
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
