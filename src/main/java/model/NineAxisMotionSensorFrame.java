package model;

import common.annotations.ValueInDb;
import common.enums.SensorPosition;
import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-23
 */
@Data
public class NineAxisMotionSensorFrame {

    // 获取时间戳（毫秒）
    private Long timestamp;

    // 三轴角速度
    private Double angularVelocityX;

    private Double angularVelocityY;

    private Double angularVelocityZ;

    // 三轴加速度
    private Double accelerationX;

    private Double accelerationY;

    private Double accelerationZ;

    // 三轴姿态角

    // 滚转角（x 轴）
    private Double roll;
    // 俯仰角(y 轴)
    private Double pitch;
    // 偏航角(z 轴)
    private Double yaw;

    // 温度
    private Double temperature;

    // 位置
    private SensorPosition sensorPosition;
}
