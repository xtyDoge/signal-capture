package org.xty.signal_capture.dao.base;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.xty.signal_capture.dao.builder.SqlUpdateBuilder;
import org.xty.signal_capture.utils.DAOUtils;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-27
 */
public interface DaoBaseInsert<T> extends DaoBase<T> {

    NamedParameterJdbcTemplate getWriter();

    default int insert(T model) {
        return insert(model, null);
    }

    default int insert(T model, NamedParameterJdbcTemplate template) {
        if (template == null) {
            template = getWriter();
        }

        return template.update(
                new SqlUpdateBuilder()
                        .insert(getTableName())
                        .columns(DAOUtils.toColumnStr(getClazz()))
                        .values(DAOUtils.toValueStr(getClazz()))
                        .build(),
                DAOUtils.toParameterSource(model));
    }

    default long insertGetId(T model) {
        return insertGetId(model, null);
    }

    default long insertGetId(T model, NamedParameterJdbcTemplate template) {
        if (template == null) {
            template = getWriter();
        }
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        template.update(
                new SqlUpdateBuilder()
                        .insert(getTableName())
                        .columns(DAOUtils.toColumnStr(getClazz()))
                        .values(DAOUtils.toValueStr(getClazz()))
                        .build(),
                DAOUtils.toParameterSource(model), generatedKeyHolder);
        return generatedKeyHolder.getKey().longValue();
    }

    default int insertIgnore(T model) {
        return insertIgnore(model, null);
    }

    default int insertIgnore(T model, NamedParameterJdbcTemplate template) {
        if (template == null) {
            template = getWriter();
        }

        return template.update(
                new SqlUpdateBuilder()
                        .insertIgnore(getTableName())
                        .columns(DAOUtils.toColumnStr(getClazz()))
                        .values(DAOUtils.toValueStr(getClazz()))
                        .build(),
                DAOUtils.toParameterSource(model));
    }
}
