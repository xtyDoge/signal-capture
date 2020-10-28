package service.hikvision.response;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-23
 */
@Data
public class HikCommonResponse<T> {

    private String code;

    private String msg;

    private T data;

    private Integer total;

    private Integer pageNo;

    private Integer pageSize;

    private Integer totalPage;

}
