package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "gears.web")
public class LongPollingConfig {
    private static Long DEFAULT_TIME_OUT = 30L;

    private Long timeOut;

    @PostConstruct
    public void setDefaultTimeOut(){
        if (this.timeOut == null) this.timeOut = DEFAULT_TIME_OUT;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public LongPollingConfig setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
        return this;
    }
}
