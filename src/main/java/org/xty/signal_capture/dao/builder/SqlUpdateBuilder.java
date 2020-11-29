package org.xty.signal_capture.dao.builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-27
 */
public class SqlUpdateBuilder {

    private StringBuilder sql = new StringBuilder();

    public SqlUpdateBuilder insert(String tableName) {
        sql.append("insert into ");
        sql.append(tableName);

        return this;
    }

    public SqlUpdateBuilder insertIgnore(String tableName) {
        sql.append("insert ignore into ");
        sql.append(tableName);

        return this;
    }

    public SqlUpdateBuilder replace(String tableName) {
        sql.append("replace into ");
        sql.append(tableName);

        return this;
    }

    public SqlUpdateBuilder update(String tableName) {
        sql.append("update ");
        sql.append(tableName);

        return this;
    }

    //very dangerous
    public SqlUpdateBuilder delete(String tableName) {
        sql.append("delete from ");
        sql.append(tableName);

        return this;
    }

    public SqlUpdateBuilder columns(String... columns) {
        if (columns.length > 0) {
            sql.append(" ( ");
            sql.append(Joiner.on(",").join(columns));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlUpdateBuilder columns(String columnsStr) {
        sql.append(" ( ");
        sql.append(columnsStr);
        sql.append(" ) ");

        return this;
    }

    public SqlUpdateBuilder onDuplicate(String... conditions) {
        return onDuplicate(Arrays.asList(conditions));
    }

    public SqlUpdateBuilder onDuplicate(List<String> conditions) {
        if (!conditions.isEmpty()) {
            sql.append(" on duplicate key update ");
            sql.append(Joiner.on(",").join(conditions));
        }

        return this;
    }

    public SqlUpdateBuilder values(String... valueParams) {
        if (valueParams.length > 0) {
            sql.append(" values( ");
            sql.append(Joiner.on(",").join(Arrays.stream(valueParams)
                    .map(valueParam -> ":" + valueParam).collect(Collectors.toList())));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlUpdateBuilder values(String valueParamsStr) {
        sql.append(" values( ");
        sql.append(valueParamsStr);
        sql.append(" ) ");

        return this;
    }

    public SqlUpdateBuilder set(String... conditions) {
        return set(Arrays.asList(conditions));
    }

    public SqlUpdateBuilder set(List<String> conditions) {
        if (!conditions.isEmpty()) {
            sql.append(" set ");
            sql.append(Joiner.on(",").join(conditions));
        }

        return this;
    }

    public SqlUpdateBuilder and(String... conditions) {
        if (conditions.length > 0) {
            sql.append(" and ( ");
            sql.append(Joiner.on(" and ").join(conditions));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlUpdateBuilder or(String... conditions) {
        if (conditions.length > 0) {
            sql.append(" or ( ");
            sql.append(Joiner.on(" or ").join(conditions));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlUpdateBuilder where(String... conditions) {
        return where(Arrays.asList(conditions));
    }

    public SqlUpdateBuilder where(List<String> conditions) {
        if (!conditions.isEmpty()) {
            sql.append(" where ( ");
            sql.append(Joiner.on(" and ").join(conditions));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlUpdateBuilder whereOr(String... conditions) {
        return whereOr(Arrays.asList(conditions));
    }

    public SqlUpdateBuilder whereOr(List<String> conditions) {
        if (!conditions.isEmpty()) {
            sql.append(" where ( ");
            sql.append(Joiner.on(" or ").join(conditions));
            sql.append(" ) ");
        }

        return this;
    }

    public String build() {
        return sql.toString();
    }
}
