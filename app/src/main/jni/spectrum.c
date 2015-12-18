
#include "spectrum.h"

/*JNIEXPORT jint  Java_com_boxshell_lesson_argenerator_MainActivity_addTwo(JNIEnv* env, jobject thiz,
    jint a, jint b){

    return add(a, b);
}*/

/*JNIEXPORT jshortArray Java_com_boxshell_lesson_atrgenerator_ReceiveActivity_calcSpectrum(JNIEnv* pEnv, jobject thiz,
        jshortArray sa , jint size){
    return calcSpectrum(pEnv, thiz,
            sa ,size);
}

JNIEXPORT jshortArray Java_com_boxshell_lesson_atrgenerator_Decoder_calcSpectrum(JNIEnv* pEnv, jobject thiz,
                                                                                         jshortArray sa , jint size){
    return calcSpectrum(pEnv, thiz,
                                                                           sa ,size);
}*/

jshortArray Java_com_boxshell_lesson_atrgenerator_ReceiveActivity_calcSpectrum(JNIEnv* pEnv, jobject thiz,
    jshortArray sa , jint size){

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "......into calcSpectrum");

    /*#ifdef FIXED_POINT
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "fixed point");
    #else
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "float point");
    #endif*/

    float hanning[size];
    kiss_fft_scalar zero;
    int nfft = size;  // fft length

    int i, max_index;
    float min=0 , max=0;
    short nativeA[size];  //input is short 16-bit
    short nativeOutputA[size];  // output is also 16-bit, here I use decimal input
    jshortArray outA;

    kiss_fft_cpx cx_in[nfft];
    kiss_fft_cpx cx_out[nfft];
    kiss_fft_cfg cfg;

    //kiss_fft_scalar rin[nfft+ 2];
    //kiss_fft_scalar rout[nfft+2];
    //kiss_fft_cpx sout[nfft];

    int BIG_SHORT = 32767;


    // initialize hanning window
    for(i=0;i<nfft;i++){
        hanning[i] = (1 - cos(i*2*pi /(nfft-1)))* 0.5;
    }

    outA = (*pEnv)->NewShortArray(pEnv, size);

    cfg = kiss_fft_alloc( nfft ,0 , 0 , 0 );

    // get input short array value
    (*pEnv)->GetShortArrayRegion(pEnv, sa, 0, size, nativeA);

    // input
    for(i=0; i < nfft; i++){
        cx_in[i].r = (float) nativeA[i]*hanning[i];
        cx_in[i].i = 0;
/*        if(cx_in[i].r > 1.0 || cx_in[i].r < -1.0){
            __android_log_print(ANDROID_LOG_DEBUG, TAG, "Input Larger than 1 %f", cx_in[i].r);
        }*/


        if(cx_in[i].r > max){
            max = cx_in[i].r;

        }

        if(cx_in[i].r < min){
            min = cx_in[i].r;
        }

        //rin[i] = (float) nativeA[i] * hanning[i];
    }

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "-----Input max %f", max);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "-----Input min %f", min);


    // fft, there is problem here!
    kiss_fft( cfg , cx_in , cx_out );

    min = max =0;
    // output
    for(i=0; i<nfft; i++){
        nativeOutputA[i] =  (short) (sqrtf(cx_out[i].r * cx_out[i].r + cx_out[i].i * cx_out[i].i)*2.0/(float)size);
        if( nativeOutputA[i] > max){
            max = nativeOutputA[i];
            max_index = i;
        }

        if( nativeOutputA[i] < min){
            min = nativeOutputA[i];
        }
    }


    __android_log_print(ANDROID_LOG_DEBUG, TAG, "-----Output max %f", max);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "-----Output max index is: %d", max_index);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "-----Output min %f", min);


    (*pEnv)->SetShortArrayRegion(pEnv, outA, 0, size, nativeOutputA);

    // release mem
    free(cfg);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "......out of calcSpectrum");
    return outA;
}