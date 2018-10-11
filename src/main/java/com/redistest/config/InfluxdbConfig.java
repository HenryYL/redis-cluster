package com.redistest.config;

import org.apache.commons.lang.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @date 2018/3/16 下午5:54
 */
@Configuration
public class InfluxdbConfig {
    @Bean
    public InfluxDB influxDB(InfluxdbParams influxdbParams) {
        if (StringUtils.isBlank(influxdbParams.getUser())) {
            return InfluxDBFactory.connect(influxdbParams.getUrl());
        }
        return InfluxDBFactory.connect(influxdbParams.getUrl(), influxdbParams.getUser(), influxdbParams.getPassword());
    }
}
