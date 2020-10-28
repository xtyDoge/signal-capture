package service.hikvision.response;

import java.util.List;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-23
 */
@Data
public class HikPageResponse<T> {

    private String code;

    private String msg;

    private DataList<T> data;

    private Integer total;

    private Integer pageNo;

    private Integer pageSize;

    private Integer totalPage;

    @Data
    public static class DataList<T> {
        List<T> list;
    }

}
