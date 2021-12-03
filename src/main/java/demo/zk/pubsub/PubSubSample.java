package demo.zk.pubsub;


import com.sun.xml.internal.ws.util.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;

public class PubSubSample {
    private static final String ADDRESS = "192.168.142.138:2181";
    private static final int SESSION_TIMEOUT = 5;
    private static final String PATH = "/configs";
    private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private static String config = "jdbc_configuration";
    private static CountDownLatch countDownLatch = new CountDownLatch(4);

    public static void main(String[] args) throws Exception {
        // 订阅该配置信息的集群节点（客户端）:sub1-sub3
        for (int i = 0; i < 3; i++) {
            CuratorFramework consumerClient = getClient();
            subscribe(consumerClient, "sub" + String.valueOf(i));
        }
        // 更改配置信息的集群节点（客户端）:pub
        CuratorFramework publisherClient = getClient();
        publish(publisherClient, "pub");
        StringUtils.capitalize("asides");
        String a="1"+"1";
        for (int i = 0; i < 10; i++) {
            a+=i;
        }
        System.out.println(a);
    }


    /**
     * 创建客户端并且初始化建立一个存储配置数据的节点
     *
     * @return
     * @throws Exception
     */
    private static CuratorFramework getClient() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ADDRESS)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        if (client.checkExists().forPath(PATH) == null) {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(PATH, config.getBytes());
        }
        return client;
    }

    /**
     * 集群中的某个节点机器更改了配置信息：即发布了更新了数据
     *
     * @param client
     * @throws Exception
     */
    private static void publish(CuratorFramework client, String znode) throws Exception {

        System.out.println("节点[" + znode + "]更改了配置数据...");
        client.setData().forPath(PATH, "configuration".getBytes());
        Thread.sleep(10000);
        client.setData().forPath(PATH, "configuration11".getBytes());
        countDownLatch.await();
    }

    /**
     * 集群中订阅的节点客户端（机器）获得最新的配置数据
     *
     * @param client
     * @param znode
     * @throws Exception
     */
    private static void subscribe(CuratorFramework client, String znode) throws Exception {
        // NodeCache监听ZooKeeper数据节点本身的变化
        final NodeCache cache = new NodeCache(client, PATH);
        // 设置为true：NodeCache在第一次启动的时候就立刻从ZooKeeper上读取节点数据并保存到Cache中
        cache.start(true);
        System.out.println("节点["+ znode +"]已订阅当前配置数据：" + new String(cache.getCurrentData().getData()));
        // 节点监听
        countDownLatch.countDown();
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() {
                System.out.println("配置数据已发生改变, 节点[" + znode + "]读取当前新配置数据: " + new String(cache.getCurrentData().getData()));
            }
        });
    }
}
