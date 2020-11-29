package org.xty.signal_capture.device.camera;

import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameGrabber.Exception;

import org.xty.signal_capture.device.BaseCameraDevice;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-28
 */
@Slf4j
public class AppleFaceTimeCamera extends BaseCameraDevice {

    public static AppleFaceTimeCamera build() throws Exception {
        BaseCameraDevice device = new AppleFaceTimeCamera();
        try {
            device.initLocalDevice(0, 4);
        } catch (FrameFilter.Exception e) {
            log.error("", e);
        }
        return (AppleFaceTimeCamera) device;
    }

}
