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
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MainActivityReverb extends AppCompatActivity {
    private AudioTrack mAudioTrack;
    private int mSampleRateInHz = 16000;    //声道
    private int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSizeInBytes;
    FileInputStream fileInputStream = null;

    SeekBar[] seekBarFinal = new SeekBar[7];
    static int themeColor = Color.parseColor("#B24242");
    LineChartView mChart;
    LineSet mChartDataset;
    float[] points;
    Paint mPaint;
    String [] ParamList = {"RoomSize", "PreDelay", "FeedBack", "Damping", "WetGain", "DryGain", "WetOnly"};
    int[] mParam;
    AssetFileDescriptor mPlayFileFd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rb);


        //RoomSize 0~100 su 0
        //PreDelay 0~200 su 10
        //FeedBack 0~100 su 65
        //Damping 0~100 su 25
        //WetGain -10~20 su -1
        //DryGain -10~20 su -1
        //WetOnly FALSE/TRUE FALSE
        Native.nativeRbSetParam(0, 10, 65, 25, -1, -1, true);
        mChart = findViewById(R.id.lineChart);
        mPaint = new Paint();
        mChartDataset = new LineSet();
        points = new float[7];
        mParam =new int[7];

        for (short i = 0; i < 7; i++) {
            String param = ParamList[i];
            final TextView frequencyHeaderTextView = new TextView(MainActivityReverb.this);
            frequencyHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            frequencyHeaderTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextView.setTextColor(Color.parseColor("#FFFFFF"));
            frequencyHeaderTextView.setText(ParamList[i]+"");

            LinearLayout seekBarRowLayout = new LinearLayout(MainActivityReverb.this);
            seekBarRowLayout.setOrientation(LinearLayout.VERTICAL);

            TextView lowerEqualizerBandLevelTextView = new TextView(MainActivityReverb.this);
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            lowerEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            lowerEqualizerBandLevelTextView.setText("" + "");

            TextView upperEqualizerBandLevelTextView = new TextView(MainActivityReverb.this);
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            upperEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            upperEqualizerBandLevelTextView.setText("" + "");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.weight = 1;

            SeekBar seekBar = new SeekBar(MainActivityReverb.this);
            TextView textView = new TextView(MainActivityReverb.this);

            if (param.equals("RoomSize")) {
                mParam[i] = 0;
                seekBar = findViewById(R.id.seekBar0);
                textView = findViewById(R.id.textView0);
                seekBar.setMax(100);
                seekBar.setProgress(0);
                points[i] = 0;

            } else if (param.equals("PreDelay")) {
                mParam[i] = 10;
                seekBar = findViewById(R.id.seekBar1);
                textView = findViewById(R.id.textView1);
                seekBar.setMax(200);
                seekBar.setProgress(10);
                points[i] = 10;

            } else if (param.equals("FeedBack")) {
                mParam[i] = 65;
                seekBar = findViewById(R.id.seekBar2);
                textView = findViewById(R.id.textView2);
                seekBar.setMax(100);
                seekBar.setProgress(65);
                points[i] = 65;
            } else if (param.equals("Damping")) {
                mParam[i] = 25;
                seekBar = findViewById(R.id.seekBar3);
                textView = findViewById(R.id.textView3);
                seekBar.setMax(100);
                seekBar.setProgress(25);
                points[i] = 25;
            } else if (param.equals("WetGain")) {
                mParam[i] = -1;
                seekBar = findViewById(R.id.seekBar4);
                textView = findViewById(R.id.textView4);
                seekBar.setMax(20 - (-10));
                seekBar.setProgress(10+(-1));
                points[i] = 10 + (-1);
            } else if (param.equals("DryGain")) {
                mParam[i] = -1;
                seekBar = findViewById(R.id.seekBar5);
                textView = findViewById(R.id.textView5);
                seekBar.setProgress(10+(-1));
                points[i] = 10 + (-1);
            } else if (param.equals("WetOnly")) {
                mParam[i] = 0;
                seekBar = findViewById(R.id.seekBar6);
                textView = findViewById(R.id.textView6);
                seekBar.setMax(2);
                seekBar.setProgress(0);
                points[i] = 0;

            }


            seekBarFinal[i] = seekBar;
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN));
            seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(themeColor, PorterDuff.Mode.SRC_IN));
            seekBar.setId(i);

            textView.setText(frequencyHeaderTextView.getText());
            textView.setTextColor(Color.WHITE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


            mChartDataset.addPoint(frequencyHeaderTextView.getText().toString(), points[i]);


            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    Log.d("tag","process value "+progress);

                    String getParam = ParamList[seekBar.getId()];
                    points[seekBar.getId()] = progress;

                    if (getParam.equals("RoomSize")) {
                        mParam[seekBar.getId()] = progress;
                    } else if (getParam.equals("PreDelay")) {

                        mParam[seekBar.getId()] = progress;

                    } else if (getParam.equals("FeedBack")) {

                        mParam[seekBar.getId()] = progress;

                    } else if (getParam.equals("Damping")) {
                        mParam[seekBar.getId()] = progress;

                    } else if (getParam.equals("WetGain")) {
                        mParam[seekBar.getId()] = progress - 10;

                    } else if (getParam.equals("DryGain")) {
                        mParam[seekBar.getId()] = progress - 10;
                    } else if (getParam.equals("WetOnly")) {
                        mParam[seekBar.getId()] = progress;

                    }
                    Boolean wetOnly = Boolean.FALSE;
                    if (mParam[6] > 0) {
                        wetOnly = Boolean.TRUE;
                    }
//                    Native.nativeRbSetParam(mParam[0], mParam[1], mParam[2], mParam[3], mParam[4], mParam[5], wetOnly);

                    mChartDataset.updateValues(points);
                    mChart.notifyDataUpdate();
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


        mChartDataset.setColor(themeColor);
        mChartDataset.setSmooth(true);
        mChartDataset.setThickness(10);

        mChart.setXAxis(false);
        mChart.setYAxis(false);

        mChart.setYLabels(AxisController.LabelPosition.NONE);
        mChart.setXLabels(AxisController.LabelPosition.NONE);
        mChart.setGrid(ChartView.GridType.NONE, 100, 100, mPaint);

        mChart.setAxisBorderValues(0, 200);

        mChart.addData(mChartDataset);
        mChart.show();




        findViewById(R.id.replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayFileFd = MainActivityReverb.this.getResources().openRawResourceFd(R.raw.djhshiliukhz);
                mSampleRateInHz = 16000;    //声道

                try {
                    playPCM_STREAM();
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });



        Native.nativeInit("111");
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

        try {
            fileInputStream = mPlayFileFd.createInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    byte[] buffer = new byte[mBufferSizeInBytes];
                    byte[] bufferProcess = new byte[mBufferSizeInBytes];
                    while(fileInputStream.available() > 0){
                        int readCount = fileInputStream.read(buffer); //一次次的读取
                        //检测错误就跳过
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION|| readCount == AudioTrack.ERROR_BAD_VALUE){
                            continue;
                        }
                        if (readCount != -1 && readCount != 0){
//                            Log.d("TAG","readCount");
                            Native.nativeRbProcess(buffer,bufferProcess,mBufferSizeInBytes,readCount);
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
