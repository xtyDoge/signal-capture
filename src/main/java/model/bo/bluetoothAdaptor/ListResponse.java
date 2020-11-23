package model.bo.bluetoothAdaptor;

import lombok.Builder;
import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-24
 * WIT-LIST-# 0:"T93pro98C9"           0xC998CE7008E7 -60
 */
@Data
@Builder
public class ListResponse {

    private String header;

    private Integer deviceNum;

    private String deviceName;

    private String deviceAddress;

    private Integer signalIntensity;
}
