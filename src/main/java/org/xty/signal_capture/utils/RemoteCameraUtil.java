package org.xty.signal_capture.utils;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-10-21
 */
@Slf4j
public class RemoteCameraUtil {

    private String cameraUrl;

    private FrameGrabber grabber;

    public void init(String cameraUrl) throws FrameGrabber.Exception {
        init(cameraUrl, 1);
    }

    public void init(String cameraUrl, int scaleDownRatio) throws FrameGrabber.Exception {
        this.cameraUrl = cameraUrl;
        this.grabber = OpenCVFrameGrabber.createDefault(0);
//        this.grabber = FFmpegFrameGrabber.createDefault(cameraUrl);
//        grabber.setOption("rtsp_transport", "tcp");
//        grabber.setOption("rtsp_transport", "tcp");

        // 有可能因为图像太大报错，可能要缩放
        grabber.setImageWidth(grabber.getImageWidth() / scaleDownRatio);
        grabber.setImageHeight(grabber.getImageHeight() / scaleDownRatio);
        FFmpegLogCallback.set();
//        FFmpegFrameGrabber.tryLoad();
        grabber.start();   //开始获取摄像头数据
    }

    public Frame readFrame() throws Exception {
        Frame frame = grabber.grab();
        log.info("timestamp {}, frame detail : {}", System.currentTimeMillis(), "img");
        return frame;
    }



    public void testCamera() throws FrameGrabber.Exception {
        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);

        while (true) {
            if (!canvas.isDisplayable()) {//窗口是否关闭
                grabber.stop();//停止抓取
                System.exit(-1);//退出
            }
            Frame frame = grabber.grab();
            System.out.println(String.format("timestamp %d, frame rate is %f", System.currentTimeMillis(), grabber.getFrameRate()));
            canvas.showImage(frame);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
        }

    }


}
