package com.example.zkclientcurator.watch;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Curator实现Watcher
 *
 * @author dingchuan
 */
@Slf4j
public class CuratorWatcherExample {

  private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
  private static final String NODE_PATH = "/myNode";

  public static void main(String[] args) throws Exception {
    // 创建ZooKeeper客户端
    CuratorFramework client = CuratorFrameworkFactory.newClient(ZOOKEEPER_ADDRESS,
        new ExponentialBackoffRetry(1000, 3));
    client.start();

    // 创建节点（如果不存在）
    if (client.checkExists().forPath(NODE_PATH) == null) {
      client.create().withMode(CreateMode.PERSISTENT).forPath(NODE_PATH, "initialData".getBytes());
    }

    // 创建PathChildrenCache，用于监听子节点的变化
    PathChildrenCache cache = new PathChildrenCache(client, NODE_PATH, true);
    cache.start();

    // 添加监听器
    cache.getListenable().addListener((client1, event) -> {
      switch (event.getType()) {
        case CHILD_ADDED:
          log.info("Child added: {}", event.getData().getPath());
          break;
        case CHILD_REMOVED:
          log.info("Child removed: {}", event.getData().getPath());
          break;
        case CHILD_UPDATED:
          log.info("Child updated: {}", event.getData().getPath());
          break;
        default:
          log.info("Unknown event type: {}", event.getType());
          break;
      }
    });

    // 模拟子节点的变化
    client.create().withMode(CreateMode.EPHEMERAL)
        .forPath(NODE_PATH + "/child1", "data1".getBytes());
    Thread.sleep(1000);
    client.setData().forPath(NODE_PATH + "/child1", "newData".getBytes());
    Thread.sleep(1000);
    client.delete().forPath(NODE_PATH + "/child1");

    // 等待一段时间以观察事件
    Thread.sleep(5000);

    // 关闭客户端
    cache.close();
    client.close();
  }
}
