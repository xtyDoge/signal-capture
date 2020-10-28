package model.bo;

import static model.constant.Constants.ANGULAR_VELOCITY_HEAD;
import static model.constant.Constants.FRAME_HEAD;
import static model.constant.Constants.FRAME_LENGTH;
import static model.constant.Constants.FRAME_WITHOUT_HEAD_LENGTH;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSON;

import lombok.Data;
import model.exception.SerialCustomException;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-19
 */
@Data
public class AngularVelocityParam extends BaseSerialParam {

    private long timeStamp;

    private double angularVelocityX;

    private double angularVelocityY;

    private double angularVelocityZ;

    private double temperature;

    private boolean isValid = true;

    // build from byte array
    public AngularVelocityParam(byte[] raw, long nowInMills) throws SerialCustomException {

        // 当前时间
        this.timeStamp = nowInMills;

        // 11字节，说明包括头部的0x55
        byte[] contents = new byte[FRAME_WITHOUT_HEAD_LENGTH - 1];
        if (raw.length == FRAME_LENGTH) {
            if (raw[0] != FRAME_HEAD || raw[1] != ANGULAR_VELOCITY_HEAD) {
                throw new SerialCustomException(String.format("检测到非法帧%s，头应是0x55",
                        DatatypeConverter.printHexBinary(raw)));
            }
            contents = Arrays.copyOfRange(raw, 2, FRAME_LENGTH);

        } else if (raw.length == FRAME_WITHOUT_HEAD_LENGTH) {
            if (raw[0] != ANGULAR_VELOCITY_HEAD) {
                throw new SerialCustomException(String.format("检测到非法帧%s，不是角速度帧",
                        DatatypeConverter.printHexBinary(raw)));
            }
            contents = Arrays.copyOfRange(raw, 1, FRAME_WITHOUT_HEAD_LENGTH);
        }

        // 角速度计算公式:
        // wx=((wxH<<8)|wxL)/32768*2000(°/s)
        // wy=((wyH<<8)|wyL)/32768*2000(°/s)
        // wz=((wzH<<8)|wzL)/32768*2000(°/s) 温度计算公式:
        // T=((TH<<8)|TL) /340+36.53 °C
        // 校验和: Sum=0x55+0x52+wxH+wxL+wyH+wyL+wzH+wzL+TH+TL
        this.angularVelocityX = (short) ((contents[1] & 0xff) << 8 | (contents[0] & 0xff)) / 32768.0 * 2000;
        this.angularVelocityY = (short) ((contents[3] & 0xff) << 8 | (contents[2] & 0xff)) / 32768.0 * 2000;
        this.angularVelocityZ = (short) ((contents[5] & 0xff) << 8 | (contents[4] & 0xff)) / 32768.0 * 2000;
        this.temperature = (short) ((contents[7] & 0xff) << 8 | (contents[6] & 0xff)) / 340.0 + 36.53;
        // 51000000000000000000
        // 52000000000000A00148
        // 5317026F03C524242969
        this.isValid = canPassCheckSum(contents, contents[8]);
    }

    // content不含0x55和0x52，需要之后补上
    private boolean canPassCheckSum(byte[] content, byte checkSum) {
        byte calculatedCheckSum = (byte) (FRAME_HEAD + ANGULAR_VELOCITY_HEAD);
        for (int i=0; i<content.length; i++) {
            calculatedCheckSum += content[i];
        }
        return calculatedCheckSum == checkSum;
    }

    public static void main(String[] args) throws SerialCustomException {
        byte[] dataBag = {0x51, (byte) 0x66, (byte) 0xFF,0x78,0x00,0x2e,0x08, (byte) 0x62,0x0b, (byte) 0x26};
        AccelerationParam angleParam = new AccelerationParam(dataBag, System.currentTimeMillis());
        System.out.println(JSON.toJSON(angleParam));
    }
}
