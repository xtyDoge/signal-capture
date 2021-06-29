package org.xty.signal_capture.device;

import java.util.EnumSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Frame.Type;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.xty.signal_capture.model.VideoFrame;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-28
 */
@Slf4j
public abstract class BaseCameraDevice {

    // 一个设备需要有哪些东西？
    // 1. grabber 抓取器
    private AtomicInteger count = new AtomicInteger(0);

    private int deviceId;

    private String cameraUrl;

    private FrameGrabber grabber;

    private FrameFilter frameFilter;

    private ToMat converter;

    private ExecutorService executor = Executors.newCachedThreadPool();

    public void init(int deviceId) throws FrameGrabber.Exception, FrameFilter.Exception {
        initLocalDevice(deviceId, 1);
    }

    public void init(String cameraUrl) throws FrameGrabber.Exception, FrameFilter.Exception {
        initRemoteDevice(cameraUrl, 1);
    }

    public void initLocalDevice(int deviceId, int scaleDownRatio) throws FrameGrabber.Exception, FrameFilter.Exception {
        this.deviceId = deviceId;
        this.grabber = OpenCVFrameGrabber.createDefault(this.deviceId);
        this.frameFilter = new FFmpegFrameFilter("scale=iw/2:-1", grabber.getImageWidth(), grabber.getImageHeight());
        this.converter = new ToMat();
        frameFilter.start();
        grabber.start();   //开始获取摄像头数据
    }

    public void initRemoteDevice(String cameraUrl, int scaleDownRatio)
            throws FrameGrabber.Exception, FrameFilter.Exception {
        this.cameraUrl = cameraUrl;
        this.grabber = FFmpegFrameGrabber.createDefault(cameraUrl);
        this.converter = new ToMat();
        grabber.setOption("rtmp_transport", "tcp");
        grabber.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//        grabber.setOption("rtsp_transport", "tcp");

        this.frameFilter = new FFmpegFrameFilter("scale=iw/2:-1", grabber.getImageWidth(), grabber.getImageHeight());
        frameFilter.start();
        grabber.start();   //开始获取摄像头数据
    }

    public double getFrameRate() {
        return grabber.getFrameRate();
    }

    public Frame readFrame() throws Exception {
        Frame frame = grabber.grab();
        log.info("timestamp {}, frame detail : {}", System.currentTimeMillis(), "img");
        return frame;
    }

    public void readFramesLoop(BlockingQueue<VideoFrame> buffer, int limit)
            throws FrameGrabber.Exception, InterruptedException {

        // 加个计数
        int totalCounts = 0;
        int validCounts = 0;

        while (validCounts < limit) {

            Frame rawFrame = grabber.grabFrame();

            if (isValidFrame(rawFrame)) {
                Mat image = converter.convertToMat(rawFrame);
                if (image != null) {
                    // 用videoFrame封装一下
                    VideoFrame videoFrame = new VideoFrame();
                    Mat imageCopy = image.clone();
                    videoFrame.setMills(System.currentTimeMillis());
                    videoFrame.setImage(imageCopy);
                    videoFrame.setSequenceNumber(count.getAndAdd(1));
                    buffer.offer(videoFrame);
                    image.release();
                    validCounts += 1;
                }
            }
            totalCounts += 1;

            if (totalCounts % 100 == 0) {
                log.info("total Frames {}, frameBuffer size : {}, {}%  images", totalCounts, buffer.size(), validCounts * 100.0 / totalCounts);
            }
        }
        log.info("Get Buffer complete.");
    }


    public void readFramesLoop(String uuid, Consumer<VideoFrame> handleVideoFrame)
            throws FrameGrabber.Exception {

        // 加个计数
        int totalCounts = 0;
        int validCounts = 0;

        while (true) {

            Frame rawFrame = grabber.grabFrame();

            if (isValidFrame(rawFrame)) {
                Mat image = converter.convertToMat(rawFrame);
                if (image != null) {
                    // 用videoFrame封装一下
                    VideoFrame videoFrame = new VideoFrame();
                    Mat imageCopy = image.clone();
                    videoFrame.setMills(System.currentTimeMillis());
                    videoFrame.setImage(imageCopy);
                    videoFrame.setSequenceNumber(count.getAndAdd(1));
                    videoFrame.setUuid(uuid);
                    // 调一下回调
                    executor.execute(() -> handleVideoFrame.accept(videoFrame));

                    image.release();
                    validCounts += 1;
                }
            }
            totalCounts += 1;

            if (totalCounts % 100 == 0) {
                log.info("total Frames {}, {}%  images", totalCounts, validCounts * 100.0 / totalCounts);
            }
        }
    }

    private boolean isValidFrame(Frame frame) {

        // 首先，空帧肯定不是
        if (frame == null) {
            return false;
        }
        // 类型不是VIDEO也不是
        EnumSet<Type> videoOrAudio = frame.getTypes();
        if (videoOrAudio.contains(Type.AUDIO) || videoOrAudio.contains(Type.DATA)) {
            return false;
        }
        return true;
    }

    @Deprecated
    public static void doExecuteFrame(Frame f, int index, ToMat converter) {
        if (null == f || null == f.image) {
            return;
        }
        String FileName = "/Users/xutianyou/Desktop/tianyouxu/temp/";
        Mat mat = converter.convert(f);
        opencv_imgcodecs.imwrite(FileName + "NORMAL_" + index + "sls.png", mat);//存储图像
    }

    @Deprecated
    public static void doExecuteFrame(Mat mat, int index) {
        String FileName = "/Users/xutianyou/Desktop/tianyouxu/temp/";
        opencv_imgcodecs.imwrite(FileName + "STORE_" + index + "sls.png", mat);//存储图像
    }
}
