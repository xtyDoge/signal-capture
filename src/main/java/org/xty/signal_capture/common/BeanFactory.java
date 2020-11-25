package org.xty.signal_capture.common;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.charset.Charset;
import java.util.Objects;

import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-25
 */
@Deprecated
@Service
public final class BeanFactory {

    private static final Logger logger = getLogger(BeanFactory.class);

    private static volatile GenericApplicationContext applicationContext;
    private static volatile boolean initByBeanFactory = false;
    private static volatile RuntimeException initFailedThrowable;

    @SuppressFBWarnings("DC_PARTIALLY_CONSTRUCTED")
    public static void init() {
        if (applicationContext == null) {
            synchronized (BeanFactory.class) {
                if (applicationContext == null) {
                    if (initFailedThrowable != null) {
                        throw initFailedThrowable;
                    }
                    if (initByBeanFactory) {
                        // 为了检测出bean里field有用getBean()声明的成员变量
                        // 但是并不能100%检测出，比如如果有一个static的变量，在spring扫到它之前就被引用并触发了getBean就没戏了
                        // 但是能检测出一点儿也比什么都检测不出来好。
                        throw new RuntimeException(
                                "found invalid spring declare. call beanfactory in bean field? first init stack:");
                    } else {
                        initByBeanFactory = true;
                    }
                    Charset defaultCharset = defaultCharset();
                    logger.info("init bean factory self. default charset:{}", defaultCharset);
                    if (!Objects.equals(defaultCharset, UTF_8)) {
                        logger.warn("FOUND INVALID DEFAULT CHARSET:{}", defaultCharset);
                    }
                    try {
                        applicationContext = new GenericApplicationContext(
                                new ClassPathXmlApplicationContext("classpath*:spring/*.xml"));
                        applicationContext.refresh();
                    } catch (RuntimeException e) {
                        initFailedThrowable = e;
                        throw e;
                    }
                }
            }
        }
    }

    @Deprecated
    public static <T> T getBean(Class<T> clazz) {
        init();
        return applicationContext.getBean(clazz);
    }

    @Deprecated
    public static <T> T getBean(Class<T> clazz, Object... args) {
        init();
        return applicationContext.getBean(clazz, args);
    }
}