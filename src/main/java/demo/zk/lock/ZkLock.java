package demo.zk.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZkLock {

    private ZkClient zkClient;
    private static final String ROOT_PATH = "/zk_lock";
    private String lockName;
    private String eNodeName;
    private String eNodeNo;

    public ZkLock(String lockName) {
        this.zkClient = new ZkClient("192.168.142.138:2181", 2000);
        this.lockName = ROOT_PATH.concat("/").concat(lockName);
        if (!zkClient.exists(this.lockName)) {
            zkClient.createPersistent(this.lockName, true);
        }
    }

    public void lock() throws InterruptedException {
        while (true) {
            if (Objects.isNull(eNodeName)) {
                eNodeName = zkClient.createEphemeralSequential(lockName.concat("/"), System.currentTimeMillis());
                eNodeNo = eNodeName.substring(eNodeName.lastIndexOf("/") + 1);
            }
            List<String> children = zkClient.getChildren(lockName);
            if (children.size() > 0) {
                children.sort(null);
                if (children.get(0).equals(eNodeNo)) {
                    System.out.println(eNodeNo + " --> 获得了锁");
                    break;
                } else {
                    CountDownLatch latch = new CountDownLatch(1);
                    String preNode = children.get(children.indexOf(eNodeNo) - 1);
                    if (zkClient.exists(lockName.concat("/").concat(preNode))) {
                        System.out.println("注册监听到:" + preNode);
                        zkClient.subscribeDataChanges(lockName.concat("/").concat(preNode),
                                new IZkDataListener() {
                                    @Override
                                    public void handleDataChange(String s, Object o) throws Exception {
                                        System.out.println(eNodeNo + "！！！！！！！！！！！被唤醒");
                                        latch.countDown();
                                    }

                                    @Override
                                    public void handleDataDeleted(String s) throws Exception {
                                        System.out.println(eNodeNo + "！！！！！！！！！！！被唤醒");
                                        latch.countDown();
                                    }
                                }
                        );
                    } else {
                        System.out.println("preNode " + preNode + " has non exists.");
                        break;
                    }

                    try {
                        System.out.println(eNodeNo + " 开始等待..");
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            } else {
                throw new RuntimeException("获取锁异常");
            }
        }
    }

    public void unLock() {
        zkClient.close();
        System.out.println(eNodeNo + " 释放锁.");
    }

    private static int resource = 0;

    public static void main(String[] args) {
        //测试5个线程的争抢
        int threadCount = 500;
        ExecutorService service = Executors.newFixedThreadPool(20);
        for (int i = 0; i < threadCount; i++) {
            service.submit(() -> {
                try {
                    ZkLock lock = new ZkLock("lock_test");
                    lock.lock();
                    //模拟处理业务逻辑
//                    Thread.sleep(1000 + new Random().nextInt(100) * 10);
//                    System.out.println("客户端节点[" + lock.eNodeNo + "]获取lock");
//                    System.out.println("客户端节点[" + lock.eNodeNo + "]读取的资源为：" + resource);
                    resource++;
                    System.out.println("资源为:" + resource);
                    lock.unLock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
