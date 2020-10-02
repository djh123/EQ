//
// Created by djh on 20-9-24.
//

#include <jni.h>
#include<android/log.h>
#include "EFTool.h"

#define LOG_TAG  "EFToolJni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jlong JNICALL
Java_com_bullhead_androidequalizer_Native_nativeInit(JNIEnv *env, jclass type, jstring modelName_) {
    EFTool::EqSelectCoeficients(12,12,12,12,12,12,12,12,12,12);

    LOGI("nativeInit");
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_bullhead_androidequalizer_Native_nativeEqSelectCoeficients(JNIEnv *env, jclass type,
                                                                    jint s31gain, jint s62gain,
                                                                    jint s125gain, jint s250gain,
                                                                    jint s500gain, jint s1kgian,
                                                                    jint s2kgain, jint s4kgain,
                                                                    jint s8kgain, jint s16kgain) {

    EFTool::EqSelectCoeficients(s31gain,s62gain,s125gain,s250gain,s500gain,s1kgian,s2kgain,s4kgain,s8kgain,s16kgain);

    return 0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_bullhead_androidequalizer_Native_nativeEqProcess(JNIEnv *env, jclass type,
                                                          jbyteArray buffer_,
                                                          jbyteArray bufferProcess_,
                                                          jint mBufferSizeInBytes, jint readCount) {
    jbyte *buffer = env->GetByteArrayElements(buffer_, NULL);
    jbyte *bufferProcess = env->GetByteArrayElements(bufferProcess_, NULL);
//    LOGI("111111111");
//    for (int i = 0; i < readCount; ++i) {
//        bufferProcess[i]=buffer[i];
//    }
//    env->ReleaseByteArrayElements(buffer_, buffer, 0);
//    env->ReleaseByteArrayElements(bufferProcess_, bufferProcess, 0);
//    return  0;

    // TODO
    size_t len = readCount/2;

    int16_t * input = new int16_t[readCount/2];
    int16_t * output = new int16_t[readCount/2];

    int h = 0;

    for (int i = 0; i < readCount; i+=2) {
        uint8_t data1 ;
        uint8_t data2 ;
        data1 = buffer[i];
        data2 = buffer[i+1];

        input[h] = (data2 << 8 | data1);
        h ++;
    }


    int ret = EFTool::EqProcess( input, output, len);

    int k = 0;
    for (int j = 0; j < readCount/2; j++) {
        short c1 = (output[j] >> 8) & 0xff;
        short c2 = output[j] & 0xff;
        bufferProcess[k] = c2;
        bufferProcess[k+1] = c1;
        k+=2;
    }


    env->ReleaseByteArrayElements(buffer_, buffer, 0);
    env->ReleaseByteArrayElements(bufferProcess_, bufferProcess, 0);
    return ret;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_bullhead_androidequalizer_Native_nativeRbSetParam(JNIEnv *env, jclass type, jint RoomSize,
                                                           jint PreDelay, jint FeedBack,
                                                           jint Damping, jint WetGain, jint DryGain,
                                                           jobject WetOnly) {
    // TODO
    int ret = EFTool::RbSetParam(RoomSize, PreDelay, FeedBack, Damping, WetGain, DryGain, WetOnly);

    return ret;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_bullhead_androidequalizer_Native_nativeRbProcess(JNIEnv *env, jclass type,
                                                          jbyteArray buffer_,
                                                          jbyteArray bufferProcess_,
                                                          jint mBufferSizeInBytes, jint readCount) {
    jbyte *buffer = env->GetByteArrayElements(buffer_, NULL);
    jbyte *bufferProcess = env->GetByteArrayElements(bufferProcess_, NULL);

    // TODO
    size_t len = readCount/2;

    int16_t * input = new int16_t[readCount/2];
    int16_t * output = new int16_t[readCount/2];

    int h = 0;

    for (int i = 0; i < readCount; i+=2) {
        uint8_t data1 ;
        uint8_t data2 ;
        data1 = buffer[i];
        data2 = buffer[i+1];

        input[h] = (data2 << 8 | data1);
        h ++;
    }


    int ret = EFTool::RbProcess( input, output, &len, &len);

    int k = 0;
    for (int j = 0; j < readCount/2; j++) {
        short c1 = (output[j] >> 8) & 0xff;
        short c2 = output[j] & 0xff;
        bufferProcess[k] = c2;
        bufferProcess[k+1] = c1;
        k+=2;
    }
    env->ReleaseByteArrayElements(buffer_, buffer, 0);
    env->ReleaseByteArrayElements(bufferProcess_, bufferProcess, 0);
    return ret;
}