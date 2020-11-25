package org.xty.signal_capture.model.bo;

import static org.xty.signal_capture.common.constant.Constants.ANGLE_HEAD;
import static org.xty.signal_capture.common.constant.Constants.FRAME_HEAD;
import static org.xty.signal_capture.common.constant.Constants.FRAME_LENGTH;
import static org.xty.signal_capture.common.constant.Constants.FRAME_WITHOUT_HEAD_LENGTH;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import lombok.Data;
import org.xty.signal_capture.common.exception.SerialCustomException;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-20
 */
@Data
public class AngleParam extends BaseSerialParam{

    // 滚转角（x 轴）
    private double roll;
    // 俯仰角(y 轴)
    private double pitch;
    // 偏航角(z 轴)
    private double yaw;
    // 温度
    private double temperature;
    // 时间
    private long timestamp;
    // 校验和是否符合
    private boolean isValid = true;

    // build from byte array
    public AngleParam(byte[] raw, long nowInMills) throws SerialCustomException {

        // 当前时间
        this.timestamp = nowInMills;

        // 11字节，说明包括头部的0x55
        byte[] contents = new byte[FRAME_WITHOUT_HEAD_LENGTH - 1];
        if (raw.length == FRAME_LENGTH) {
            if (raw[0] != FRAME_HEAD || raw[1] != ANGLE_HEAD) {
                throw new SerialCustomException(String.format("检测到非法帧%s，不是角度帧",
                        DatatypeConverter.printHexBinary(raw)));
            }
            contents = Arrays.copyOfRange(raw, 2, FRAME_LENGTH);

        } else if (raw.length == FRAME_WITHOUT_HEAD_LENGTH) {
            if (raw[0] != ANGLE_HEAD) {
                throw new SerialCustomException(String.format("检测到非法帧%s，不是角度帧",
                        DatatypeConverter.printHexBinary(raw)));
            }
            contents = Arrays.copyOfRange(raw, 1, FRAME_WITHOUT_HEAD_LENGTH);
        }

//        角速度计算公式:
//        滚转角(x 轴)Roll=((RollH<<8)|RollL)/32768*180(°)
//        俯仰角(y 轴)Pitch=((PitchH<<8)|PitchL)/32768*180(°)
//        偏航角(z 轴)Yaw=((YawH<<8)|YawL)/32768*180(°) 温度计算公式:
//        T=((TH<<8)|TL) /340+36.53 °C
//        校验和: Sum=0x55+0x53+RollH+RollL+PitchH+PitchL+YawH+YawL+TH+TL
        // 还好是战雷玩家，看得懂
        this.roll = (short) ((0xff & contents[1]) << 8 |  (0xff & contents[0])) / 32768.0 * 180;
        this.pitch = (short) ((0xff & contents[3]) << 8 | (0xff & contents[2])) / 32768.0 * 180;
        this.yaw = (short) ((0xff & contents[5]) << 8 | (0xff & contents[4])) / 32768.0 * 180;
        this.temperature = (short) ((0xff & contents[7]) << 8 | (0xff & contents[6])) / 100.0;

        this.isValid = canPassCheckSum(contents, contents[8]);
    }

    // content不含0x55和0x51，需要之后补上
    private boolean canPassCheckSum(byte[] content, byte checkSum) {
        byte calculatedCheckSum = (byte) (FRAME_HEAD + ANGLE_HEAD);
        for (int i=0; i<content.length; i++) {
            calculatedCheckSum += content[i];
        }
        return calculatedCheckSum == checkSum;
    }

}
