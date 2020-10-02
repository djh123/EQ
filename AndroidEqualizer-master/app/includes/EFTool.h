//
// Created by djh on 20-9-25.
//

#ifndef EFTOOL_H_
#define EFTOOL_H_

#include <stdint.h>
#include <stdlib.h>
class EFTool
{

public:
    int mainProcess();
    //-12 ~ 12
    static int EqSelectCoeficients(int s31gain, int s62gain, int s125gain,int s250gain, int s500gain, int s1kgian, int s2kgain, int s4kgain, int s8kgain, int s16kgain);
    static int EqProcess(int16_t * inBuff, int16_t * outBuff, size_t  inLength);

    static int RbSetParam(int RoomSize , int PreDelay, int FeedBack, int Damping, int WetGain, int DryGain, bool WetOnly);
    static int RbProcess(int16_t * ibuf, int16_t * obuf, size_t * isamp, size_t * osamp);

};
#endif