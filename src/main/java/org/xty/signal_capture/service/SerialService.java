package org.xty.signal_capture.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import gnu.io.NRSerialPort;
import lombok.extern.slf4j.Slf4j;

import org.xty.signal_capture.common.enums.SerialDevice;
import org.xty.signal_capture.dao.NineAxisMotionSensorFrameDao;
import org.xty.signal_capture.dao.VideoFrameDao;
import org.xty.signal_capture.device.bluetoothAdaptor.WT52HB;
import org.xty.signal_capture.device.sensor.WitBWT901CL;
import org.xty.signal_capture.model.NineAxisMotionSensorFrame;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-28
 * 读取串口传感器的相关服务
 */
@Lazy
@Service
@Slf4j
public class SerialService {

    @Autowired
    private NineAxisMotionSensorFrameDao frameDao;

    /**
     * 展示可用串口
     */
    public List<String> showAllAvailablePorts(String prefix) {
        return NRSerialPort.getAvailableSerialPorts()
                .stream()
                .filter(portName -> portName.contains(prefix))
                .collect(Collectors.toList());
    }

    /**
     * 初始化特定串口
     */
    public List<WitBWT901CL> initPorts(List<String> portNames) {
        return portNames.stream().map(WitBWT901CL::fromPortName).collect(Collectors.toList());
    }


    public WT52HB initAllSensors(String deviceName, String connMode) {
        WT52HB device1 = WT52HB.from(SerialDevice.BLUETOOTH_CONNECTOR);
        device1.name(deviceName);

        device1.connmode(connMode);
        // 设置连接超时时间
        device1.contimeout(60);
        // 设置为自动连接指定名称的
        device1.scan(1);

        // 需要确定四个都连接上了，怎么确定呢
        // TODO 查询当前连接的设备数是否为4，如果是的话说明成功了，进行下一步
//        while(!device1.isAllSensorsConnected()) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                log.error("", e);
//            }
//        }
        return device1;
    }

    public void readFromAllSensors(String uuid, WT52HB device) {
        try {
            NineAxisMotionSensorFrame frame = device.getFrameBuffer().take();
            frame.setUuid(uuid);
            frameDao.insert(frame);
        } catch (InterruptedException e) {
            log.error("Take from sensor frame buffer error! ", e);
        }
    }

    public void initAllWT901BLE57(String uuid) {
        initAllSensors("WT901BLE58", "1");
    }



    public static void main(String[] args) {
        String prefix = "/dev/tty.HC-06-DevB";
    }
}
