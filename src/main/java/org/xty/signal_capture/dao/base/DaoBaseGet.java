package org.xty.signal_capture.dao.base;

import static java.lang.String.format;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.xty.signal_capture.dao.builder.SqlQueryBuilder;

import com.github.phantomthief.util.CursorIterator;
import com.google.common.base.CaseFormat;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-27
 */
public interface DaoBaseGet<T> extends DaoBase<T> {

    int BUFFER_SIZE = 1000;

    NamedParameterJdbcTemplate getReader();

    //通过唯一key获得行
    default <Key> T getByKey(String keyName, Key key) {
        try {
            final String dbKeyName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, keyName);

            return getReader().queryForObject(
                    new SqlQueryBuilder()
                            .select("*")
                            .from(getTableName())
                            .where(format("%s=:%s", dbKeyName, keyName))
                            .build(),
                    new MapSqlParameterSource(keyName, key), getRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    default <Key> Stream<T> listByKeyDesc(String keyName, Key key) {
        List<Map.Entry<String, Object>> entryList = new ArrayList<>();
        entryList.add(new AbstractMap.SimpleImmutableEntry<>(keyName, key));

        return listByKeyDesc(entryList);
    }

    default Stream<T> listByKeyDesc(List<Entry<String, Object>> entryList) {
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

}
