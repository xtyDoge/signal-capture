package dao;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.github.phantomthief.util.CursorIterator;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Iterables;

import common.annotations.PrimaryKey;
import dao.builder.SqlQueryBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-08
 */
@Lazy
@Repository
@Slf4j
public abstract class BaseDao<T> {

    @Autowired
    private DataSources dataSources;

    private static final int BUFFER_SIZE = 500;

    Map<Class, RowMapper> ROW_MAPPER_MAP = new ConcurrentHashMap<>();
    Map<Class, String> TABLE_NAME_MAP = new ConcurrentHashMap<>();
    Map<Class, String> PRIMARY_KEY_NAME_MAP = new ConcurrentHashMap<>();

    abstract Class<T> getClazz();

    public NamedParameterJdbcTemplate getReader() {
        return dataSources.getReader();
    }

    public NamedParameterJdbcTemplate getWriter() {
        return dataSources.getWriter();
    }


    @SuppressWarnings("unchecked")
    public RowMapper<T> getRowMapper() {
        return ROW_MAPPER_MAP.computeIfAbsent(getClazz(), e -> {
            BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper(e);
            // Set whether we're defaulting Java primitives in the case of mapping a null value
            rowMapper.setPrimitivesDefaultedForNullValue(true);
            return rowMapper;
        });
    }

    public String getTableName() {
        return TABLE_NAME_MAP.computeIfAbsent(getClazz(), (clazz) -> {
            String tableName = clazz.getSimpleName();
            tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, tableName);

            return tableName;
        });
    }

    public String getPrimaryKeyName() {
        return PRIMARY_KEY_NAME_MAP.computeIfAbsent(getClazz(),
                (clazz) -> Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> field.getDeclaredAnnotation(PrimaryKey.class) != null)
                        .findAny().get().getName());
    }

    public long getPrimaryKey(Object model) {
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


    public T getById(long id) {
        return getReader().queryForObject("select * from " + getTableName() + " where id = :id",
                new MapSqlParameterSource("id", id), getClazz());
    }

    public Stream<T> listByKeyDesc(List<Map.Entry<String, Object>> entryList) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        List<String> conditions = new ArrayList<>();

        entryList.forEach(entry -> {
            conditions.add(format(" %s=:%s ", CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entry.getKey()),
                    entry.getKey()));
            mapSqlParameterSource.addValue(entry.getKey(), entry.getValue());
        });

        final String dbPrimaryKeyName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                getPrimaryKeyName());

        return CursorIterator.<Long, T>newGenericBuilder().bufferSize(BUFFER_SIZE)
                .start(Long.MAX_VALUE).cursorExtractor(this::getPrimaryKey)
                .build((cursor, limit) -> getReader().query(
                        new SqlQueryBuilder()
                                .select("*")
                                .from(getTableName())
                                .where(conditions)
                                .and(format("%s<=:cursor", dbPrimaryKeyName))
                                .orderBy(dbPrimaryKeyName)
                                .desc()
                                .limit(":limit")
                                .build(),
                        mapSqlParameterSource.addValue("cursor", cursor)
                                .addValue("limit", limit),
                        getRowMapper()))
                .stream();
    }

//    public void batchInsert(List<T> records) {
//        AtomicInteger count = new AtomicInteger();
//        // 分批次写入, 单次写入500条
//        Iterables.partition(records, 500).forEach(items -> {
//            String insertSQL = "INSERT IGNORE INTO " + getTableName() + "(photo_id, time) values(:photoId, :time)";
//            SqlParameterSource[] batchArgs = new MapSqlParameterSource[items.size()];
//            int i = 0;
//            for (PartnerHotPhoto item : items) {
//                batchArgs[i++] =
//                        new MapSqlParameterSource("photoId", item.getPhotoId()).addValue("time", item.getTime());
//            }
//            writeTemplate.batchUpdate(insertSQL, batchArgs);
//            count.addAndGet(items.size());
//        });
//        return count.get();
//    }

}
