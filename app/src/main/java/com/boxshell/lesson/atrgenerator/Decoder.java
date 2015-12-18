package com.boxshell.lesson.atrgenerator;

import android.util.Log;

/**
 * Created by yangjun on 15/12/14.
 */
public class Decoder {
    private final static String TAG = "ATRdecoder";
    private static String mData;

    private static State mState;
    private final static int mThreshold = 800;
    private enum State{
        IDLE,
        SYNC,
        END,
        STATEA,
        STATEB,
        STATEC
    };

    Decoder(){
        mData = "";

        mState = State.IDLE;
    }

    private boolean filterFrequency(short[]data, int len, int freq){
        int index = (int)Math.round(freq/Freq.FREQUENCY_DELTA);
        int i;


        for(i= -3;i< 0; i++){
            Log.d(TAG,Integer.toString(index + i) + ':' + Integer.toString(data[index + i])
                    + "  " + Integer.toString(freq));

        }
        for(i= 0;i< 4; i++){
            Log.d(TAG,Integer.toString(index + i) + ':' + Integer.toString(data[index + i])
                    + "  " + Integer.toString(freq));
        }
        Log.d(TAG,"E");

        if(data[index] > mThreshold){
            return true;
        }else{
            return false;
        }
    }

    public String decodeMessage(short[]data, int len){
        switch( mState ){
            case IDLE:
                Log.d(TAG, "<--- IDLE");
                if(filterFrequency(data,len, Freq.GTONE_HEAD)){
                    mState = State.SYNC;
                }

                break;
            case SYNC:
                Log.d(TAG, "<---- SYNC");
                if(filterFrequency(data,len,Freq.GTONE_A)){
                    mState = State.STATEA;
                }
                else if(filterFrequency(data,len,Freq.GTONE_HEAD)){
                    break;
                }
                else{
                    Log.d(TAG, "should never happen in SYNC");
                    mState = State.IDLE;
                }

                break;
            case STATEA:
                Log.d(TAG, "<---- stateA");
                if(filterFrequency(data,len,Freq.GTONE_AB)){
                    Log.d(TAG, "received 1");
                    mData += '1';
                    mState = State.STATEB;
                }else if(filterFrequency(data,len,Freq.GTONE_AC)){
                    mState = State.STATEC;
                    Log.d(TAG, "received 0");
                    mData += '0';
                }else if(filterFrequency(data,len,Freq.GTONE_TAIL)){
                    mState = State.END;
                }else if(filterFrequency(data, len, Freq.GTONE_A)){
                    break;
                }else if(filterFrequency(data, len, Freq.GTONE_CA)){
                    break;
                }else if(filterFrequency(data, len, Freq.GTONE_BA)){
                    break;
                }else{
                    Log.d(TAG, "Should never happen, STATEA");
                    mState = State.IDLE;
                }

                break;
            case STATEB:
                Log.d(TAG, "<---- stateB");
                if(filterFrequency(data, len, Freq.GTONE_BC)){
                    mState = State.STATEC;
                    mData += '1';
                    Log.d(TAG, "received 1");
                }else if(filterFrequency(data,len,Freq.GTONE_BA)){
                    mState = State.STATEA;
                    mData += '0';
                    Log.d(TAG, "received 0");
                }else if(filterFrequency(data,len,Freq.GTONE_TAIL)){
                    mState = State.END;
                }else if(filterFrequency(data, len, Freq.GTONE_CB)){
                    break;
                }else if(filterFrequency(data, len, Freq.GTONE_AB)){
                    break;
                }
                else{
                    Log.d(TAG, "Should never happen, STATEB");
                    mState = State.IDLE;
                }
                break;
            case STATEC:
                Log.d(TAG, "<---- stateC");
                if(filterFrequency(data, len, Freq.GTONE_CA)){
                    mState = State.STATEA;
                    mData += '1';
                    Log.d(TAG, "received 1");
                }else if(filterFrequency(data,len,Freq.GTONE_CB)){
                    mState = State.STATEB;
                    mData += '0';
                    Log.d(TAG, "received 0");
                }else if(filterFrequency(data,len,Freq.GTONE_TAIL)){
                    mState = State.END;
                }
                else if(filterFrequency(data, len, Freq.GTONE_AC)){
                    break;
                }else if(filterFrequency(data, len, Freq.GTONE_BC)){
                    break;
                }else{
                    Log.d(TAG, "Should never happen, STATEC");
                    mState = State.IDLE;
                }
                break;
            default:
                Log.d(TAG, "Should never happen in Decoder STATEC.");

        }
        // a character has been decoded
        if(mState == State.END){
            // handle it print it out
            Log.d(TAG,"=============================");
            Log.d(TAG, mData);
            Log.d(TAG,String.valueOf((char)Integer.parseInt(mData, 2)));


            Log.d(TAG,"=============================");

            mState = State.IDLE;
            String backupMData = mData;
            mData = "";
            return String.valueOf((char) Integer.parseInt(backupMData, 2));
        }
        else {
            return new String();
        }
    }

    /*//JNI code
    static {
        System.loadLibrary("spectrum");
    }

    public native short[] calcSpectrum(short[] sa, int size);*/
}
