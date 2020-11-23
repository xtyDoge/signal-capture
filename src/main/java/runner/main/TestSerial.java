package runner.main;

import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bytedeco.javacv.FrameGrabber.Exception;

import com.alibaba.fastjson.JSON;

import gnu.io.CommPortIdentifier;
import lombok.extern.slf4j.Slf4j;
import model.config.SerialParamConfig;
import model.device.AppleFaceTimeCamera;
import model.device.WitBWT901CL;
import common.exception.SerialCustomException;
import utils.NSerialUtils;
import utils.SerialUtils;

/**
 * @author xutianyou <xutianyou@mail.bnu.edu.cn>
 * Created on 2020-10-18
 */
@Slf4j
public class TestSerial {

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

    public static void main(String[] args) throws SerialCustomException {


        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(() -> {
            try {
                AppleFaceTimeCamera camera1 = AppleFaceTimeCamera.build();
                WitBWT901CL device1 = WitBWT901CL.fromPortName("/dev/tty.HC-06-DevB");

                while (true) {
                    camera1.readFrame();
                    log.info("{}", JSON.toJSON(device1.readOneFrame()));
                }


            } catch (SerialCustomException | Exception e) {
                e.printStackTrace();
            }
        });

    }

}
