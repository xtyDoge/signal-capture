package model.bo;

import static model.constant.Constants.ACCELERATION_HEAD;
import static model.constant.Constants.FRAME_HEAD;
import static model.constant.Constants.FRAME_LENGTH;
import static model.constant.Constants.FRAME_WITHOUT_HEAD_LENGTH;
import static model.constant.Constants.GRAVITY;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import lombok.Data;
import model.exception.SerialCustomException;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-19
 */
@Data
public class AccelerationParam extends BaseSerialParam {

    private long timeStamp;

    private double accelerationX;

    private double accelerationY;

    private double accelerationZ;

    private double temperature;

    private boolean isValid = true;

    // build from byte array
    public AccelerationParam(byte[] raw, long nowInMills) throws SerialCustomException {

        // 当前时间
        this.timeStamp = nowInMills;

        // 11字节，说明包括头部的0x55
        byte[] contents = new byte[FRAME_WITHOUT_HEAD_LENGTH - 1];
        if (raw.length == FRAME_LENGTH) {
            if (raw[0] != FRAME_HEAD || raw[1] != ACCELERATION_HEAD) {
                throw new SerialCustomException(String.format("检测到非法帧%s，不是加速度帧",
                        DatatypeConverter.printHexBinary(raw)));
            }
            contents = Arrays.copyOfRange(raw, 2, FRAME_LENGTH);

        } else if (raw.length == FRAME_WITHOUT_HEAD_LENGTH) {
            if (raw[0] != ACCELERATION_HEAD) {
                throw new SerialCustomException(String.format("检测到非法帧%s，不是加速度帧",
                        DatatypeConverter.printHexBinary(raw)));
            }
            contents = Arrays.copyOfRange(raw, 1, FRAME_WITHOUT_HEAD_LENGTH);
        }

//        加速度计算公式:
//        ax=((AxH<<8)|AxL)/32768*16g(g 为重力加速度，可取 9.8m/s2)
//        ay=((AyH<<8)|AyL)/32768*16g(g 为重力加速度，可取 9.8m/s2)
//        az=((AzH<<8)|AzL)/32768*16g(g 为重力加速度，可取 9.8m/s2)
//        温度计算公式:
//        T=((TH<<8)|TL) /340+36.53 °C
//        校验和: Sum=0x55+0x51+AxH+AxL+AyH+AyL+AzH+AzL+TH+TL
        System.out.println(DatatypeConverter.printHexBinary(contents));
        this.accelerationX = (short) ((contents[1] & 0xff) << 8 | (contents[0] & 0xff)) / 32768.0 * 16;
        this.accelerationY = (short) ((contents[3] & 0xff) << 8 | (contents[2] & 0xff)) / 32768.0 * 16;
        this.accelerationZ = (short) ((contents[5] & 0xff) << 8 | (contents[4] & 0xff)) / 32768.0 * 16;
        this.temperature = (short) ((contents[7] & 0xff) << 8 | (contents[6] & 0xff)) / 100.0;

        this.isValid = canPassCheckSum(contents, contents[8]);
    }

    // content不含0x55和0x51，需要之后补上
    private boolean canPassCheckSum(byte[] content, byte checkSum) {
        byte calculatedCheckSum = (byte) (0xff & FRAME_HEAD + 0xff & ACCELERATION_HEAD);
        for (int i=0; i<content.length; i++) {
            calculatedCheckSum += (content[i] & 0xff);
        }
        return (calculatedCheckSum & 0xff) == (checkSum & 0xff);
    }

}
