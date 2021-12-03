package demo.zk.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistributedLock {
    private static final String ADDRESS = "192.168.142.138:2181";
    private static final int SESSION_TIMEOUT = 500;
    private static final String LOCK_PATH = "/lock_path";
    private static final int CLIENT_COUNT = 20;

    private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private static int resource = 0;

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(CLIENT_COUNT);
        for (int i = 0; i < 500; i++) {
            final String index = String.valueOf(i);
            service.submit(() -> {
                distributedLock(index);
            });
        }
        service.shutdown();
    }

    private static void distributedLock(final String znode) {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ADDRESS)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        final InterProcessMutex lock = new InterProcessMutex(client, LOCK_PATH);
        try {
            lock.acquire();
            System.out.println("客户端节点[" + znode + "]获取lock");
            System.out.println("客户端节点[" + znode + "]读取的资源为：" + resource);
            resource++;
            lock.release();
            System.out.println("客户端节点[" + znode + "]释放lock");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
