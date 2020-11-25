package org.xty.signal_capture.service;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Service
public class CameraService {

    private final Java2DFrameConverter frameConverter = new Java2DFrameConverter();

    public void saveImage(Frame frame, String bucketName, String imageName) {

    }


}
