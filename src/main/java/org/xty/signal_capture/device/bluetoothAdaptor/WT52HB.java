package org.xty.signal_capture.device.bluetoothAdaptor;

import static org.xty.signal_capture.common.constant.Constants.MOTION_SENSOR;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import javax.xml.bind.DatatypeConverter;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xty.signal_capture.common.enums.SensorPosition;
import org.xty.signal_capture.common.enums.SerialCommand;
import org.xty.signal_capture.common.enums.SerialDevice;
import lombok.extern.slf4j.Slf4j;

import org.xty.signal_capture.dao.NineAxisMotionSensorFrameDao;
import org.xty.signal_capture.device.BaseSerialDevice;
import org.xty.signal_capture.model.NineAxisMotionSensorFrame;
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

    // 一个缓冲队列，来处理数据包
    private BlockingQueue<NineAxisMotionSensorFrame> frameBuffer = new LinkedBlockingQueue<>();

    private List<ListResponse> deviceIdsToConnect = new ArrayList<>();

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
                byte[] frameBytes = new byte[512];
                int count = 0;
                while(!Thread.interrupted()) {// read all bytes
                    if(ins.available() > 0) {
                        byte b = ins.readByte();
                        // 如果出现 CR LF 代表帧结束
                        if (b == (byte) 0x0D && ins.available() > 0 && ins.readByte() == (byte) 0x0A) {
                            String line = new String(frameBytes);
                            // 序列化成Response
                            CommonResponse response =
                                    WT52HBResponseBuilder.buildFromTextLine(line, Arrays.copyOfRange(frameBytes, 0, frameBytes.length));
                            // 初始化buffer
                            count = 0;
                            frameBytes = new byte[512];
                            // 回调解析Response
                            if (Objects.nonNull(response) && callbackMap.containsKey(response.getHeader())) {
                                String header = response.getHeader();
                                if (!StringUtils.equals("WIT-REV", header)) {
                                    log.info("{}", JSON.toJSON(response));
                                }
                                // 异步处理回调
                                executor.execute(() -> callbackMap.get(header).accept(response));
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
        // 这里一直接收，需要先Loop 120s把所有设备都存起来，然后统一链接，或者看设备是不是全活儿了。。全不活儿就不能连

        ListResponse listResponse = (ListResponse) response;
        // 如果是传感器，就发送连接请求
        if (StringUtils.equals(MOTION_SENSOR, listResponse.getDeviceName())) {
            deviceIdsToConnect.add(listResponse);
        }

        if (deviceIdsToConnect.size() == 4) {
            deviceIdsToConnect.forEach(device -> {
                connect(device.getDeviceNum());
                try {
                    // 串口助手两个命令之间间隔了1s
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            });
        } else {
            log.info("Not enough devices, now {}, required : {}", deviceIdsToConnect.size(), 2);
        }
    }

    // 处理数据包
    private void revResponseCallback(CommonResponse response) {
        RevResponse revResponse = (RevResponse) response;
        // 处理传感器包
        if (StringUtils.equals(MOTION_SENSOR, response.getDeviceName())) {
            // parse content
            NineAxisMotionSensorFrame frame = NineAxisMotionSensorFrame.buildFromByteArray(revResponse.getContent());
            frame.setSensorPosition(parsePositionFromDeviceAddress(response.getDeviceAddress()));
            frame.setDeviceAddress(response.getDeviceAddress());
//            log.info("Device {}. Motion Raw Data [{}]", revResponse.getDeviceAddress(),
//                    DatatypeConverter.printHexBinary(revResponse.getContent()));
//            log.info("Device {}. Motion Parsed Data [{}]", revResponse.getDeviceAddress(),
//                    JSON.toJSON(frame));
            try {
                frameBuffer.put(frame);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }
    }

    // 处理数据包
    private void con(CommonResponse response) {

    }

    // TODO
    private int parsePositionFromDeviceAddress(String deviceAddress) {
        return 1;
    }

    public BlockingQueue<NineAxisMotionSensorFrame> getFrameBuffer() {
        return frameBuffer;
    }

}
