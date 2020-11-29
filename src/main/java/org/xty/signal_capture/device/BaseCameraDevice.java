package org.xty.signal_capture.device;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import javax.swing.WindowConstants;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Frame.Type;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-28
 */
@Slf4j
public abstract class BaseCameraDevice {

    // 一个设备需要有哪些东西？
    // 1. grabber 抓取器

    private int deviceId;

    private String cameraUrl;

    private FrameGrabber grabber;

    private FrameFilter frameFilter;

    private ToMat converter;

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
//                grabber.setOption("rtsp_transport", "tcp");

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

    public void readFramesLoop(BlockingQueue<Mat> buffer, int limit)
            throws FrameGrabber.Exception, InterruptedException {

        // 加个计数
        int totalCounts = 0;
        int validCounts = 0;

        while (validCounts < limit) {

            Frame rawFrame = grabber.grabFrame();

            if (isValidFrame(rawFrame)) {
                Mat image = converter.convertToMat(rawFrame);
                if (image != null) {
                    Mat imageCopy = image.clone();
                    buffer.offer(imageCopy);
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

    public void readFramesLoop()
            throws FrameGrabber.Exception {

        List<Mat> frameList = new ArrayList<>();
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        int i = 0;
        while(i < 1000) {
            Frame frame = grabber.grabFrame();
            if (isValidFrame(frame)) {
                Mat image = converter.convertToMat(frame);
                if (image != null) {
                    Mat imageCopy = image.clone();
                    frameList.add(imageCopy);
                    image.release();
                    i += 1;
                }
            }
        }

//        i = 0;
//        Iterator<Mat> iterator = frameList.iterator();
//        while(i < 100) {
//            Mat mat = iterator.next();
//            doExecuteFrame(mat, i);
//            i += 1;
//        }

        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);

        int totalCounts = 0;
        int videoCounts = 0;
        int audioCounts = 0;
        int dataCounts = 0;

        Iterator<Mat> iterator = frameList.iterator();
        while (true) {
            canvas.showImage(converter.convert(iterator.next()));
//            Frame rawFrame = grabber.grabFrame();
//
//            if (isValidFrame(rawFrame)) {
//                canvas.showImage(converter.getBufferedImage(rawFrame));
//            }
//            EnumSet<Type> types = rawFrame.getTypes();
//            if (types.contains(Type.VIDEO)) {
//                videoCounts += 1;
//            }
//            if (types.contains(Type.AUDIO)) {
//                audioCounts += 1;
//            }
//            if (types.contains(Type.DATA)) {
//                dataCounts += 1;
//            }
//            totalCounts += 1;
//            if (totalCounts % 100 == 0) {
//                log.info("total Frames {}, {}% images, {}% audio, {}% data", totalCounts, videoCounts * 100.0 / totalCounts,
//                        audioCounts * 100.0 / totalCounts, dataCounts * 100.0 / totalCounts);
//            }
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
