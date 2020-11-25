package org.xty.signal_capture.main;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
        BeanFactory factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TestBlobStore blobStore = (TestBlobStore) factory.getBean("testBlobStore");
        blobStore.testMakeBucket();
    }

}
