package com.example.zkclientcurator.watch;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * ZooKeeper中注册和处理Watcher
 *
 * @author dingchuan
 */
@Slf4j
public class WatcherExample implements Watcher {

  private static ZooKeeper zk;

  public static void main(String[] args) throws Exception {
    zk = new ZooKeeper("localhost:2181", 3000, new WatcherExample());

    // 创建节点
    String path = "/myNode";
    if (zk.exists(path, false) == null) {
      zk.create(path, "initialData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 注册Watcher
    zk.getData(path, true, new Stat());

    // 修改节点数据
    zk.setData(path, "newData".getBytes(), -1);

    // 等待事件处理
    Thread.sleep(5000);

    // 关闭ZooKeeper连接
    zk.close();
  }

  @Override
  public void process(WatchedEvent event) {
    log.debug("Received event: {}", event);

    // 处理节点变化事件
    if (event.getType() == Event.EventType.NodeDataChanged) {
      try {
        // 重新注册Watcher
        String path = event.getPath();
        byte[] data = zk.getData(path, true, new Stat());
        log.debug("Data changed: {}", new String(data));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

