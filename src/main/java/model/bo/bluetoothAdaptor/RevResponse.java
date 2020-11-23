package model.bo.bluetoothAdaptor;

import lombok.Builder;
import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-24
 * WIT-REV-#0:"WT901BLE58"           0xF9E81A7EA3EC,20,Ua�
 */
@Data
@Builder
public class RevResponse {

    private String header;

    private Integer deviceNum;

    private String deviceName;

    private String deviceAddress;

    private Integer contentLength;

    private String content;
}
