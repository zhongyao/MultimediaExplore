package com.hongri.multimedia.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.hongri.multimedia.audio.fftlib.FftFactory;
import com.hongri.multimedia.audio.listener.RecordDataListener;
import com.hongri.multimedia.audio.listener.RecordFftDataListener;
import com.hongri.multimedia.audio.listener.RecordResultListener;
import com.hongri.multimedia.audio.listener.RecordSoundSizeListener;
import com.hongri.multimedia.audio.listener.RecordStateListener;
import com.hongri.multimedia.audio.mp3.Mp3EncodeThread;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.audio.state.AudioRecordStatus;
import com.hongri.multimedia.audio.wav.PcmToWav;
import com.hongri.multimedia.audio.wav.WavUtils;
import com.hongri.multimedia.util.ByteUtils;
import com.hongri.multimedia.util.FileUtil;
import com.hongri.multimedia.util.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by zhongyao on 2021/8/17
 * Description: 录音封装
 */
public class AudioRecorder {
    private static final String TAG = "AudioRecorder";
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    //录音对象
    private AudioRecord audioRecord;

    //录音状态
    private AudioRecordStatus audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_IDLE;

    //文件名
    private String fileName;

    //录音文件
    private List<String> filesName = new ArrayList<>();

    private AudioRecordThread recordThread;

    private Mp3EncodeThread mp3EncodeThread;
    private RecordStateListener recordStateListener;
    private RecordDataListener recordDataListener;
    private RecordSoundSizeListener recordSoundSizeListener;
    private RecordResultListener recordResultListener;
    private RecordFftDataListener recordFftDataListener;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private FftFactory fftFactory = new FftFactory(FftFactory.Level.Original);
    private File resultFile = null;
    private File tmpFile = null;
    private List<File> files = new ArrayList<>();

    /**
     * 录音配置
     */
    private RecordConfig currentConfig = new RecordConfig();

    public RecordConfig getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(RecordConfig recordConfig) {
        currentConfig = recordConfig;
    }

