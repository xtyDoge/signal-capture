package service.hikvision;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Lazy;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import model.hik.HikClassroom;
import model.hik.HikLivePlayUrl;
import retrofit2.Retrofit;
import retrofit2.adapter.guava.GuavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import service.hikvision.api.HikApi;
import service.hikvision.request.QueryClassroomRequest;
import service.hikvision.request.QueryLivePlayUrlRequest;
import service.hikvision.response.HikCommonResponse;
import service.hikvision.response.HikPageResponse;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-21
 */
@Lazy
@Slf4j
public class HikApiUtils {

    private static final HikApi HIK_API = new Retrofit.Builder()
            .baseUrl("http://172.22.99.204/artemis/")
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(GuavaCallAdapterFactory.create())
            .build().create(HikApi.class);

    // 获取海康Token
    public static String getAccessToken(String appKey, String appSecret) {
        String accessToken = "";
        try {
            accessToken =  HIK_API.getAccessToken(appKey, appSecret).get(20, TimeUnit.SECONDS).getAccessToken();
        } catch (Exception e) {
            log.error("Get AccessToken error! ", e);
        }
        return accessToken;
    }

    // 按教室regionId获取直播url
    public static String getLivePlayUrl(String accessToken, Integer domainId, String indexCode, Integer streamType, String protocol) {
        QueryLivePlayUrlRequest request = QueryLivePlayUrlRequest.builder()
                .indexCode(indexCode)
                .streamType(streamType)
                .protocol(protocol)
                .build();
        String url = "";
        try {
            HikCommonResponse<HikLivePlayUrl> response = HIK_API.queryLivePlayUrl(accessToken, domainId, request).get(20, TimeUnit.SECONDS);
            log.info("{}",  JSON.toJSON(response));
            return response.getData().getUrl();
        } catch (Exception e) {
            log.error("Get livePlayUrl error!", e);
        }
        return url;
    }

    // 查询学校下所有教室
    public static List<HikClassroom> getClassrooms(String accessToken, String schoolId, Integer pageNo, Integer pageSize) {
        QueryClassroomRequest request = QueryClassroomRequest.builder()
                .schoolId(schoolId)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .build();
        try {
            HikPageResponse<HikClassroom>
                    response = HIK_API.queryClassroom(accessToken, request).get(20, TimeUnit.SECONDS);
            log.info("{}",  JSON.toJSON(response));
            return response.getData().getList();
        } catch (Exception e) {
            log.error("Get classrooms error!", e);
        }
        return new ArrayList<>();
    }


    public static void main(String[] args) {
        String accessToken = getAccessToken("21907215", "CLUNV45SK0uTVZqdXlsW");
        String livePlayUrl =  getLivePlayUrl(accessToken, 0, "38f07e271d854e83b82d85e5e5ea4153", 0, "rtmp");
        log.info("{}", livePlayUrl);
        System.exit(0);
    }





}
