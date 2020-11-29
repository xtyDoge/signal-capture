package org.xty.signal_capture.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import org.xty.signal_capture.dao.blobStore.ImageMinioDao;
import org.xty.signal_capture.device.camera.AppleFaceTimeCamera;
import org.xty.signal_capture.device.camera.RtmpCamera;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Service
@Slf4j
public class TestBlobStore {

    private static final String BUCKET_NAME = "test-bucket";

    private static BlockingQueue<Mat> frameBuffer = new LinkedBlockingQueue<>(1000);
    private static ExecutorService executors = Executors.newFixedThreadPool(2);

    @Autowired
    private ImageMinioDao imageMinioDao;

    private void testMakeBucket() {
        imageMinioDao.makeBucket(BUCKET_NAME);
    }

    private void testSaveFrame() {
        try {
            AppleFaceTimeCamera camera = AppleFaceTimeCamera.build();

            camera.readFramesLoop(frameBuffer, 10);

            while (frameBuffer.size() != 0) {
                imageMinioDao.insertObject(BUCKET_NAME, String.format("Neo-frame-%d", System.currentTimeMillis()), frameBuffer.take());

            }


        } catch (Exception | InterruptedException e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TestBlobStore blobStore = (TestBlobStore) factory.getBean("testBlobStore");
        blobStore.testSaveFrame();
    }

}
