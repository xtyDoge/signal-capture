package service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import gnu.io.NRSerialPort;
import lombok.extern.slf4j.Slf4j;
import model.device.WitBWT901CL;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-28
 * 读取串口传感器的相关服务
 */
@Lazy
@Service
@Slf4j
public class SerialService {

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




    public static void main(String[] args) {
        String prefix = "/dev/tty.HC-06-DevB";
    }
}
