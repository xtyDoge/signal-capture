package org.xty.signal_capture.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.xty.signal_capture.dao.base.DaoBaseGet;
import org.xty.signal_capture.dao.base.DaoBaseInsert;
import org.xty.signal_capture.model.VideoFrame;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xutianyou <xutianyou@kuaishou.com>
 * Created on 2020-11-29
 */
@Repository
@Slf4j
public class VideoFrameDao implements DaoBaseInsert<VideoFrame>, DaoBaseGet<VideoFrame> {

    @Autowired
    private DruidDataSource dataSource;

    @Override
    public NamedParameterJdbcTemplate getWriter() {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Class<VideoFrame> getClazz() {
        return VideoFrame.class;
    }

    @Override
    public NamedParameterJdbcTemplate getReader() {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
