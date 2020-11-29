package org.xty.signal_capture.main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.WindowConstants;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import org.xty.signal_capture.device.camera.AppleFaceTimeCamera;
import org.xty.signal_capture.device.camera.RtmpCamera;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Service
@Slf4j
public class TestCamera {

    private static BlockingQueue<Mat> frameBuffer = new LinkedBlockingQueue<>(1000);
    private static ExecutorService executors = Executors.newFixedThreadPool(2);

    @Autowired
    private Java2DFrameConverter converter;

    @Autowired
    private ToMat toMat;

    private void testPlay(BlockingQueue<Mat> frameBuffer, double frameRate) {

        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);

        // 取帧播放
        Frame frame;
        while (true) {
            try {
                frame = toMat.convert(frameBuffer.take());
                canvas.showImage(frame);
                Thread.sleep((long) (1000 / frameRate));
                log.info("frame remains {}", frameBuffer.size());
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }
    }

    private void testLocalCamera() {
        try {
            AppleFaceTimeCamera camera = AppleFaceTimeCamera.build();
            Thread grabThread = new Thread(() -> {
                try {
                    camera.readFramesLoop(frameBuffer, 1000);
                } catch (Exception | InterruptedException e) {
                    log.error("", e);
                }
            });

            Thread playThread = new Thread(() -> testPlay(frameBuffer, camera.getFrameRate()));

            grabThread.start();
            log.info("Ready for another start");
            playThread.start();

        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void testRemoteCamera() {
        try {
            RtmpCamera camera = RtmpCamera.build("rtmp://58.200.131.2:1935/livetv/hunantv");
            Thread grabThread = new Thread(() -> {
                try {
                    camera.readFramesLoop(frameBuffer, Integer.MAX_VALUE);
                } catch (Exception | InterruptedException e) {
                    log.error("", e);
                }
            });

            Thread playThread = new Thread(() -> {
                testPlay(frameBuffer, 35);
            });


            grabThread.start();
            Thread.sleep(500);
            log.info("Ready for another start");
            playThread.start();
        } catch (Exception | InterruptedException e) {
            log.error("", e);
        }
    }

    private void testIdiotPlay() {
        try {
//            RtmpCamera camera = RtmpCamera.build("rtmp://58.200.131.2:1935/livetv/hunantv");
            AppleFaceTimeCamera camera = AppleFaceTimeCamera.build();
            Thread playThread = new Thread(() -> {
                try {
                    camera.readFramesLoop();
                } catch (Exception e) {
                    log.error("", e);
                }
            });
            log.info("Ready for another start");
            playThread.start();

        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TestCamera testCamera = (TestCamera) factory.getBean("testCamera");
        testCamera.testRemoteCamera();
    }

}
