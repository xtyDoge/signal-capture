package dao.builder;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-23
 */

public class SqlQueryBuilder {

    private StringBuilder sql = new StringBuilder();

    public SqlQueryBuilder select(String columns) {
        sql.append("select ");
        sql.append(columns);

        return this;
    }

    public SqlQueryBuilder from(String tableName) {
        sql.append(" from ");
        sql.append(tableName);

        return this;
    }

    public SqlQueryBuilder join(String tableName) {
        sql.append(" join ");
        sql.append(tableName);

        return this;
    }

    public SqlQueryBuilder leftOuterJoin(String tableName) {
        sql.append(" left outer join ");
        sql.append(tableName);

        return this;
    }

    public SqlQueryBuilder on(String condition) {
        sql.append(" on ");
        sql.append(condition);

        return this;
    }

    public SqlQueryBuilder and(String... conditions) {
        if (conditions.length > 0) {
            sql.append(" and ( ");
            sql.append(Joiner.on(" and ").join(conditions));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlQueryBuilder or(String... conditions) {
        if (conditions.length > 0) {
            sql.append(" or ( ");
            sql.append(Joiner.on(" or ").join(conditions));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlQueryBuilder where(String... conditions) {
        return where(Arrays.asList(conditions));
    }

    public SqlQueryBuilder where(List<String> conditions) {
        if (!conditions.isEmpty()) {
            sql.append(" where ( ");
            sql.append(Joiner.on(" and ").join(conditions));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlQueryBuilder whereOr(String... conditions) {
        return whereOr(Arrays.asList(conditions));
    }

    public SqlQueryBuilder whereOr(List<String> conditions) {
        if (!conditions.isEmpty()) {
            sql.append(" where ( ");
            sql.append(Joiner.on(" or ").join(conditions));
            sql.append(" ) ");
        }

        return this;
    }

    public SqlQueryBuilder groupBy(String... fields) {
        if (fields.length > 0) {
            sql.append(" group by ");
            sql.append(Joiner.on(", ").join(fields));
        }

        return this;
    }

    public SqlQueryBuilder having(String condition) {
        sql.append(" having ");
        sql.append(condition);

        return this;
    }

    public SqlQueryBuilder orderBy(String field) {
        sql.append(" order by ");
        sql.append(field);

        return this;
    }

    public SqlQueryBuilder asc() {
        sql.append(" asc ");

        return this;
    }

    public SqlQueryBuilder desc() {
        sql.append(" desc ");

        return this;
    }

    public SqlQueryBuilder limit(int num) {
        sql.append(" limit ");
        sql.append(num);

        return this;
    }

    public SqlQueryBuilder limit(String param) {
        sql.append(" limit ");
        sql.append(param);

        return this;
    }

    public SqlQueryBuilder limit(int num, long offset) {
        sql.append(format(" limit %d,%d ", offset, num));

        return this;
    }

    public String build() {
        return sql.toString();
    }
}
