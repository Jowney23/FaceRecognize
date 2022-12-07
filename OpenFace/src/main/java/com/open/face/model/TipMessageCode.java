package com.open.face.model;

/**
 * Created by Jowney on 2018/7/7.
 */

public class TipMessageCode {
    public final static int MESSAGE_READ_CARED_ERROR = 0x0001;//读卡失败
    public final static int MESSAGE_LOAD_TEMPLATE_START = 0x0010;//加载模版数据开始
    public final static int MESSAGE_LOAD_TEMPLATE_END = 0x0011;//加载模版数据结束
    public final static int MESSAGE_COLOR_RECOGNIZE_ERROR = 0x0100;//与自然光识别处理相关的地方出现出错
    public final static int MESSAGE_COLOR_1_N_SUCCESS = 0x0101;//自然光1：N识别成功
    public final static int MESSAGE_COLOR_1_1_SUCCESS = 0x0110;
    public final static int MESSAGE_COLOR_1_1_FAILURE = 0x0111;
    public final static int SERVICE_CONNECT_SUCCESS = 0x1000;
    public final static int SERVICE_CONNECT_FAILURE = 0x1001;
    public final static int Message_Color_Recognize_Pause = 0x1010;
    public final static int Message_Color_Recognize_Resume = 0x1011;
}
