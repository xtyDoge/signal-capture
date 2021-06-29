package org.xty.signal_capture.main;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.xty.signal_capture.dao.NineAxisMotionSensorFrameDao;
import org.xty.signal_capture.model.NineAxisMotionSensorFrame;
import org.xty.signal_capture.model.VideoFrame;
import org.xty.signal_capture.service.CameraService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-29
 */
@Service
@Slf4j
public class TestDao {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private NineAxisMotionSensorFrameDao sensorFrameDao;

    private void testSaveMysql() {

        for (int i=0; i < 100; i++) {
            VideoFrame videoFrame = new VideoFrame();
            videoFrame.setSequenceNumber(i);
            videoFrame.setMills(System.currentTimeMillis());
            videoFrame.setUuid("STESRS-");
        }
    }


    private void testSaveSensorsMysql() {

        for (int i=0; i < 1; i++) {
            NineAxisMotionSensorFrame frame = new NineAxisMotionSensorFrame();
            frame.setUuid("InfoLessonCap1");
            frame.setSensorPosition(1);
            frame.setAccelerationX(1.0);
            frame.setAccelerationY(2.0);
            frame.setAccelerationZ(3.0);
            frame.setAngularVelocityX(-1.0);
            frame.setAngularVelocityY(-2.0);
            frame.setAngularVelocityZ(-3.0);
            frame.setPitch(3.0);
            frame.setRoll(-23.0);
            frame.setYaw(-32.0);
            frame.setMills(System.currentTimeMillis());
            frame.setDeviceAddress("0xDSEQASDFA");
            sensorFrameDao.insert(frame);

        }
    }

    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TestDao testDao = (TestDao) factory.getBean("testDao");
        testDao.testSaveSensorsMysql();
    }

}
