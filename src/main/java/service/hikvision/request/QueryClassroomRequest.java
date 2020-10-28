package service.hikvision.request;

import lombok.Builder;
import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-23
 */
@Data
@Builder
public class QueryClassroomRequest {

    // 学校id，目前只有北师大
    private String schoolId = "root000000";

    private Integer pageNo = 1;

    private Integer pageSize = 20;
}

