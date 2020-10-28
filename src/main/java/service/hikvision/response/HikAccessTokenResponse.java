package service.hikvision.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-23
 */
@Data
public class HikAccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;
}
