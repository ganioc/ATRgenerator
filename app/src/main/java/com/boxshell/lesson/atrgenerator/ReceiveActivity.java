package com.boxshell.lesson.atrgenerator;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.logging.Handler;


public class ReceiveActivity extends AppCompatActivity {
    private final static String TAG = "ATRreceive";
    private static TextView mText;
    private static Decoder mDecoder;
    private static AudioQueue mAudioQueue;
    private Thread mThread;
    private static boolean mRunning;
    private static short[] mData;
    private static String strFb;
    private static String mReceivedText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        mReceivedText = "";
        mText = (TextView) findViewById(R.id.textReceived);

        mText.setText(mReceivedText);

        Log.d(TAG, "Into receive.");

        mDecoder = new Decoder();
        mData = new short[Freq.SAMPLE_RATE_1* 20];
        mAudioQueue = new AudioQueue(Freq.SAMPLE_RATE_1* 20);

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,"Start the analyze thread.");
                startAnalyze();
            }
        });

        mRunning = true;
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        //mRunning = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRunning = false;
    }

    @Override
    protected void onStop() {

        super.onStop();
        mRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startAnalyze();
            }
        });

        mRunning = true;
        mThread.start();

    }

    // main thread loop function
    private void startAnalyze(){
        int minBufferSize = AudioRecord.getMinBufferSize(
                Freq.SAMPLE_RATE_1,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        int numberOfShort;
        short[] mOutBuffer;


        Log.d(TAG, "Into startAnalyze(), minBufferSize:" + Integer.toString(minBufferSize));

        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                Freq.SAMPLE_RATE_1,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize);

        audioRecord.startRecording();

        while(mRunning){
            // this is with blocking
            numberOfShort = audioRecord.read(mData, 0 , minBufferSize);

            mAudioQueue.add(mData, numberOfShort);
            //

            if(numberOfShort <= 0){
                continue;
            }else{
                Log.d(TAG, "Received:" + Integer.toString(numberOfShort));
            }

            while (true){
                short[]data = mAudioQueue.readNoBlocking(Freq.PACKET_DELTA);
                //Log.d(TAG, "data.length=" + Integer.toString(data.length));
                if(data.length == 0){
                    break;
                }
                else{
                    mOutBuffer = calcSpectrum(data, Freq.PACKET_DELTA);
                    strFb = mDecoder.decodeMessage(mOutBuffer, Freq.PACKET_DELTA / 2);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!strFb.isEmpty()){
                                addToReceivedText(strFb);
                            }

                        }
                    });

                }
            }
        }

        audioRecord.stop();
        audioRecord.release();

        Log.d(TAG, "Out of startAnalyze()");

    }

    private void addToReceivedText(String str){
        mReceivedText = mReceivedText + str;
        mText.setText(mReceivedText);
    }
    //JNI code
    static {
        System.loadLibrary("spectrum");
    }

    public native short[] calcSpectrum(short[] sa, int size);

}
