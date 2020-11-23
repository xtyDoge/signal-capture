package model.hik;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-23
 */
@Data
public class HikClassroom {

    // 教室id
    private String regionId;
    // 教室名
    private String regionName;
    // 学校名
    private String parentId;

    private String schoolId;

    private String idPath;

    private String namePath;


}
