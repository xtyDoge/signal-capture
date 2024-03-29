package org.xty.signal_capture.dao.blobStore;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */

@Slf4j
public class MinioDataSource {
    // String endpoint, String accessKey, String secretKey
    private volatile static MinioClient minioClient;

    public static MinioClient getInstance() {
        if (minioClient == null) {
            synchronized (MinioClient.class) {
                if (minioClient == null) {
                    try {
                        minioClient = new MinioClient("http://xty.com:9000", "minioadmin", "minioadmin");
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }
        return minioClient;
    }

}
