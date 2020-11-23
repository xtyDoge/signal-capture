package model.device.bluetoothAdaptor;

import static common.constant.Constants.FRAME_LENGTH;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSON;

import common.enums.SerialCommand;
import common.enums.SerialDevice;
import common.exception.SerialCustomException;
import lombok.extern.slf4j.Slf4j;
import model.bo.bluetoothAdaptor.WT52HBResponseBuilder;
import model.device.BaseSerialDevice;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-23
 * 蓝牙多连适配器
 */
@Slf4j
public class WT52HB extends BaseSerialDevice {

    // 每个设备对应两个线程，一个发一个收
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final DataOutputStream outs = new DataOutputStream(super.getSerial().getOutputStream());
    private final DataInputStream ins = new DataInputStream(super.getSerial().getInputStream());


    public WT52HB(String portName, int baudRate) {
        super(portName, 230400);
    }

    public static WT52HB from(SerialDevice serialDevice) {
        return new WT52HB(serialDevice.getIdentifier(), serialDevice.getBaudRate());
    }

    @Override
    public void readFrame() throws SerialCustomException {
        executor.execute(() -> {
            try{
                byte[] frameBytes = new byte[100];
                int count = 0;
                while(!Thread.interrupted()) {// read all bytes
                    if(ins.available() > 0) {
                        byte b = ins.readByte();
                        // 如果出现 CR LF 代表帧结束
                        if (b == (byte) 0x0D && ins.available() > 0 && ins.readByte() == (byte) 0x0A) {
                            String line = new String(frameBytes);
                            // 初始化buffer
                            count = 0;
                            frameBytes = new byte[100];
                            log.info("{}", JSON.toJSON(WT52HBResponseBuilder.buildFromTextLine(line)));
                            continue;
                        }

                        // 开始接收，存进去
                        frameBytes[count] = b;
                        count += 1;
                    }
                }

            } catch(Exception ex){
                log.error("", ex);
            }
        });
    }

    @Override
    public void sendCommand(SerialCommand command, String... args) {
        executor.execute(() -> {
            try {
                String commandText = command.getCommand(args);
                log.info("Try to send command : {}", commandText);
                outs.writeBytes(commandText);
            } catch (IOException e) {
                log.error("", e);
            }
        });
    }

    // ping命令
    public void ping() {
        sendCommand(SerialCommand.AT_PING);
    }

    // connect命令
    public void connect(Integer deviceNum) {
        sendCommand(SerialCommand.AT_CONNECT, String.valueOf(deviceNum));
    }

    // scan命令
    public void scan(Integer deviceNum) {
        sendCommand(SerialCommand.AT_SCAN, String.valueOf(deviceNum));
    }

}
