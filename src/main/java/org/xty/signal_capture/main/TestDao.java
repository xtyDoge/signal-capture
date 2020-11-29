package org.xty.signal_capture.main;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
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

    private void testSaveMysql() {

        for (int i=0; i < 100; i++) {
            VideoFrame videoFrame = new VideoFrame();
            videoFrame.setSequenceNumber(i);
            videoFrame.setMills(System.currentTimeMillis());
            videoFrame.setUuid("STESRS-");
            cameraService.saveImage(null, videoFrame);
        }
    }

    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TestDao testDao = (TestDao) factory.getBean("testDao");
        testDao.testSaveMysql();
    }

}