    /**
     * 类级的内部类，也就是静态类的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用时才会装载，从而实现了延迟加载
     */
    private static class AudioRecorderHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static final AudioRecorder instance = new AudioRecorder();
    }

    private AudioRecorder() {
        Log.d(TAG, "AudioRecorder");
        createDefaultAudio("");
    }

    public void setRecordStateListener(RecordStateListener recordStateListener) {
        this.recordStateListener = recordStateListener;
    }

    public void setRecordDataListener(RecordDataListener recordDataListener) {
        this.recordDataListener = recordDataListener;
    }

    public void setRecordSoundSizeListener(RecordSoundSizeListener recordSoundSizeListener) {
        this.recordSoundSizeListener = recordSoundSizeListener;
    }

    public void setRecordResultListener(RecordResultListener recordResultListener) {
        this.recordResultListener = recordResultListener;
    }

    public void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
        this.recordFftDataListener = recordFftDataListener;
    }

    public static AudioRecorder getInstance() {
        return AudioRecorderHolder.instance;
    }

    /**
     * 创建录音对象
     */
    public void createAudio(String fileName, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, channelConfig);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        this.fileName = fileName;
    }

    /**
     * 创建默认的录音对象
     *
     * @param fileName 文件名
     */
    public void createDefaultAudio(String fileName) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        this.fileName = fileName;
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_READY;
    }


    /**
     * 开始录音
     */
    public void startRecord() {
        if (TextUtils.isEmpty(fileName)) {
            fileName = FileUtil.getFilePath();
        }
        if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_IDLE) {
//            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_START) {
            throw new IllegalStateException("正在录音");
        }
        Log.d("AudioRecorder", "===startRecord===" + audioRecord.getState());

        resultFile = new File(fileName);

        String tempFilePath = FileUtil.getTempFilePath();

        tmpFile = new File(tempFilePath);

        //开启录制音频线程
        recordThread = new AudioRecordThread();
        recordThread.start();
    }

    private class AudioRecordThread extends Thread {

        public AudioRecordThread() {
            if (getCurrentConfig().getFormat() == RecordConfig.RecordFormat.MP3) {
                if (mp3EncodeThread == null) {
                    initMp3EncoderThread(bufferSizeInBytes);
                } else {
                    Logger.e(TAG, "mp3EncodeThread != null, 请检查代码");
                }
            }
        }

        @Override
        public void run() {
            super.run();
            switch (getCurrentConfig().getFormat()) {
                case MP3:
                    startMp3Recorder();
                    break;
                default:
                    startPcmRecorder();
                    break;
            }
        }

    }

    private void startMp3Recorder() {
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_START;
        notifyState();

        try {
            audioRecord.startRecording();
            short[] byteBuffer = new short[bufferSizeInBytes];

            while (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_START) {
                int end = audioRecord.read(byteBuffer, 0, byteBuffer.length);
                if (mp3EncodeThread != null) {
                    mp3EncodeThread.addChangeBuffer(new Mp3EncodeThread.ChangeBuffer(byteBuffer, end));
                }
                notifyData(ByteUtils.toBytes(byteBuffer));
            }
            audioRecord.stop();
        } catch (Exception e) {
            Logger.e(e, TAG, e.getMessage());
            notifyError("录音失败");
        }
        if (audioRecordStatus != AudioRecordStatus.AUDIO_RECORD_PAUSE) {
            audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_IDLE;
            notifyState();
            stopMp3Encoded();
        } else {
            Logger.d(TAG, "暂停");
        }
    }

    private void startPcmRecorder() {
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_START;
        notifyState();
        Logger.d(TAG, "开始录制 Pcm");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tmpFile);
            audioRecord.startRecording();
            byte[] byteBuffer = new byte[bufferSizeInBytes];

            while (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_START) {
                int end = audioRecord.read(byteBuffer, 0, byteBuffer.length);
                notifyData(byteBuffer);
                fos.write(byteBuffer, 0, end);
                fos.flush();
            }
            audioRecord.stop();
            files.add(tmpFile);
            if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_STOP) {
                makeFile();
            } else {
                Logger.i(TAG, "暂停！");
            }
        } catch (Exception e) {
            Logger.e(e, TAG, e.getMessage());
            notifyError("录音失败");
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (audioRecordStatus != AudioRecordStatus.AUDIO_RECORD_PAUSE) {
            audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_IDLE;
            notifyState();
            Logger.d(TAG, "录音结束");
        }
    }

    private void makeFile() {
        switch (currentConfig.getFormat()) {
            case MP3:
                return;
            case WAV:
                mergePcmFile();
                makeWav();
                break;
            case PCM:
                mergePcmFile();
                break;
            default:
                break;
        }
//        notifyFinish();
        Logger.i(TAG, "录音完成！ path: %s ； 大小：%s", resultFile.getAbsoluteFile(), resultFile.length());
    }

    /**
     * 添加Wav头文件
     */
    private void makeWav() {
        if (!FileUtil.isFile(resultFile) || resultFile.length() == 0) {
            return;
        }
        byte[] header = WavUtils.generateWavFileHeader((int) resultFile.length(), currentConfig.getSampleRate(), currentConfig.getChannelCount(), currentConfig.getEncoding());
        WavUtils.writeHeader(resultFile, header);
    }

    /**
     * 合并文件
     */
    private void mergePcmFile() {
        boolean mergeSuccess = mergePcmFiles(resultFile, files);
        if (!mergeSuccess) {
            notifyError("合并失败");
        }
    }

    /**
     * 合并Pcm文件
     *
     * @param recordFile 输出文件
     * @param files      多个文件源
     * @return 是否成功
     */
    private boolean mergePcmFiles(File recordFile, List<File> files) {
        if (recordFile == null || files == null || files.size() <= 0) {
            return false;
        }

        FileOutputStream fos = null;
        BufferedOutputStream outputStream = null;
        byte[] buffer = new byte[1024];
        try {
            fos = new FileOutputStream(recordFile);
            outputStream = new BufferedOutputStream(fos);

            for (int i = 0; i < files.size(); i++) {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(files.get(i)));
                int readCount;
                while ((readCount = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readCount);
                }
                inputStream.close();
            }
        } catch (Exception e) {
            Logger.e(e, TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        files.clear();
        return true;
    }

    private void stopMp3Encoded() {
        if (mp3EncodeThread != null) {
            mp3EncodeThread.stopSafe(new Mp3EncodeThread.EncordFinishListener() {
                @Override
                public void onFinish() {
//                    notifyFinish();
                    mp3EncodeThread = null;
                }
            });
        } else {
            Logger.e(TAG, "mp3EncodeThread is null, 代码业务流程有误，请检查！！ ");
        }
    }

    private void initMp3EncoderThread(int bufferSize) {
        try {
            mp3EncodeThread = new Mp3EncodeThread(new File(fileName), bufferSize);
            mp3EncodeThread.start();
        } catch (Exception e) {
            Logger.e(e, TAG, e.getMessage());
        }
    }

    private void notifyData(final byte[] data) {
        if (recordDataListener == null && recordSoundSizeListener == null && recordFftDataListener == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (recordDataListener != null) {
                    recordDataListener.onData(data);
                }

                if (recordFftDataListener != null || recordSoundSizeListener != null) {
                    byte[] fftData = fftFactory.makeFftData(data);
                    if (fftData != null) {
                        if (recordSoundSizeListener != null) {
                            recordSoundSizeListener.onSoundSize(getDb(fftData));
                        }

                        if (recordFftDataListener != null) {
                            recordFftDataListener.onFftData(fftData);
                        }
                    }
                }
            }
        });
    }

    private int getDb(byte[] data) {
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
     * 暂停录音
     */
    public void pauseRecord() {
        Log.d("AudioRecorder", "===pauseRecord===");
        if (audioRecordStatus != AudioRecordStatus.AUDIO_RECORD_START) {
            throw new IllegalStateException("没有在录音");
        } else {
            audioRecord.stop();
            audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_PAUSE;
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        Log.d("AudioRecorder", "===stopRecord===");
        if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_IDLE || audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_READY) {
            throw new IllegalStateException("录音尚未开始");
        } else {
            audioRecord.stop();
            audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_STOP;
            saveRecordFile();
        }
    }

    public void saveRecordFile() {
        //假如有暂停录音
        try {
            if (filesName.size() > 0) {
                List<String> filePaths = new ArrayList<>();
                for (String fileName : filesName) {
                    filePaths.add(FileUtil.getPcmFileAbsolutePath(fileName));
                }
                //清除
                filesName.clear();
                //将多个pcm文件转化为wav文件
                mergePCMFilesToWAVFile(filePaths);

            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * 销毁(释放)录音实例
     */
    public void releaseRecord() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_IDLE;
    }

    /**
     * 取消录音
     */
    public void cancel() {
//        filesName.clear();
//        fileName = null;
//        if (audioRecord != null) {
//            audioRecord.release();
//            audioRecord = null;
//        }

        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_CANCEL;
    }

    /**
     * 将pcm合并成wav
     *
     * @param filePaths
     */
    private void mergePCMFilesToWAVFile(final List<String> filePaths) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (PcmToWav.mergePCMFilesToWAVFile(filePaths, FileUtil.getWavFileAbsolutePath(fileName))) {
                    //操作成功
                } else {
                    //操作失败
                    Log.e("AudioRecorder", "mergePCMFilesToWAVFile fail");
                    throw new IllegalStateException("mergePCMFilesToWAVFile fail");
                }
                fileName = "";
            }
        }).start();
    }

    private void notifyState() {
        if (recordStateListener == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                recordStateListener.onStateChange(audioRecordStatus);
            }
        });

        if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_STOP || audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_PAUSE) {
            if (recordSoundSizeListener != null) {
                recordSoundSizeListener.onSoundSize(0);
            }
        }
    }

    private void notifyError(final String error) {
        if (recordStateListener == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                recordStateListener.onError(error);
            }
        });
    }

    /**
     * 获取录音对象的状态
     *
     * @return
     */
    public AudioRecordStatus getAudioRecordStatus() {
        return audioRecordStatus;
    }

}