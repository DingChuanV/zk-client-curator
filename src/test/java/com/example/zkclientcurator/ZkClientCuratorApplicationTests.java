package com.example.zkclientcurator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ZkClientCuratorApplicationTests {

  @Autowired
  private CuratorFramework curatorFramework;

  @Test
  void contextLoads() {
  }

  /**
   * 添加节点
   *
   * @throws Exception
   */
  @Test
  void createNode() throws Exception {
    //添加默认(持久)节点
    String path = curatorFramework.create().forPath("/curator-node");
    log.info("curator 持久节点 node :{}  successfully!", path);

    //添加临时序号节点
    String path2 = curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
        .forPath("/curator-nodes", "messageDate".getBytes());
    log.info("临时序号节点:{}  successfully!", path2);
  }

  /**
   * 获取节点值
   *
   * @throws Exception
   */
  @Test
  void getDate() throws Exception {
    byte[] bttes = curatorFramework.getData().forPath("/curator-node");
    log.info("get zNode : {}", new String(bttes));
  }

  @Test
  void testCreateWithParent() throws Exception{
    String pathWithParent = "/node-parent/sub-node-1";
    String path = curatorFramework.create().creatingParentsIfNeeded()
        .forPath(pathWithParent);
    log.info("curator create node :{} success!", path);
  }

  /**
   * 设置节点值
   *
   * @throws Exception
   */
  @Test
  void setDate() throws Exception {
    curatorFramework.setData().forPath("/curator-node", "newMessage".getBytes());
    byte[] bytes = curatorFramework.getData().forPath("/curator-node");
    log.info("bytes : {} ", bytes);
  }

  /**
   * 创建多级节点
   *
   * @throws Exception
   */
  @Test
  void createWithParent() throws Exception {
    String pathWithParent = "/node-parent/sub-node-1";
    String path = curatorFramework.create().creatingParentContainersIfNeeded()
        .forPath(pathWithParent);
    log.info("curator create node :{} success!", path);
  }

  /**
   * 删除节点
   *
   * @throws Exception
   */
  @Test
  void delete() throws Exception {
    String path = "/node-parent";
    //删除节点的同时一并删除子节点
    curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
  }
}
