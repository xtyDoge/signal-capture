package org.xty.signal_capture.main;

import java.util.Enumeration;

import com.alibaba.fastjson.JSON;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.xty.signal_capture.common.enums.SerialDevice;
import gnu.io.CommPortIdentifier;
import lombok.extern.slf4j.Slf4j;

import org.xty.signal_capture.dao.NineAxisMotionSensorFrameDao;
import org.xty.signal_capture.model.NineAxisMotionSensorFrame;
import org.xty.signal_capture.model.config.SerialParamConfig;

import org.xty.signal_capture.device.bluetoothAdaptor.WT52HB;
import org.xty.signal_capture.common.exception.SerialCustomException;
import org.xty.signal_capture.utils.NSerialUtils;
import org.xty.signal_capture.utils.SerialUtils;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-18
 */
@Service
@Slf4j
public class TestSerial {

    @Autowired
    private NineAxisMotionSensorFrameDao frameDao;

    public void showAllSerials() {
        // 获取系统中所有的通讯端口
        CommPortIdentifier commPortId;
        Enumeration<CommPortIdentifier> portList;

        portList = CommPortIdentifier.getPortIdentifiers();
        // 循环通讯端口
        while (portList.hasMoreElements()) {
            commPortId = portList.nextElement();
            log.info("port : {}", JSON.toJSON(commPortId));
        }
    }

    private void testReadSerial() throws SerialCustomException {
        // 波特率:115200，停止位 1，校验位 0。
        // 3 个数据包，分别为加速度包，角速度包和角度包，3个数据包顺序输出
        // 每个包11字节
        SerialParamConfig paramConfig = new SerialParamConfig();
        paramConfig.setBaudRate(115200);
        paramConfig.setSerialNumber("/dev/tty.HC-06-DevB");
        paramConfig.setDataBit(7);
        paramConfig.setCheckoutBit(0);
        paramConfig.setStopBit(1);

        SerialUtils serialUtils = new SerialUtils();
        serialUtils.init(paramConfig);

    }

    private void testReadNSerial(String deviceName) throws SerialCustomException {
        NSerialUtils nSerialUtils = new NSerialUtils();
        nSerialUtils.init(deviceName);
        nSerialUtils.readFrame();
        nSerialUtils.terminate();
    }


    private void testReadAndSave() {
        WT52HB device1 = WT52HB.from(SerialDevice.BLUETOOTH_CONNECTOR);
        //        device1.ping();
        device1.name("WT901BLE58");
        device1.connmode("1");
        device1.scan(1);

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    NineAxisMotionSensorFrame frame = device1.getFrameBuffer().take();
                    frame.setUuid("InfoLessonCap1");
                    frameDao.insert(frame);
                } catch (InterruptedException e) {
                    log.error("Take from sensor frame buffer error! ", e);
                }
            }
        });
        thread.start();
        device1.readFrame();

    }

    public static void main(String[] args) throws SerialCustomException {
        BeanFactory factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TestSerial testDao = (TestSerial) factory.getBean("testSerial");
        testDao.testReadAndSave();

    }

}
