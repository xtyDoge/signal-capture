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

import org.xty.signal_capture.dao.VideoFrameDao;
import org.xty.signal_capture.dao.blobStore.ImageMinioDao;
import org.xty.signal_capture.device.camera.AppleFaceTimeCamera;
import org.xty.signal_capture.device.camera.RtmpCamera;
import org.xty.signal_capture.model.VideoFrame;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Service
@Slf4j
public class TestBlobStore {

    private static final String BUCKET_NAME = "test-bucket";

    private static BlockingQueue<VideoFrame> frameBuffer = new LinkedBlockingQueue<>(1000);
    private static ExecutorService executors = Executors.newFixedThreadPool(2);

    @Autowired
    private ImageMinioDao imageMinioDao;

    @Autowired
    private VideoFrameDao videoFrameDao;

    private void testMakeBucket() {
        imageMinioDao.makeBucket(BUCKET_NAME);
    }

    private void testSaveFrame() {
        try {
            RtmpCamera camera = RtmpCamera.build("rtmp://58.200.131.2:1935/livetv/hunantv");

            camera.readFramesLoop(frameBuffer, Integer.MAX_VALUE);

            while (frameBuffer.size() != 0) {
                VideoFrame frame = frameBuffer.take();
                frame.setUuid("InfoLessonCap");

                imageMinioDao.insertObject(BUCKET_NAME,
                        String.format("%s_%d_%d", frame.getUuid(), frame.getMills(), frame.getSequenceNumber()), frame.getImage());
                videoFrameDao.insert(frame);
            }


        } catch (Exception | InterruptedException e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TestBlobStore blobStore = (TestBlobStore) factory.getBean("testBlobStore");
        blobStore.testSaveFrame();
        System.exit(0);
    }

}
