package org.xty.signal_capture.dao.blobStore;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Repository
@Slf4j
public class ImageMinioDao extends MinioBaseDao<Frame> {

    @Autowired
    private Java2DFrameConverter frameConverter;

    @Override
    public byte[] convertObjectToByteArray(Frame frame) {
        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        BufferedImage image = frameConverter.convert(frame);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", out);
        } catch (IOException e) {
            log.error("convert frame to ByteArray error!", e);
        }
        return out.toByteArray();
    }

    @Override
    public Frame convertByteArrayToObject(byte[] bytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            return frameConverter.convert(image);
        } catch (IOException e) {
            log.error("convert byte to object error!", e);
        }
        return null;
    }
}
