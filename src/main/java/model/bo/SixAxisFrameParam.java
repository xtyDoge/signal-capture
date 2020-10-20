package model.bo;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-19
 */
@Data
public class SixAxisFrameParam {

    private AccelerationParam accelerationParam;

    private AngularVelocityParam angularVelocityParam;

    private AngleParam angleParam;
}
