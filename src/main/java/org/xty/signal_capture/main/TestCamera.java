package org.xty.signal_capture.main;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.stereotype.Service;

import org.xty.signal_capture.common.exception.CameraCustomException;
import org.xty.signal_capture.device.camera.RtmpCamera;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Service
@Slf4j
public class TestCamera {

    private void testLocalCamera() throws CameraCustomException {

    }

    private void testRemoteCamera() {
        try {
            RtmpCamera camera = RtmpCamera.build("rtmp://58.200.131.2:1935/livetv/hunantv");
            camera.testCamera();
        } catch (Exception e) {
            log.error("", e);
        }


    }

}
