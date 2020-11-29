package org.xty.signal_capture.dao.base;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.phantomthief.util.CursorIterator;
import com.google.common.base.CaseFormat;

import org.xty.signal_capture.common.annotations.PrimaryKey;
import org.xty.signal_capture.dao.builder.SqlQueryBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-08
 */
@Lazy
@Repository
public interface DaoBase<T> {

    Map<Class, RowMapper> ROW_MAPPER_MAP = new ConcurrentHashMap<>();
    Map<Class, String> TABLE_NAME_MAP = new ConcurrentHashMap<>();
    Map<Class, String> PRIMARY_KEY_NAME_MAP = new ConcurrentHashMap<>();

     Class<T> getClazz();

    @SuppressWarnings("unchecked")
    default RowMapper<T> getRowMapper() {
        return ROW_MAPPER_MAP.computeIfAbsent(getClazz(), e -> {
            BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(e);
            // Set whether we're defaulting Java primitives in the case of mapping a null value
            rowMapper.setPrimitivesDefaultedForNullValue(true);
            return rowMapper;
        });
    }

    default String getTableName() {
        return TABLE_NAME_MAP.computeIfAbsent(getClazz(), (clazz) -> {
            String tableName = clazz.getSimpleName();
            tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tableName);

            return tableName;
        });
    }

    default String getPrimaryKeyName() {
        return PRIMARY_KEY_NAME_MAP.computeIfAbsent(getClazz(),
                (clazz) -> Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> field.getDeclaredAnnotation(PrimaryKey.class) != null)
                        .findAny().get().getName());
    }

    default long getPrimaryKey(Object model) {
        return (long) getField(model, getPrimaryKeyName());
    }

    //可以获取 private 且无 get 方法的 field 值
    static Object getField(Object target, String fieldName) {
        Object value;
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            value = field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            value = null;
        }
        return value;
    }

}
