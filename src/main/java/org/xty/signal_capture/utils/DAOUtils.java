package org.xty.signal_capture.utils;

import static java.util.Objects.isNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-27
 */
@Slf4j
public class DAOUtils {
    // 只针对Model
    public static <T> MapSqlParameterSource toParameterSource(T model) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        Field[] fields = model.getClass().getDeclaredFields();

        Arrays.stream(fields).filter(field -> !Modifier.isStatic(field.getModifiers()))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(model);
                        mapSqlParameterSource.addValue(field.getName(), value);
                    } catch (IllegalAccessException e) {
                        log.error("access field error ", e);
                    }
                });

        return mapSqlParameterSource;
    }

    // 产生sql字符串, 类似" user, name, user_info ", xx_xx的命名方式
    public static String toColumnStr(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldList = Arrays.stream(fields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(field -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                        field.getName()))
                .collect(Collectors.toList());

        return Joiner.on(", ").join(fieldList);
    }

    // 产生sql字符串，类似" :user, :name, :userInfo ", xxXXX的命名方式
    public static String toValueStr(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldList = Arrays.stream(fields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(field -> ":" + field.getName()).collect(Collectors.toList());

        return Joiner.on(", ").join(fieldList);
    }
}
