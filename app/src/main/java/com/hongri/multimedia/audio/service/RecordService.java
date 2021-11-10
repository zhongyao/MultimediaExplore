package com.hongri.multimedia.audio.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.hongri.multimedia.audio.AudioRecorder;
import com.hongri.multimedia.audio.FileUtil;
import com.hongri.multimedia.audio.RecordStreamListener;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.audio.state.Status;
import com.hongri.multimedia.util.Logger;

/**
 * Create by zhongyao on 2021/8/30
 * Description: 录音服务
 */
public class RecordService extends Service {

    private static final String TAG = RecordService.class.getSimpleName();

    private final static String ACTION_NAME = "action_type";

    private final static int ACTION_INVALID = 0;

    private final static int ACTION_START_RECORD = 1;

    private final static int ACTION_STOP_RECORD = 2;

    private final static int ACTION_RESUME_RECORD = 3;

    private final static int ACTION_PAUSE_RECORD = 4;

    private final static int ACTION_CANCEL_RECORD = 5;

    private final static int ACTION_RELEASE_RECORD = 6;

    private final static int ACTION_CREATE_DEFAULT_RECORD = 10;

    private final static String PARAM_PATH = "path";

    private final static String STREAM_LISTENER = "stream_listener";
    private static RecordStreamListener streamListener;


    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(ACTION_NAME)) {
            switch (bundle.getInt(ACTION_NAME, ACTION_INVALID)) {
                case ACTION_CREATE_DEFAULT_RECORD:
                    doCreateDefaultAudio(bundle.getString(PARAM_PATH));
                    break;
                case ACTION_START_RECORD:
                    doStartRecording(bundle.getString(PARAM_PATH));
                    break;
                case ACTION_STOP_RECORD:
                    doStopRecording();
                    break;
                case ACTION_RESUME_RECORD:
                    doResumeRecording();
                    break;
                case ACTION_PAUSE_RECORD:
                    doPauseRecording();
                    break;
                case ACTION_CANCEL_RECORD:
                    doCancelRecording();
                    break;
                case ACTION_RELEASE_RECORD:
                    doReleaseRecording();
                    break;
                default:
                    break;
            }
            return START_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public static void createDefaultAudio(Context context, String fileName) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_CREATE_DEFAULT_RECORD);
        intent.putExtra(PARAM_PATH, FileUtil.getFilePath());
        context.startService(intent);
    }

    //TODO listener 处理 , getFilePath
    public static void startRecording(Context context, RecordStreamListener listener) {
        streamListener = listener;
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_START_RECORD);
        intent.putExtra(PARAM_PATH, FileUtil.getFilePath());
        context.startService(intent);
    }

    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_STOP_RECORD);
        context.startService(intent);
    }

    public static void cancelRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_CANCEL_RECORD);
        context.startService(intent);
    }

    public static void resumeRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_RESUME_RECORD);
        context.startService(intent);
    }

    public static void pauseRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_PAUSE_RECORD);
        context.startService(intent);
    }

    public static void releaseRecord(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_RELEASE_RECORD);
        context.startService(intent);
    }

    /**
     * 改变录音格式
     */
//    public static boolean changeFormat(RecordConfig.RecordFormat recordFormat) {
//        if (getState() == RecordHelper.RecordState.IDLE) {
//            currentConfig.setFormat(recordFormat);
//            return true;
//        }
//        return false;
//    }

    /**
     * 改变录音配置
     */
//    public static boolean changeRecordConfig(RecordConfig recordConfig) {
//        if (getState() == RecordHelper.RecordState.IDLE) {
//            currentConfig = recordConfig;
//            return true;
//        }
//        return false;
//    }
    public static void changeRecordDir(String recordDir) {
        getCurrentConfig().setRecordDir(recordDir);
    }

    /**
     * 获取当前的录音状态
     */
    public static Status getState() {
        return AudioRecorder.getInstance().getStatus();
    }
//
//    public static void setRecordStateListener(RecordStateListener recordStateListener) {
//        RecordHelper.getInstance().setRecordStateListener(recordStateListener);
//    }
//
//    public static void setRecordDataListener(RecordDataListener recordDataListener) {
//        RecordHelper.getInstance().setRecordDataListener(recordDataListener);
//    }
//
//    public static void setRecordSoundSizeListener(RecordSoundSizeListener recordSoundSizeListener) {
//        RecordHelper.getInstance().setRecordSoundSizeListener(recordSoundSizeListener);
//    }
//
//    public static void setRecordResultListener(RecordResultListener recordResultListener) {
//        RecordHelper.getInstance().setRecordResultListener(recordResultListener);
//    }
//
//    public static void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
//        RecordHelper.getInstance().setRecordFftDataListener(recordFftDataListener);
//    }

    private void doCreateDefaultAudio(String path) {
        AudioRecorder.getInstance().createDefaultAudio(path);
    }

    private void doStartRecording(String path) {
        Logger.v(TAG, "doStartRecording path: %s", path);
        AudioRecorder.getInstance().startRecord(streamListener);
    }

    //TODO
    private void doResumeRecording() {
        Logger.v(TAG, "doResumeRecording");
//        RecordHelper.getInstance().resume();
    }

    private void doPauseRecording() {
        Logger.v(TAG, "doResumeRecording");
        AudioRecorder.getInstance().pauseRecord();
    }

    private void doCancelRecording() {
        AudioRecorder.getInstance().cancel();
    }

    private void doStopRecording() {
        Logger.v(TAG, "doStopRecording");
        AudioRecorder.getInstance().stopRecord();
        stopSelf();
    }

    private void doReleaseRecording() {
        AudioRecorder.getInstance().releaseRecord();
    }

    public static RecordConfig getCurrentConfig() {
        return AudioRecorder.getInstance().getCurrentConfig();
    }

    public static void setCurrentConfig(RecordConfig currentConfig) {
        AudioRecorder.getInstance().setCurrentConfig(currentConfig);
    }
}
