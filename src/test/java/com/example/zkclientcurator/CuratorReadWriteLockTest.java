package com.example.zkclientcurator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CuratorReadWriteLockTest {
  private static final String LOCK_PATH = "/lock";

  @Test
  void contextLoads() {
  }

  @Test
  public void readWriteLock() throws Exception {
    // 创建ZooKeeper客户端
    CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
    client.start();

    // 创建读写锁
    InterProcessReadWriteLock lock = new InterProcessReadWriteLock(client, LOCK_PATH);

    // 读操作
    new Thread(() -> {
      try {
        lock.readLock().acquire();
        System.out.println("Reading data...");
        // 模拟读操作
        Thread.sleep(1000);
        System.out.println("Read completed.");
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          lock.readLock().release();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }).start();

    // 写操作
    new Thread(() -> {
      try {
        lock.writeLock().acquire();
        System.out.println("Writing data...");
        // 模拟写操作
        Thread.sleep(2000);
        System.out.println("Write completed.");
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          lock.writeLock().release();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }).start();

    // 关闭客户端
    Thread.sleep(5000); // 等待一段时间以便观察读写操作
    client.close();
  }



}
