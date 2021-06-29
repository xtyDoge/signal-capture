package org.xty.signal_capture.main;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.xty.signal_capture.common.exception.SerialCustomException;
import org.xty.signal_capture.dao.blobStore.ImageMinioDao;
import org.xty.signal_capture.device.bluetoothAdaptor.WT52HB;
import org.xty.signal_capture.device.camera.RtmpCamera;
import org.xty.signal_capture.service.CameraService;
import org.xty.signal_capture.service.SerialService;
import org.xty.signal_capture.service.hikvision.HikApiUtils;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2021-01-05
 */
@Lazy
@Service
@Slf4j
public class SyncCameraSerial {

    @Autowired
    private SerialService serialService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ImageMinioDao imageMinioDao;

    private ExecutorService executor = Executors.newCachedThreadPool();

    public void mainPipeline(String uuid, String bucketName) throws InterruptedException {

        // 0. 获取accessToken
        String accessToken = HikApiUtils.getAccessToken("21907215", "CLUNV45SK0uTVZqdXlsW");
        String livePlayUrl =  HikApiUtils.getLivePlayUrl(accessToken, 0, "38f07e271d854e83b82d85e5e5ea4153", 0, "rtmp");
        log.info("{}", livePlayUrl);

        // 1. 初始化各个设备
        RtmpCamera camera = cameraService.initCamera(livePlayUrl);
        WT52HB sensors = serialService.initAllSensors("WT901BLE58", "1");
        imageMinioDao.makeBucket(bucketName);
        sensors.readFrame();


        // 2.放置传感器读取线程
        Callable<Void> readSensorsRunnable = () -> {
            while (true) {
                serialService.readFromAllSensors(uuid, sensors);
            }
        };

        // 3.放置摄像头读取线程
        Callable<Void> readCameraRunnable = () -> {
            try {
                camera.readFramesLoop(uuid, (videoFrame) -> {
                    // videoFrame
                    cameraService.saveImageLoop(bucketName, videoFrame);
                });
            } catch (Exception e) {
                log.error("", e);
            }
            return null;
        };

        // 4.启动两个线程
        executor.invokeAll(Lists.newArrayList(readCameraRunnable, readSensorsRunnable));

    }


    public static void main(String[] args) throws SerialCustomException, InterruptedException {
        BeanFactory factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        SyncCameraSerial syncCameraSerial = (SyncCameraSerial) factory.getBean("syncCameraSerial");
        syncCameraSerial.mainPipeline("xty-teach-turnnew", "xty-teach-turnnew");
    }

}
