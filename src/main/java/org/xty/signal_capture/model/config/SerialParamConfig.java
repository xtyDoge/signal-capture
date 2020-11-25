package org.xty.signal_capture.model.config;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-14
 */
@Data
public class SerialParamConfig {

    private String serialNumber;// 串口号
    private int baudRate;        // 波特率
    private int checkoutBit;    // 校验位
    private int dataBit;        // 数据位
    private int stopBit;        // 停止位

}
