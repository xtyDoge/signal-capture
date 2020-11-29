package org.xty.signal_capture.service;

import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xty.signal_capture.dao.VideoFrameDao;
import org.xty.signal_capture.dao.blobStore.ImageMinioDao;
import org.xty.signal_capture.model.VideoFrame;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Service
public class CameraService {

    @Autowired
    private ImageMinioDao imageMinioDao;

    @Autowired
    private VideoFrameDao videoFrameDao;

    // 首先要确定图片是哪一次拍摄存的，其次要打个时间戳，再其次要有个序号
    public void saveImage(Mat mat, VideoFrame videoFrame) {
        // 1. 落盘到miniIo
//        imageMinioDao.makeBucket(videoFrame.getUuid());
//        imageMinioDao.insertObject(videoFrame.getUuid(), getObjectName(videoFrame), mat);
        // 2. 落库到mysql
        videoFrameDao.insert(videoFrame);
    }

    public void getImage(String uuid, long fromTimestamp, long toTimeStamp) {

    }

    private String getObjectName(VideoFrame videoFrame) {
        return videoFrame.getMills() + "_" + videoFrame.getSequenceNumber();
    }


}
