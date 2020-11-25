package org.xty.signal_capture.common.constant;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-19
 */
public class Constants {
    // 帧开头
    public static final byte FRAME_HEAD = (byte) 0x55;
    // 角速度定位字节
    public static final byte ANGULAR_VELOCITY_HEAD = (byte) 0x52;
    // 加速度定位字节
    public static final byte ACCELERATION_HEAD = (byte) 0x51;
    // 角度定位字节
    public static final byte ANGLE_HEAD = (byte) 0x53;
    // 包长度
    public static final int FRAME_LENGTH = 11;
    // 不带开头的包长度
    public static final int FRAME_WITHOUT_HEAD_LENGTH = 10;
    // 重力加速度
    public static final double GRAVITY = 9.8;
    // 姿态传感器型号
    public static final String MOTION_SENSOR = "WT901BLE58";

}
