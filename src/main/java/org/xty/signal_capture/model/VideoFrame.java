package org.xty.signal_capture.model;

import org.xty.signal_capture.common.annotations.PrimaryKey;

import lombok.Data;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-29
 * 从 摄像头 或 网络拉取到的视频帧
 */
@Data
public class VideoFrame {

    @PrimaryKey
    private long id;

    // 标记哪一次拍摄的，用作bucketName
    private String uuid;

    private long mills;

    private int sequenceNumber;
}
