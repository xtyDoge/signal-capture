package model.device;

import common.enums.SerialCommand;
import gnu.io.NRSerialPort;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import common.exception.SerialCustomException;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-18
 * 基本串口设备的定义（一般是传感器）
 */

@Data
@Slf4j
public abstract class BaseSerialDevice {

    private NRSerialPort serial;

    public BaseSerialDevice(String portName, int baudRate) {
        String port = "";
        for(String s : NRSerialPort.getAvailableSerialPorts()) {
            if (s.equals(portName)) {
                port = s;
            }
        }
        serial = new NRSerialPort(port, baudRate);
        serial.connect();
        this.serial = serial;
    }

    public void readFrame() throws SerialCustomException {
        throw new SerialCustomException("Read Frame method must be override!");
    }

    public void sendCommand(SerialCommand command, String... args) throws SerialCustomException{
        throw new SerialCustomException("SendCommand method must be override!");
    }

    public void terminate() {
        serial.disconnect();
    }


}

