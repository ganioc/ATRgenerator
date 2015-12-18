package com.boxshell.lesson.atrgenerator;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by yangjun on 15/12/14.
 */
public class Encoder {
    private final static String TAG = "ATRencoder";
    private static short[]mData;
    private static int mOffset;
    private static int mMaxSize;
    private enum State{
        STATEA,
        STATEB,
        STATEC
    };

    Encoder(int size){
        mData = new short[size];
        mMaxSize = size;
        mOffset = 0;
    }

    // send text out by sound
    public synchronized void  send(String text){

        Log.d(TAG, "Encoder send text");

        // split it to binary characters String[] Array
        /**
         * I will send one character at a time, 1024 resolution
         */
        String [] bText = createBinaryText(text);
        int i;

        mOffset = 0;

        for(i=0; i< bText.length; i++){
            // addToBuffer will handle "10100102" string
            // which will have a head and a tail
            addToBuffer(bText[i]);
        }

        AudioTrack mTrack = createTrack(mData, mOffset);
        mTrack.play();

    }
    private void addToBuffer(String b){
        Log.d(TAG, "into addToBuffer()");
        int i;
        short [] data;
        // add head
        createHead();
        //fillTone(data, Freq.PACKET_HEAD_LENGTH);
        // add body bits
        createBody(b);
        // add tail
        createTail();
    }
    private void createHead(){
        Log.d(TAG, Integer.toString(Freq.GTONE_HEAD));
        short[] data = createMultiTone(new int[]{Freq.GTONE_HEAD}, 1,
                Freq.PACKET_HEAD_LENGTH );
        fillTone(data, Freq.PACKET_HEAD_LENGTH );

    }
    private void createTail(){
        Log.d(TAG, Integer.toString(Freq.GTONE_TAIL));
        short[] data = createMultiTone(new int[]{Freq.GTONE_TAIL}, 1,
                Freq.PACKET_TAIL_LENGTH);
        fillTone(data, Freq.PACKET_TAIL_LENGTH);
    }
    private void createBody(String b){
        Log.d(TAG, "into createBody");
        int i;
        char[] charArray = b.toCharArray();
        short[] data;

        Log.d(TAG, Integer.toString(Freq.GTONE_A));
        data = createMultiTone(new int[]{Freq.GTONE_A}, 1,
                Freq.PACKET_DELTA ); // 1024
        fillTone(data, Freq.PACKET_DELTA); // 1024

        encode(b);
        //fillTone(data,data.length);
    }
    private void createBit(int[] freqs, int freqNum, int length){
        short[] data;
        data = createMultiTone(freqs, freqNum, length);
        fillTone(data, length);
    }
    private void encode(String b){
        short[] data;
        State state;
        state = State.STATEA;
        char [] charArray = b.toCharArray();

        // change binary string back to Android Readable string
        Log.d(TAG,"into encode:" + String.valueOf((char)Integer.parseInt(b, 2)));

        for(int i=0; i < charArray.length; i++){
            //Log.d(TAG, String.valueOf(charArray[i]));
            if(charArray[i] == '1'){
                Log.d(TAG, "一");
                switch (state){
                    case STATEA:
                        Log.d(TAG, Integer.toString(Freq.GTONE_AB));
                        createBit(new int[]{Freq.GTONE_AB}, 1,
                                Freq.PACKET_BIT_LENGTH);
                        state = State.STATEB;
                        break;
                    case STATEB:
                        Log.d(TAG, Integer.toString(Freq.GTONE_BC));
                        createBit(new int[]{Freq.GTONE_BC}, 1,
                                Freq.PACKET_BIT_LENGTH);
                        state = State.STATEC;
                        break;
                    case STATEC:
                        Log.d(TAG, Integer.toString(Freq.GTONE_CA));
                        createBit(new int[]{Freq.GTONE_CA}, 1,
                                Freq.PACKET_BIT_LENGTH);
                        state = State.STATEA;
                        break;
                    default:
                        Log.d(TAG, "Should never happen in encode");
                        break;
                }
            }else{
                Log.d(TAG, "零");
                switch (state){
                    case STATEA:
                        Log.d(TAG, Integer.toString(Freq.GTONE_AC));
                        createBit(new int[]{Freq.GTONE_AC},1,
                                Freq.PACKET_BIT_LENGTH);
                        state = State.STATEC;
                        break;
                    case STATEB:
                        Log.d(TAG, Integer.toString(Freq.GTONE_BA));
                        createBit(new int[]{Freq.GTONE_BA},1,
                                Freq.PACKET_BIT_LENGTH);
                        state = State.STATEA;
                        break;
                    case STATEC:
                        Log.d(TAG, Integer.toString(Freq.GTONE_CB));
                        createBit(new int[]{Freq.GTONE_CB},1,
                                Freq.PACKET_BIT_LENGTH);
                        state = State.STATEB;
                        break;
                    default:
                        Log.d(TAG, "Should never happen in encode");
                        break;
                }
            }
        }
    }
    private short[] createMultiTone(int []freqs, int freq_num, int num){
        short[] data = new short[num];
        float[] constVariableLst = new float[freq_num];
        short sample;
        int i, j;
        float temp;

        for(i = 0; i< freq_num; i++){
            constVariableLst[i] = (float)(2*Math.PI * freqs[i]/ Freq.SAMPLE_RATE_1);
        }

        for(i=0; i< num/2; i++){
            temp = 0;
            for(j=0; j< freq_num; j++){
                temp += Math.sin(i * constVariableLst[j]) ;
            }
            sample = (short)((temp/ freq_num)*0x7fff);
            data[i*2] = sample;
            data[i*2 + 1] = sample;
        }
        return data;

    }

    private AudioTrack createTrack(short[] data, int num) {
        AudioTrack track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                Freq.SAMPLE_RATE_1,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                num * 2,
                AudioTrack.MODE_STATIC
        );
        track.write(data, 0, num);
        return track;
    }
    private void fillTone(short[] src, int len) {
        if ((mOffset + len) > mMaxSize) {
            Log.d(TAG, "Out of buffer bound");
            mOffset = 0;
            return;
        }

        for (int i = 0; i < len; i++) {
            mData[mOffset + i] = src[i];
        }

        mOffset += len;
    }

    private String[] createBinaryText(String text){
        char[] charArray = text.toCharArray();
        String [] bFormData = new String[charArray.length];
        int i;
        for(i=0; i< charArray.length; i++){
            bFormData[i] = Integer.toBinaryString(charArray[i]);
        }
        return bFormData;
    }

    private short[] createPacket(String str){

        Log.d(TAG, "Into Encoder::createPacket()");
        //Log.d(TAG, "str length is:" + str.length());

        char[] charArray = str.toCharArray();

        short[] data= new short[charArray.length];  // short list to be sent
        String [] bFormData = new String[charArray.length];
        int i;

        Log.d(TAG, Integer.toString(charArray.length));

        Log.d(TAG, "to binary form:");
        for(i=0; i< charArray.length; i++){
            Log.d(TAG, String.valueOf(charArray[i]));
            //Log.d(TAG, Integer.toString(charArray[i]));
            Log.d(TAG, Integer.toBinaryString(charArray[i]));
            bFormData[i] = Integer.toBinaryString(charArray[i]);
            Log.d(TAG, Integer.toString(bFormData[i].length()));


        }

        Log.d(TAG, "to string form:");
        for(i=0; i< charArray.length; i++){
            Log.d(TAG, bFormData[i]);

            Log.d(TAG, String.valueOf((char)Integer.parseInt(bFormData[i], 2)));
            //String str = new
        }



        return data;
    }
}
