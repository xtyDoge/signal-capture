package org.xty.signal_capture.model.bo.bluetoothAdaptor;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-24
 * WIT-LIST-# 0:"T93pro98C9"           0xC998CE7008E7 -60
 */
@Data
public class ListResponse extends CommonResponse{

    private Integer signalIntensity;
}
