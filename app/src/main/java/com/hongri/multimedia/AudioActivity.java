package com.hongri.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hongri.multimedia.audio.AudioModeManager;
import com.hongri.multimedia.audio.AudioPlayManager;
import com.hongri.multimedia.audio.AudioRecordManager;
import com.hongri.multimedia.audio.listener.RecordSoundSizeListener;
import com.hongri.multimedia.audio.listener.RecordStateListener;
import com.hongri.multimedia.audio.state.AudioPlayStatus;
import com.hongri.multimedia.util.AppUtil;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.audio.state.AudioRecordStatus;
import com.hongri.multimedia.audio.widget.RecordButton;
import com.hongri.multimedia.audio.widget.AudioPlayView;
import com.hongri.multimedia.audio.widget.AudioRecordView;

import java.util.List;
import java.util.Locale;

/**
 * 音频Activity：
 * https://bbs.huaweicloud.com/blogs/239037
 */
public class AudioActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "AudioActivity";
    private Button start, pause, cancel, stop;
    private AudioRecordView audioRecordView;
    private RecordButton recordBtn;
    private AudioPlayView audioPlayView;
    private boolean permissionGranted = false;
    private int phoneWidth;
    private TextView audioModeBtn;
    private RecordConfig recordConfig;
    private static String[] PERMISSION_ALL = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        phoneWidth = AppUtil.getPhoneWidth(this);

        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        cancel = findViewById(R.id.cancel);
        stop = findViewById(R.id.stop);
        audioRecordView = findViewById(R.id.recordLayout);
        recordBtn = findViewById(R.id.recordBtn);
        audioPlayView = findViewById(R.id.recordPlayView);
        audioModeBtn = findViewById(R.id.audioMode);

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);
        stop.setOnClickListener(this);
        audioRecordView.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        audioModeBtn.setOnClickListener(this);

        //初始化音频播放切换模式管理类
        AudioModeManager.getInstance().init(getApplication());
        //初始化感应息/亮屏模式管理类
//        SensorModeManager.getInstance().init(getApplication());

        initConfig();

        audioRecordView.setPhoneWidth(this, phoneWidth);
        audioRecordView.setRecordConfig(recordConfig);

        XXPermissions.with(this).permission(PERMISSION_ALL).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all) {
                    permissionGranted = true;
                    AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_PREPARE);
                }

            }
        });

        initListener();
    }

    private void initListener() {
        AudioRecordManager.getInstance().setRecordStateListener(new RecordStateListener() {
            @Override
            public void onStateChange(AudioRecordStatus state) {
                switch (state) {
                    case AUDIO_RECORD_IDLE:
                        Log.d(TAG, "status ---> STATUS_IDLE");
                        break;

                    case AUDIO_RECORD_PREPARE:
                        Log.d(TAG, "status ---> STATUS_READY");
                        break;

                    case AUDIO_RECORD_START:
                        Log.d(TAG, "status ---> STATUS_START");
                        break;

                    case AUDIO_RECORD_PAUSE:
                        Log.d(TAG, "status ---> STATUS_PAUSE");
                        break;

                    case AUDIO_RECORD_STOP:
                        Log.d(TAG, "status ---> STATUS_STOP");
                        break;

                    case AUDIO_RECORD_CANCEL:
                        Log.d(TAG, "status ---> STATUS_CANCEL");
                        break;

                    case AUDIO_RECORD_RELEASE:
                        Log.d(TAG, "status ---> STATUS_RELEASE");
                        break;
                }
            }

            @Override
            public void onError(String error) {

            }
        });
        AudioRecordManager.getInstance().setRecordSoundSizeListener(new RecordSoundSizeListener() {
            @Override
            public void onSoundSize(int soundSize) {
                Log.d(TAG, "onSoundSize:" + soundSize);
            }
        });

    }

    private void initConfig() {
        recordConfig = new RecordConfig();
        recordConfig.setEncodingConfig(AudioFormat.ENCODING_PCM_16BIT);
        recordConfig.setFormat(RecordConfig.RecordFormat.MP3);
//        recordConfig.setFormat(RecordConfig.RecordFormat.WAV);
        recordConfig.setSampleRate(16000);
        String recordDir = String.format(Locale.getDefault(), "%s/Record/zhongyao/",
                Environment.getExternalStorageDirectory().getAbsolutePath());
        recordConfig.setRecordDir(recordDir);
        AudioRecordManager.getInstance().setCurrentConfig(recordConfig);
    }

    @Override
    public void onClick(View v) {
        if (!permissionGranted) {
            Toast.makeText(AudioActivity.this, "请申请权限后再试", Toast.LENGTH_LONG).show();
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.start:
                AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_START);
                break;

            case R.id.pause:
                AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_PAUSE);
                break;

            case R.id.cancel:
                AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_CANCEL);
                break;

            case R.id.stop:
                AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_STOP);
                break;

            case R.id.recordLayout:

                break;
            case R.id.recordBtn:

                break;

            case R.id.audioMode:
                if (AudioModeManager.getInstance().isSpeakerOn()) {
                    //听筒
                    AudioModeManager.getInstance().setSpeakerOn(false);
                    audioModeBtn.setText("听筒");
                    Toast.makeText(this, "已切换至听筒模式", Toast.LENGTH_SHORT).show();
                } else {
                    //扬声器
                    AudioModeManager.getInstance().setSpeakerOn(true);
                    audioModeBtn.setText("扬声器");
                    Toast.makeText(this, "已切换至扬声器模式", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioPlayManager.setStatus(AudioPlayStatus.AUDIO_STOP);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioPlayManager.setStatus(AudioPlayStatus.AUDIO_STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_RELEASE);
        if (audioPlayView != null) {
            audioPlayView.onRelease();
        }
    }
}