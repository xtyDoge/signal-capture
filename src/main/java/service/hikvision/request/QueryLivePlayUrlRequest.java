package service.hikvision.request;

import lombok.Builder;
import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-23
 */
@Data
@Builder
public class QueryLivePlayUrlRequest {

    // 设备的 channelId
    private String indexCode;

    // 取值0，1，2
    private Integer streamType;

    // 协议，支持rstp, rtmp
    private String protocol;
}
