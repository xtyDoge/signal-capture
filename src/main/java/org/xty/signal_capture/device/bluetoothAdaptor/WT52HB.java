package org.xty.signal_capture.device.bluetoothAdaptor;

import static org.xty.signal_capture.common.constant.Constants.MOTION_SENSOR;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.xml.bind.DatatypeConverter;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;

import org.xty.signal_capture.common.enums.SerialCommand;
import org.xty.signal_capture.common.enums.SerialDevice;
import lombok.extern.slf4j.Slf4j;

import org.xty.signal_capture.device.BaseSerialDevice;
import org.xty.signal_capture.model.bo.bluetoothAdaptor.CommonResponse;
import org.xty.signal_capture.model.bo.bluetoothAdaptor.ListResponse;
import org.xty.signal_capture.model.bo.bluetoothAdaptor.RevResponse;
import org.xty.signal_capture.model.bo.bluetoothAdaptor.WT52HBResponseBuilder;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-23
 * 蓝牙多连适配器
 */
@Slf4j
public class WT52HB extends BaseSerialDevice {

    // 每个设备对应三个线程，一个发一个收一个处理回调？
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final DataOutputStream outs = new DataOutputStream(super.getSerial().getOutputStream());
    private final DataInputStream ins = new DataInputStream(super.getSerial().getInputStream());

    private final Map<String, Consumer<CommonResponse>> callbackMap = new HashMap<String, Consumer<CommonResponse>>(){{
        put("WIT-LIST", (response) -> listResponseCallback(response));
        put("WIT-REV",  (response) -> revResponseCallback(response));
        put("WIT-CONLIST",  (response) -> revResponseCallback(response));

    }};


    public WT52HB(String portName, int baudRate) {
        super(portName, 230400);
    }

    public static WT52HB from(SerialDevice serialDevice) {
        return new WT52HB(serialDevice.getIdentifier(), serialDevice.getBaudRate());
    }

    @Override
    public void readFrame() {
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
                            // 序列化成Response
                            CommonResponse response = WT52HBResponseBuilder.buildFromTextLine(line);
                            // 回调解析Response
                            if (Objects.nonNull(response) && callbackMap.containsKey(response.getHeader())) {
                                log.info("{}", JSON.toJSON(response));
                                callbackMap.get(response.getHeader()).accept(response);
                            }
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


    // 列表中找到输入传感器的设备，并连接
    private void listResponseCallback(CommonResponse response) {
        ListResponse listResponse = (ListResponse) response;
        // 如果是传感器，就发送连接请求
        if (StringUtils.equals(MOTION_SENSOR, listResponse.getDeviceName())) {
            connect(response.getDeviceNum());
        }
    }

    // 处理数据包
    private void revResponseCallback(CommonResponse response) {
        RevResponse revResponse = (RevResponse) response;
        // 如果是传感器，就发送连接请求
        if (StringUtils.equals(MOTION_SENSOR, response.getDeviceName())) {
            // parse content
            log.info("Device {}. Motion Raw Data [{}]", revResponse.getDeviceAddress(),
                    DatatypeConverter.printHexBinary(revResponse.getContent().getBytes()));
        }
    }

    // 处理数据包
    private void con(CommonResponse response) {
        RevResponse revResponse = (RevResponse) response;
        // 如果是传感器，就发送连接请求
        if (StringUtils.equals(MOTION_SENSOR, response.getDeviceName())) {
            // parse content
            log.info("Device {}. Motion Raw Data [{}]", revResponse.getDeviceAddress(),
                    DatatypeConverter.printHexBinary(revResponse.getContent().getBytes()));
        }
    }
}
