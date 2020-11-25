package org.xty.signal_capture.common.enums;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-23
 */
public enum SensorPosition {
    LEFT_HAND(1),
    RIGHT_HAND(2),
    HEAD(3),
    LEFT_ANKLE(4),
    RIGHT_ANKLE(5)
    ;

    private final int position;

    SensorPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
