package com.boxshell.lesson.atrgenerator;

import android.util.Log;

/**
 * Created by yangjun on 15/12/14.
 */
public class AudioQueue {
    private static final String TAG = "ATRaudioqueue";
    private static short[] mQueue;
    private static int mSize;
    private static int mHeader, mTail;

    AudioQueue(int size) {
        mSize = size;
        mQueue = new short[size];
        mHeader = 0;
        mTail = 0;
    }

    public static void reset() {
        mHeader = 0;
        mTail = 0;
    }

    // this is the producer
    public synchronized void add(short[] in, int size) {
        for (int i = 0; i < size; i++) {
            if (mTail < mSize) {
                mQueue[mTail] = in[i];

            }// mTail is larger than the queue size
            else if (mTail == mSize) {
                mTail = 0;
                mQueue[mTail] = in[i];
            }
            else{
                Log.d(TAG, "Will never happen in AudioQueue");
            }
            mTail++;
        }
        notifyAll();
    }
    // this is the consumer
    public synchronized short[] readBlocking(int size) {
        while (getSize() < size) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        short[] buf = new short[size];

        if (mTail > mHeader) {
            for (int i = 0; i < size; i++) {
                buf[i] = mQueue[mHeader];
                mHeader++;
            }
        } else {
            int nFirstPart = (mSize - mHeader);
            //MainActivity.L("nFirstPart is:" + Integer.toString(nFirstPart));

            if(nFirstPart<= size) {

                for (int i = 0; i < nFirstPart; i++) {
                    buf[i] = mQueue[mHeader];
                    mHeader++;
                }
                mHeader = 0;

                for (int i = 0; i < (size - nFirstPart); i++) {
                    buf[nFirstPart + i] = mQueue[mHeader];
                    mHeader++;
                }
            }
            else{
                for (int i = 0; i < size; i++) {
                    buf[i] = mQueue[mHeader];
                    mHeader++;
                }
            }
            //MainActivity.L("mHeader is:" + Integer.toString(mHeader));
        }

        return buf;
    }

    public synchronized short[] readNoBlocking(int size) {
        int nGetSize = getSize();

        if(nGetSize < size) {
           return new short[]{};
        }else{
            Log.d(TAG, "getSize():" + nGetSize);
        }

        Log.d(TAG,"into readNoBlocking();");

        short[] buf = new short[size];

        if (mTail > mHeader) {
            for (int i = 0; i < size; i++) {
                buf[i] = mQueue[mHeader];
                mHeader++;
            }
        } else {
            int nFirstPart = (mSize - mHeader);
            //MainActivity.L("nFirstPart is:" + Integer.toString(nFirstPart));

            if(nFirstPart<= size) {

                for (int i = 0; i < nFirstPart; i++) {
                    buf[i] = mQueue[mHeader];
                    mHeader++;
                }
                mHeader = 0;

                for (int i = 0; i < (size - nFirstPart); i++) {
                    buf[nFirstPart + i] = mQueue[mHeader];
                    mHeader++;
                }
            }
            else{
                for (int i = 0; i < size; i++) {
                    buf[i] = mQueue[mHeader];
                    mHeader++;
                }
            }
            //MainActivity.L("mHeader is:" + Integer.toString(mHeader));
        }
        Log.d(TAG,"out of readNoBlocking()");
        return buf;
    }

    public int getSize() {

        if (mTail > mHeader) {
            //Log.d(TAG, "getSize() " + Integer.toString(mTail - mHeader));
            return mTail - mHeader;
        }
        else if(mTail < mHeader){
            //Log.d(TAG, "getSize() " + Integer.toString(mTail + mSize - mHeader));
            return (mTail + mSize - mHeader);
        }else {
            //Log.d(TAG, "getSize() " + Integer.toString(0));
            return 0;
        }
    }


}
