package com.example.zkclientcurator.config;

import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 引用配置类
 *
 * @author dingchuan
 */
@Configuration
@RequiredArgsConstructor
public class CuratorConfig {

  private final WrapperZk wrapperZK;

  @Bean(initMethod = "start")
  public CuratorFramework curatorFramework() {
    return CuratorFrameworkFactory.newClient(
        wrapperZK.getConnectionString(),
        wrapperZK.getSessionTimeoutMs(),
        wrapperZK.getConnectionTimeoutMs(),
        new RetryNTimes(wrapperZK.getRetryCount(), wrapperZK.getElapsedTimeMs())
    );
  }

}
