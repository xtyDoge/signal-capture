package org.xty.signal_capture.model;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-23
 */
@Data
public class NineAxisMotionSensorFrame {

    private Long id;

    // 获取时间戳（毫秒）
    private Long mills;

    // 三轴角速度
    private Double angularVelocityX;

    private Double angularVelocityY;

    private Double angularVelocityZ;

    // 三轴加速度
    private Double accelerationX;

    private Double accelerationY;

    private Double accelerationZ;

    // 三轴姿态角

    // 滚转角（x 轴）
    private Double roll;
    // 俯仰角(y 轴)
    private Double pitch;
    // 偏航角(z 轴)
    private Double yaw;

    // 位置
    private int sensorPosition;

    // 设备地址
    private String deviceAddress;

    // 采集行动名称（和保持一致）
    private String uuid;

    public static NineAxisMotionSensorFrame buildFromByteArray(byte[] rawData) {

        NineAxisMotionSensorFrame frame = new NineAxisMotionSensorFrame();
        // 当前时间
        frame.mills = System.currentTimeMillis();

        // WT901BLE5.0帧结构
        // 0    1   2   3   4   5   6   7   8   9
        // x    x   axl axh ayl ayh azl azh wxl wxh
        // 10  11   12  13  14  15  16  17  18  19
        // wyl  wyh wzl wzh rl  rh  pl  ph  yl  yh

        //        加速度计算公式:
        //        ax=((AxH<<8)|AxL)/32768*16g(g 为重力加速度，可取 9.8m/s2)
        //        ay=((AyH<<8)|AyL)/32768*16g(g 为重力加速度，可取 9.8m/s2)
        //        az=((AzH<<8)|AzL)/32768*16g(g 为重力加速度，可取 9.8m/s2)
        //        温度计算公式:
        //        T=((TH<<8)|TL) /340+36.53 °C
        //        校验和: Sum=0x55+0x51+AxH+AxL+AyH+AyL+AzH+AzL+TH+TL
        frame.accelerationX = (short) (((short) rawData[3] & 0xff) << 8 | ((short) rawData[2] & 0xff)) / 32768.0 * 16;
        frame.accelerationY = (short) (((short) rawData[5] & 0xff) << 8 | ((short) rawData[4] & 0xff)) / 32768.0 * 16;
        frame.accelerationZ = (short) (((short) rawData[7] & 0xff) << 8 | ((short) rawData[6] & 0xff)) / 32768.0 * 16;

        // 角速度计算公式:
        // wx=((wxH<<8)|wxL)/32768*2000(°/s)
        // wy=((wyH<<8)|wyL)/32768*2000(°/s)
        // wz=((wzH<<8)|wzL)/32768*2000(°/s) 温度计算公式:
        // T=((TH<<8)|TL) /340+36.53 °C
        // 校验和: Sum=0x55+0x52+wxH+wxL+wyH+wyL+wzH+wzL+TH+TL
        frame.angularVelocityX = (short) (((short) rawData[9] & 0xff) << 8 | ((short) rawData[8] & 0xff)) / 32768.0 * 2000;
        frame.angularVelocityY = (short) (((short) rawData[11] & 0xff) << 8 | ((short) rawData[10] & 0xff)) / 32768.0 * 2000;
        frame.angularVelocityZ = (short) (((short) rawData[13] & 0xff) << 8 | ((short) rawData[12] & 0xff)) / 32768.0 * 2000;

        //        角速度计算公式:
        //        滚转角(x 轴)Roll=((RollH<<8)|RollL)/32768*180(°)
        //        俯仰角(y 轴)Pitch=((PitchH<<8)|PitchL)/32768*180(°)
        //        偏航角(z 轴)Yaw=((YawH<<8)|YawL)/32768*180(°) 温度计算公式:
        //        T=((TH<<8)|TL) /340+36.53 °C
        //        校验和: Sum=0x55+0x53+RollH+RollL+PitchH+PitchL+YawH+YawL+TH+TL
        // 还好是战雷玩家，看得懂
        frame.roll = (short) (((short) rawData[15] & 0xff) << 8 | ((short) rawData[14] & 0xff)) / 32768.0 * 180;
        frame.pitch = (short) (((short) rawData[17] & 0xff) << 8 | ((short) rawData[16] & 0xff)) / 32768.0 * 180;
        frame.yaw = (short) (((short) rawData[19] & 0xff) << 8 | ((short) rawData[18] & 0xff)) / 32768.0 * 180;

        return frame;
    }


}
