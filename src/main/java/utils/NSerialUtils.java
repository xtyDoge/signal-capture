package utils;

import static common.constant.Constants.ACCELERATION_HEAD;
import static common.constant.Constants.ANGLE_HEAD;
import static common.constant.Constants.ANGULAR_VELOCITY_HEAD;
import static common.constant.Constants.FRAME_LENGTH;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSON;

import gnu.io.NRSerialPort;
import lombok.extern.slf4j.Slf4j;
import model.bo.AccelerationParam;
import model.bo.AngleParam;
import model.bo.AngularVelocityParam;
import common.exception.SerialCustomException;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-18
 */
@Slf4j
public class NSerialUtils {

    private NRSerialPort serial;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public void init(String portName) throws SerialCustomException {
        String port = "";
        for(String s : NRSerialPort.getAvailableSerialPorts()) {
            log.info("Availible port: "+s);
            if (s.equals(portName)) {
                port = s;
            }
        }
        int baudRate = 115200;
        serial = new NRSerialPort(port, baudRate);
        serial.connect();
    }

    public void readFrame() {
        DataInputStream ins = new DataInputStream(serial.getInputStream());
        DataOutputStream outs = new DataOutputStream(serial.getOutputStream());
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

    public void terminate() {
        serial.disconnect();
    }

    private void parseFrame(byte[] dataBytes, long now) throws SerialCustomException {
        byte headByte = dataBytes[1];
        log.info("============================== FRAME BEGIN ==============================");
        switch (headByte) {
            case ANGULAR_VELOCITY_HEAD:
                log.info("time {}, angularVelocity data : {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));
                AngularVelocityParam angularVelocityParam = new AngularVelocityParam(dataBytes, now);
                log.info("time {}, angularVelocity data {}", System.currentTimeMillis(), JSON.toJSON(angularVelocityParam));
                break;
            case ACCELERATION_HEAD:
                AccelerationParam accelerationParam = new AccelerationParam(dataBytes, now);
                log.info("time {}, acceleration data : {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));
                log.info("time {}, acceleration data {}", System.currentTimeMillis(), JSON.toJSON(accelerationParam));
                break;
            case ANGLE_HEAD:
                AngleParam angleParam = new AngleParam(dataBytes, now);
                log.info("time {}, angle data : {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));
                log.info("time {}, angle data {}", System.currentTimeMillis(), JSON.toJSON(angleParam));
                break;
            default:
                break;
        }
        log.info("=============================== FRAME END ===============================");
    }




}
