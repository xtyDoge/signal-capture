package model.bo.bluetoothAdaptor;

import lombok.Builder;
import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-24
 * WIT-LINK-#<NUM>:”<NAME>” 0xAAAAAAAAAAAA\r\n
 */
@Data
@Builder
public class LinkResponse {

    private String header;

    private Integer deviceNum;

    private String deviceName;

    private String deviceAddress;

}
