package org.xty.signal_capture.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.xty.signal_capture.dao.blobStore.ImageMinioDao;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Service
public class TestBlobStore {

    @Autowired
    private ImageMinioDao imageMinioDao;

    private void testMakeBucket() {
        imageMinioDao.makeBucket("testBucket");
    }

    public static void main(String[] args) {
        TestBlobStore blobStore = new TestBlobStore();
        blobStore.testMakeBucket();
    }

}
