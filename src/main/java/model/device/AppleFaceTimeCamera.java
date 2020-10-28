package model.device;

import org.bytedeco.javacv.FrameGrabber.Exception;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-28
 */
public class AppleFaceTimeCamera extends BaseCameraDevice{

    public static AppleFaceTimeCamera build() throws Exception {
        BaseCameraDevice device = new AppleFaceTimeCamera();
        device.initLocalDevice(0, 2);
        return (AppleFaceTimeCamera) device;
    }

}
