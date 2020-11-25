package org.xty.signal_capture.common.enums;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-24
 */
public enum SerialDevice {

    BLUETOOTH_CONNECTOR("WT52HB蓝牙多连适配器", "", "/dev/tty.wchusbserial14120", 230400),
    ;

    SerialDevice(String description, String uuid, String identifier, int baudRate) {
        this.description = description;
        this.uuid = uuid;
        this.identifier = identifier;
        this.baudRate = baudRate;
    }

    // 设备描述
    private String description;

    // 多连时的uuid
    private String uuid;

    // 设备路径
    private String identifier;

    // 设备波特率
    private int baudRate;

    public String getDescription() {
        return description;
    }

    public String getUuid() {
        return uuid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getBaudRate() {
        return baudRate;
    }
}
