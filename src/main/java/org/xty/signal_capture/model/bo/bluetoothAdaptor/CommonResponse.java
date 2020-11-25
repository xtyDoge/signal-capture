package org.xty.signal_capture.model.bo.bluetoothAdaptor;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-24
 */
@Data
public class CommonResponse {

    protected String header;

    protected Integer deviceNum;

    protected String deviceName;

    protected String deviceAddress;

}
