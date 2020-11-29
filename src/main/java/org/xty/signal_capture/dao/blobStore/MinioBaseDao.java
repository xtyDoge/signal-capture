package org.xty.signal_capture.dao.blobStore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.bytedeco.javacv.FrameGrabber.Exception;

import io.minio.MinioClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 * Minio是一个类似AmazonS3的对象存储，分为bucket(桶)，Object（对象）两级
 *
 */
@Slf4j
public abstract class MinioBaseDao<T> {

    private static final int BASIC_OBJECT_SIZE = 1920 * 1080 * 3;

    public MinioClient getClient() {
        return MinioDataSource.getInstance();
    }

    @SneakyThrows
    public boolean isBucketExist(String bucketName) {
        return getClient().bucketExists(bucketName);
    }

    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!getClient().bucketExists(bucketName)) {
            getClient().makeBucket(bucketName);
            log.info("Successfully make bucket {}.", bucketName);
        }
    }

    @SneakyThrows
    public void insertObject(String bucketName, String objectName, T object) {
        byte[] objectBytes = convertObjectToByteArray(object);
        ByteArrayInputStream in = new ByteArrayInputStream(objectBytes);
        try {
            getClient().putObject(bucketName, objectName, in, in.available(), (String) null);
        } catch (Exception e) {
            log.error("Insert object error! BucketName {}, ObjectName : {}", bucketName, object, e);
        }
    }

    @SneakyThrows
    public T queryObject(String bucketName, String objectName) {
        InputStream stream = getClient().getObject(bucketName, objectName);
        byte[] buf = new byte[4 * BASIC_OBJECT_SIZE];
        stream.read(buf);
        stream.close();
        return convertByteArrayToObject(buf);
    }


    // 序列化方法
    public abstract byte[] convertObjectToByteArray(T object);

    // 反序列化方法
    public abstract T convertByteArrayToObject(byte[] bytes);

}
