package utils;

import static model.constant.Constants.ACCELERATION_HEAD;
import static model.constant.Constants.ANGLE_HEAD;
import static model.constant.Constants.ANGULAR_VELOCITY_HEAD;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.xml.bind.DatatypeConverter;

import gnu.io.NRSerialPort;
import lombok.extern.slf4j.Slf4j;
import model.exception.SerialCustomException;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-18
 */
@Slf4j
public class NSerialUtils {

    private NRSerialPort serial;

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
            while(!Thread.interrupted()) {// read all bytes
                if(ins.available() > 0) {
                    byte b = ins.readByte();
                    //outs.write((byte)b);
                    if (b == (byte) 0x55) {
                        int cursor = 0;
                        byte[] frameBytes = new byte[10];
                        ins.read(frameBytes);
                        // 连续存储之后的10byte = 1个包
                        parseFrame(frameBytes);
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

    private void parseFrame(byte[] dataBytes) {
        byte headByte = dataBytes[0];
        switch (headByte) {
            case ANGULAR_VELOCITY_HEAD:
                log.info("time {}, angle speed data : {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));

                break;
            case ACCELERATION_HEAD:
                log.info("time {}, acceleration data {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));
                break;
            case ANGLE_HEAD:
                log.info("time {}, angle data {}", System.currentTimeMillis(), DatatypeConverter.printHexBinary(dataBytes));
                break;
            default:
                break;
        }
    }


}
