package org.xty.signal_capture.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-08
 */
public class DataSources {

    @Autowired
    private DataSource dataSource;

    public NamedParameterJdbcTemplate getReader() {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    public NamedParameterJdbcTemplate getWriter() {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
