package model.device;

import static common.constant.Constants.ACCELERATION_HEAD;
import static common.constant.Constants.ANGLE_HEAD;
import static common.constant.Constants.ANGULAR_VELOCITY_HEAD;
import static common.constant.Constants.FRAME_LENGTH;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.xml.bind.DatatypeConverter;

import lombok.extern.slf4j.Slf4j;
import model.bo.AccelerationParam;
import model.bo.AngleParam;
import model.bo.AngularVelocityParam;
import model.bo.BaseSerialParam;
import model.bo.SixAxisFrameParam;
import common.exception.SerialCustomException;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-28
 */
@Slf4j
public class WitBWT901CL extends BaseSerialDevice {

    public WitBWT901CL(String portName) {
        super(portName, 9600);
    }

    public static WitBWT901CL fromPortName(String portName) {
        WitBWT901CL device = new WitBWT901CL(portName);
        return device;
    }

    @Override
    public void readFrame() throws SerialCustomException {
        DataInputStream ins = new DataInputStream(super.getSerial().getInputStream());
        DataOutputStream outs = new DataOutputStream(super.getSerial().getOutputStream());
        try{
            //while(ins.available()==0 && !Thread.interrupted());// wait for a byte
            int count = 0;
            byte[] frameBytes = new byte[11];

            while(!Thread.interrupted()) {// read all bytes
                if(ins.available() > 0) {
                    byte b = ins.readByte();
                    long now = System.currentTimeMillis();
                    //outs.write((byte)b);
                    if (count == 0 && b != (byte) 0x55) {continue;}

                    // 开始接收
                    System.out.println(DatatypeConverter.printHexBinary(new byte[]{b}) + ","+ Integer.toBinaryString(b));
                    frameBytes[count] = b;
                    count += 1;
                    if (count == FRAME_LENGTH) {
                        count = 0;
                        parseFrame(frameBytes, now);
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public BaseSerialParam parseFrame(byte[] dataBytes, long now) throws SerialCustomException {
        BaseSerialParam param = null;
        byte headByte = dataBytes[1];
//        log.info("============================== FRAME BEGIN ==============================");
        switch (headByte) {
            case ANGULAR_VELOCITY_HEAD:
                AngularVelocityParam angularVelocityParam = new AngularVelocityParam(dataBytes, now);
                param = angularVelocityParam;
//                log.info("time {}, angularVelocity data : {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));
//                log.info("time {}, angularVelocity data {}", System.currentTimeMillis(), JSON.toJSON(angularVelocityParam));
                break;
            case ACCELERATION_HEAD:
                AccelerationParam accelerationParam = new AccelerationParam(dataBytes, now);
                param = accelerationParam;
//                log.info("time {}, acceleration data : {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));
//                log.info("time {}, acceleration data {}", System.currentTimeMillis(), JSON.toJSON(accelerationParam));
                break;
            case ANGLE_HEAD:
                AngleParam angleParam = new AngleParam(dataBytes, now);
                param = angleParam;
//                log.info("time {}, angle data : {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));
//                log.info("time {}, angle data {}", System.currentTimeMillis(), JSON.toJSON(angleParam));
                break;
            default:
                break;
        }
//        log.info("=============================== FRAME END ===============================");
        return param;
    }

    @Override
    public void terminate() {
        super.terminate();
    }

    public SixAxisFrameParam readOneFrame() throws SerialCustomException {

        DataInputStream ins = new DataInputStream(super.getSerial().getInputStream());
        DataOutputStream outs = new DataOutputStream(super.getSerial().getOutputStream());

        SixAxisFrameParam sixAxisFrameParam = new SixAxisFrameParam();

        // TODO 增加三个标志位 来标记是不是抓了一整个帧
        boolean isAngleVelocityReady = false;
        boolean isAngleReady = false;
        boolean isAccelerationReady = false;

        boolean isFrameReady = false;
        try{
            //while(ins.available()==0 && !Thread.interrupted());// wait for a byte
            int count = 0;
            byte[] frameBytes = new byte[11];
            long now = System.currentTimeMillis();

            while(!Thread.interrupted() && !isFrameReady) {// read all bytes
                if(ins.available() > 0) {
                    byte b = ins.readByte();
                    //outs.write((byte)b);
                    if (count == 0 && b != (byte) 0x55) {continue;}

                    // 开始接收
                    frameBytes[count] = b;
                    count += 1;
                    if (count == FRAME_LENGTH) {
                        count = 0;
                        BaseSerialParam frame = parseFrame(frameBytes, now);

                        // 判空
                        if (frame != null) {
                            if (frame instanceof AccelerationParam) {
                                isAccelerationReady = true;
                                sixAxisFrameParam.setAccelerationParam((AccelerationParam) frame);
                            } else if (frame instanceof AngleParam) {
                                isAngleReady = true;
                                sixAxisFrameParam.setAngleParam((AngleParam) frame);
                            } else if (frame instanceof AngularVelocityParam) {
                                isAngleVelocityReady = true;
                                sixAxisFrameParam.setAngularVelocityParam((AngularVelocityParam) frame);
                            }
                        }
                        isFrameReady = isAccelerationReady && isAngleReady && isAngleVelocityReady;

                    }
                }
            }
            return sixAxisFrameParam;

        }catch(Exception ex){
            log.error("Parse frame error !", ex);
        }
        return null;
    }

}
