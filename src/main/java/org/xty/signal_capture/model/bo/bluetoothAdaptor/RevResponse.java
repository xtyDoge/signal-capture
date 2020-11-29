package org.xty.signal_capture.model.bo.bluetoothAdaptor;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-24
 * WIT-REV-#0:"WT901BLE58"           0xF9E81A7EA3EC,20,Ua�
 */
@Data
public class RevResponse extends CommonResponse{

    private Integer contentLength;

    private byte[] content;
}
