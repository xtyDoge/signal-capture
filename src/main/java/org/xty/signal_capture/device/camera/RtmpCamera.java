package org.xty.signal_capture.device.camera;

import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.xty.signal_capture.device.BaseCameraDevice;

import lombok.extern.slf4j.Slf4j;


/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 *  rtmp://58.200.131.2:1935/livetv/hunantv
 */
@Slf4j
public class RtmpCamera extends BaseCameraDevice {

    public static RtmpCamera build(String rtmpUrl) throws Exception {
        BaseCameraDevice device = new RtmpCamera();
        try {
            device.initRemoteDevice(rtmpUrl, 4);
        } catch (FrameFilter.Exception e) {
            log.error("", e);
        }
        return (RtmpCamera) device;
    }

}
