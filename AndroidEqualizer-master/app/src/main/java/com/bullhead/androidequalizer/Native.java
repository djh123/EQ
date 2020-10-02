package com.bullhead.androidequalizer;

public class Native {
    static {

        System.loadLibrary("mnncore");
    }
    public static native long nativeInit(String modelName);
    public static native int nativeEqSelectCoeficients(int s31gain, int s62gain, int s125gain,int s250gain, int s500gain, int s1kgian, int s2kgain, int s4kgain, int s8kgain, int s16kgain);
    public static native int nativeEqProcess(byte[] buffer, byte[] bufferProcess, int mBufferSizeInBytes, int readCount);

    public static native int nativeRbSetParam(int RoomSize , int PreDelay, int FeedBack, int Damping, int WetGain, int DryGain, Boolean WetOnly);
    public static native int nativeRbProcess(byte[] buffer, byte[] bufferProcess, int mBufferSizeInBytes, int readCount);

}
