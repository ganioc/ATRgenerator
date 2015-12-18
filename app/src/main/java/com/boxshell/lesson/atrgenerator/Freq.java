package com.boxshell.lesson.atrgenerator;

/**
 * Created by yangjun on 15/12/14.
 */
public class Freq {

    public final static int DTMF_TONE_A1 = 697;
    public final static int DTMF_TONE_A2 = 770;
    public final static int DTMF_TONE_A3 = 852;
    public final static int DTMF_TONE_A4 = 941;
    public final static int DTMF_TONE_B1 = 1209;
    public final static int DTMF_TONE_B2 = 1336;
    public final static int DTMF_TONE_B3 = 1477;
    public final static int DTMF_TONE_B4 = 1633;
    /*
            A6 ~ A19 are the strongest signal which can resonate between sender and receiver
            600Hz ~ 5.5kHz
     */
    public final static int TONE_A0 = 50;
    public final static int TONE_A1 = 100;
    public final static int TONE_A2 = 200;
    public final static int TONE_A3 = 300;
    public final static int TONE_A4 = 400;
    public final static int TONE_A5 = 500;
    public final static int TONE_A6 = 600;
    public final static int TONE_A7 = 700;
    public final static int TONE_A8 = 800;
    public final static int TONE_A9 = 900;
    public final static int TONE_A10 = 1000;
    public final static int TONE_A11 = 1500;
    public final static int TONE_A12 = 2000;
    public final static int TONE_A13 = 2500;
    public final static int TONE_A14 = 3000;
    public final static int TONE_A15 = 3500;
    public final static int TONE_A16 = 4000;
    public final static int TONE_A17 = 4500;
    public final static int TONE_A18 = 5000;
    public final static int TONE_A19 = 5500;
    public final static int TONE_A20 = 6000;
    public final static int TONE_A21 = 6500;
    public final static int TONE_A22 = 7000;
    public final static int TONE_A23 = 7500;
    public final static int TONE_A24 = 8000;
    public final static int TONE_A25 = 8500;
    public final static int TONE_A26 = 9000;
    public final static int TONE_A27 = 9500;
    public final static int TONE_A28 = 10000;
    public final static int TONE_A29 = 10500;
    public final static int TONE_A30 = 11000;



    public final static int SAMPLE_RATE_0 = 48000;
    public final static int SAMPLE_RATE_1 = 44100;
    public final static int SAMPLE_RATE_2 = 22050;


    // below is the frequency I'm using right now. 2015-12-14
    public final static int GTONE_1 = 1100;
    public final static int GTONE_2 = 1500;
    public final static int GTONE_3 = 1900;
    public final static int GTONE_4 = 2300;
    public final static int GTONE_5 = 2700;
    public final static int GTONE_6 = 3100;
    public final static int GTONE_7 = 3500;

    public final static int GTONE_HEAD = 4100;
    public final static int GTONE_TAIL = 4400;
    public final static int GTONE_AB = GTONE_2;
    public final static int GTONE_AC = GTONE_5;
    public final static int GTONE_BA = GTONE_7;
    public final static int GTONE_BC = GTONE_3;
    public final static int GTONE_CA = GTONE_4;
    public final static int GTONE_CB = GTONE_6;
    public final static int GTONE_A =  GTONE_1;

    public final static int PACKET_DELTA = 1024;
    public final static int PACKET_HEAD_LENGTH = PACKET_DELTA * 8;
    public final static int PACKET_TAIL_LENGTH = PACKET_DELTA * 8;
    public final static int PACKET_BIT_LENGTH = PACKET_DELTA * 4;
    public final static int FREQUENCY_DELTA = SAMPLE_RATE_1/PACKET_DELTA;



}
