package org.xty.signal_capture.model.bo.bluetoothAdaptor;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2021-01-04
 */
@Data
public class ConlistResponse extends CommonResponse {

    private Integer contentLength;

    private byte[] content;
}
