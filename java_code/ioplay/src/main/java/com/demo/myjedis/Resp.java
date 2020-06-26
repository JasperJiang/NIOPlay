package com.demo.myjedis;


/**
 * 协议层
 */
public class Resp {

    public static final String START = "*";

    public static final String STRING_LENGTH = "$";

    public static final String LINE = "\r\n";


    public static enum command{
        SET, GET, INCR
    }

}
