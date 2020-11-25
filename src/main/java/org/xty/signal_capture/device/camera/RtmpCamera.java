package org.xty.signal_capture.device.camera;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.xty.signal_capture.device.BaseCameraDevice;


/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 *  rtmp://58.200.131.2:1935/livetv/hunantv
 */
public class RtmpCamera extends BaseCameraDevice {

    public static RtmpCamera build(String rtmpUrl) throws Exception {
        BaseCameraDevice device = new RtmpCamera();
        device.initRemoteDevice(rtmpUrl, 2);
        return (RtmpCamera) device;
    }

}
