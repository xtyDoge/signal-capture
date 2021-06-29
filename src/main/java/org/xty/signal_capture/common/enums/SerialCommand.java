package org.xty.signal_capture.common.enums;

import javax.xml.bind.DatatypeConverter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-23
 */
@Slf4j
public enum SerialCommand {
    AT_PING("AT\r\n", "测试连接状态，OK表示连接成功"),
    AT_SCAN("AT+SCAN=%s\r\n", "搜索可连接设备，1开始，2停止"),
    AT_CONNECT("AT+CONNECT=%s\r\n", "连接指定设备，参数为deviceNum"),
    AT_NAME("AT+NAME=%s\r\n", "查询/设置名称自动连接模式名称"),
    AT_CONNMODE("AT+CONNMODE=%s\r\n", "设置手动/自动连接,0默认，1按名称，2按UUID"),
    AT_CONTIMEOUT("AT+CONTIMEOUT=%s\r\n", "设置查询设置连接超时"),
    ;

    private String command;
    private String description;

    public String getCommand(String... args) {
        log.info("COMMAND bytes : [{}]", DatatypeConverter.printHexBinary(String.format(command, args).getBytes()));
        return String.format(command, args);
    }

    public String getDescription() {
        return description;
    }

    SerialCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }


}
