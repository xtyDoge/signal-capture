package service.hikvision.api;

import com.google.common.util.concurrent.ListenableFuture;

import model.hik.HikClassroom;
import model.hik.HikLivePlayUrl;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import service.hikvision.request.QueryClassroomRequest;
import service.hikvision.request.QueryLivePlayUrlRequest;
import service.hikvision.response.HikAccessTokenResponse;
import service.hikvision.response.HikCommonResponse;
import service.hikvision.response.HikPageResponse;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-21
 */
public interface HikApi {

    @Headers({ "Content-Type: application/json" })
    @POST("oauth/token")
    ListenableFuture<HikAccessTokenResponse> getAccessToken(@Query("client_id") String appKey, @Query("client_secret") String appSecret);

    @Headers({ "Content-Type: application/json" })
    @POST("api/efd/v1/classroom/search")
    ListenableFuture<HikPageResponse<HikClassroom>> queryClassroom(@Header("access_token") String accessToken, @Body
            QueryClassroomRequest request);

    @Headers({ "Content-Type: application/json" })
    @POST("api/erm/v1/live/play/url")
    ListenableFuture<HikCommonResponse<HikLivePlayUrl>> queryLivePlayUrl(@Header("access_token") String accessToken, @Header("domainId") Integer domainId,
            @Body QueryLivePlayUrlRequest request);

}
