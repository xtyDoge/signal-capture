package org.xty.signal_capture.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.WindowConstants;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.video.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.xty.signal_capture.dao.VideoFrameDao;
import org.xty.signal_capture.dao.blobStore.ImageMinioDao;
import org.xty.signal_capture.device.camera.RtmpCamera;
import org.xty.signal_capture.model.VideoFrame;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Lazy
@Service
@Slf4j
public class CameraService {

    private static BlockingQueue<VideoFrame> frameBuffer = new LinkedBlockingQueue<>(1000);

    @Autowired
    private ImageMinioDao imageMinioDao;

    @Autowired
    private VideoFrameDao videoFrameDao;

    // 初始化
    public RtmpCamera initCamera(String cameraUrl) {
        try {
            RtmpCamera camera = RtmpCamera.build(cameraUrl);
            return camera;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    // 读取线程，专门读图到缓存中用的
    public void readImageLoop(RtmpCamera camera) {
        try {
            camera.readFramesLoop(frameBuffer, Integer.MAX_VALUE);
        } catch (Exception | InterruptedException e) {
            log.error("", e);
        }
    }

    // 保存线程，专门保存缓存中的图落库用的
    public void saveImageLoop(String bucketName) {
        try {
            while (frameBuffer.size() != 0) {
                VideoFrame frame = frameBuffer.take();
                frame.setUuid("InfoLessonCap");

                imageMinioDao.insertObject(bucketName,
                        String.format("%s_%d_%d", frame.getUuid(), frame.getMills(), frame.getSequenceNumber()), frame.getImage());
                videoFrameDao.insert(frame);
            }

        } catch (InterruptedException e) {
            log.error("", e);
        }
    }


    // 保存图
    public void saveImageLoop(String bucketName, VideoFrame frame) {
        imageMinioDao.insertObject(bucketName,
                        String.format("%s_%d_%d", frame.getUuid(), frame.getMills(), frame.getSequenceNumber()), frame.getImage());
        videoFrameDao.insert(frame);
    }

}
