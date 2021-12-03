package demo.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZookeeperTest {
    public static void main(String[] args) throws InterruptedException {
        ZooKeeper zooKeeper = null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            zooKeeper =
                    new ZooKeeper("192.168.142.138:2181",
                            4000, event -> {
                        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                            //如果收到了服务端的响应事件，连接成功
                            countDownLatch.countDown();
                        }
                    });
            countDownLatch.await();
            //CONNECTED
            System.out.println(zooKeeper.getState());
            zooKeeper.delete("/runoob", 0);
            zooKeeper.create("/runoob", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zooKeeper.close();
        }
    }
}
