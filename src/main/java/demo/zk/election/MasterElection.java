package demo.zk.election;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MasterElection {

    private static final String ADDRESS = "192.168.142.138:2181";
    private static final int SESSION_TIMEOUT = 5000;
    private static final String MASTER_PATH = "/master_path";
    private static final int CLIENT_COUNT = 5;

    private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);


    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(CLIENT_COUNT);
        for (int i = 0; i < CLIENT_COUNT; i++) {
            final String index = String.valueOf(i);
            service.submit(() -> {
                masterSelect(index);
            });
        }
    }

    private static void masterSelect(final String znode) {
        // client成为master的次数统计
        AtomicInteger leaderCount = new AtomicInteger(1);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ADDRESS)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        // 一旦执行完takeLeadership，就会重新进行选举
        LeaderSelector selector = new LeaderSelector(client, MASTER_PATH, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("节点[" + znode + "]成为master");
                System.out.println("节点[" + znode + "]已经成为master次数：" + leaderCount.getAndIncrement());
                // 睡眠5s模拟成为master后完成任务
                Thread.sleep(5000);
                System.out.println("节点[" + znode + "]释放master");
            }
        });
        // autoRequeue自动重新排队：使得上一次选举为master的节点还有可能再次成为master
        selector.autoRequeue();
        selector.start();
    }
}
