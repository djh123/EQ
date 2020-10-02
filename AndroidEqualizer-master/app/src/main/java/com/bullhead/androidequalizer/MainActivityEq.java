package com.bullhead.androidequalizer;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bullhead.equalizer.Settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;


public class MainActivityEq extends AppCompatActivity {
    private AudioTrack mAudioTrack;
    private static final int mSampleRateInHz = 44100;    //声道
    private static final int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSizeInBytes;
    FileInputStream mFileInputStream = null;

    SeekBar[] mSeekBarFinal = new SeekBar[10];
    static int mThemeColor = Color.parseColor("#B24242");
    LineChartView mChartView;
    LineSet mChartDataset;
    float[] mPoints;
    Paint mPaint;
    String [] mFcList = {"31.25", "62.5", "125", "250", "500", "1k", "2k", "4k", "8k", "16k"};
    int[] mCoef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_eq);
        mChartView = findViewById(R.id.lineChart);
        mPaint = new Paint();
        mChartDataset = new LineSet();
        mPoints = new float[10];
        mCoef =new int[10];
//        Native.nativeEqSelectCoeficients(12, 12, 12, 12, 12, 12, 12, 12, 12, 12);
//

        int upperEqualizerBandLevel = 12;
        int lowerEqualizerBandLevel = -12;
        for (short i = 0; i < 10; i++) {
            mCoef[i] = 12;
            final TextView frequencyHeaderTextView = new TextView(MainActivityEq.this);
            frequencyHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            frequencyHeaderTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextView.setTextColor(Color.parseColor("#FFFFFF"));
            frequencyHeaderTextView.setText(mFcList[i]+"Hz");

            LinearLayout seekBarRowLayout = new LinearLayout(MainActivityEq.this);
            seekBarRowLayout.setOrientation(LinearLayout.VERTICAL);

            TextView lowerEqualizerBandLevelTextView = new TextView(MainActivityEq.this);
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            lowerEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            lowerEqualizerBandLevelTextView.setText("-12" + "dB");

            TextView upperEqualizerBandLevelTextView = new TextView(MainActivityEq.this);
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            upperEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            upperEqualizerBandLevelTextView.setText("12" + "dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.weight = 1;

            SeekBar seekBar = new SeekBar(MainActivityEq.this);
            TextView textView = new TextView(MainActivityEq.this);
            switch (i) {
                case 0:
                    seekBar = findViewById(R.id.seekBar0);
                    textView = findViewById(R.id.textView0);
                    break;
                case 1:
                    seekBar = findViewById(R.id.seekBar1);
                    textView = findViewById(R.id.textView1);
                    break;
                case 2:
                    seekBar = findViewById(R.id.seekBar2);
                    textView = findViewById(R.id.textView2);
                    break;
                case 3:
                    seekBar = findViewById(R.id.seekBar3);
                    textView = findViewById(R.id.textView3);
                    break;
                case 4:
                    seekBar = findViewById(R.id.seekBar4);
                    textView = findViewById(R.id.textView4);
                    break;

                case 5:
                    seekBar = findViewById(R.id.seekBar5);
                    textView = findViewById(R.id.textView5);
                    break;

                case 6:
                    seekBar = findViewById(R.id.seekBar6);
                    textView = findViewById(R.id.textView6);
                    break;

                case 7:
                    seekBar = findViewById(R.id.seekBar7);
                    textView = findViewById(R.id.textView7);
                    break;

                case 8:
                    seekBar = findViewById(R.id.seekBar8);
                    textView = findViewById(R.id.textView8);
                    break;

                case 9:
                    seekBar = findViewById(R.id.seekBar9);
                    textView = findViewById(R.id.textView9);
                    break;
            }
            mSeekBarFinal[i] = seekBar;
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN));
            seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(mThemeColor, PorterDuff.Mode.SRC_IN));
            seekBar.setId(i);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);

            textView.setText(frequencyHeaderTextView.getText());
            textView.setTextColor(Color.WHITE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


            seekBar.setProgress(12);

            mPoints[i] = 12;

            mChartDataset.addPoint(frequencyHeaderTextView.getText().toString(), mPoints[i]);


            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    Log.d("tag","process value "+progress);
                    mCoef[seekBar.getId()] = progress;
                    Native.nativeEqSelectCoeficients(mCoef[0], mCoef[1], mCoef[2], mCoef[3], mCoef[4], mCoef[5], mCoef[6], mCoef[7], mCoef[8], mCoef[9]);
                    mPoints[seekBar.getId()] = progress;

                    mChartDataset.updateValues(mPoints);
                    mChartView.notifyDataUpdate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        mPaint.setColor(Color.parseColor("#555555"));
        mPaint.setStrokeWidth((float) (1.10 * Settings.ratio));


        mChartDataset.setColor(mThemeColor);
        mChartDataset.setSmooth(true);
        mChartDataset.setThickness(10);

        mChartView.setXAxis(false);
        mChartView.setYAxis(false);

        mChartView.setYLabels(AxisController.LabelPosition.NONE);
        mChartView.setXLabels(AxisController.LabelPosition.NONE);
        mChartView.setGrid(ChartView.GridType.NONE, 10, 25, mPaint);

        mChartView.setAxisBorderValues(0, 25);

        mChartView.addData(mChartDataset);
        mChartView.show();

//        Native.nativeInit("111");

        try {
            playPCM_STREAM();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void playPCM_STREAM() throws FileNotFoundException  {
        if (mAudioTrack != null){
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }

        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz,mChannelConfig,mAudioFormat);
        mAudioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder()
                        .setSampleRate(mSampleRateInHz)
                        .setEncoding(mAudioFormat)
                        .setChannelMask(mChannelConfig)
                        .build(),
                mBufferSizeInBytes,
                AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE
        );
        mAudioTrack.play();


        AssetFileDescriptor afd = MainActivityEq.this.getResources().openRawResourceFd(R.raw.mo);
        try {
            mFileInputStream = afd.createInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    byte[] buffer = new byte[mBufferSizeInBytes];
                    byte[] bufferProcess = new byte[mBufferSizeInBytes];
                    while(mFileInputStream.available() > 0){
                        int readCount = mFileInputStream.read(buffer); //一次次的读取
                        //检测错误就跳过
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION|| readCount == AudioTrack.ERROR_BAD_VALUE){
                            continue;
                        }
                        if (readCount != -1 && readCount != 0){
//                            Log.d("TAG","readCount");
                            Native.nativeEqProcess(buffer,bufferProcess,mBufferSizeInBytes,readCount);
                            //输出音频数据
                            mAudioTrack.write(bufferProcess,0,readCount); //一次次的write输出播放
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("TAG","STREAM模式播放完成");
            }
        }.start();

    }


}
