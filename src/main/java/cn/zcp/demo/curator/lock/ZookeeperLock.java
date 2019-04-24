package cn.zcp.demo.curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-24 23:02
 * @describe demo-parent 分布式锁实现
 */
public class ZookeeperLock {

    /** zookeeper地址 */
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";
    static CuratorFramework cf = null;
    static int count = 0;
    public static void main(String[] args) throws Exception {


        cf = CuratorFrameworkFactory.newClient(ZOOKEEPER_HOST, new ExponentialBackoffRetry(1000, 1));

        cf.start();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        for (int i=0;i<5;i++){

            final int c = i;
            new Thread(()->{
                System.out.println("创建线程："+c);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dowork();

            },"T"+i).start();
        }
        Thread.sleep(1000);
        countDownLatch.countDown();

        Thread.sleep(10000);
        cf.close();

    }
    public static void dowork(){
        InterProcessLock lock=null;
        try {
            lock = new InterProcessMutex(cf,"/lock");
            lock.acquire();

            count+=1;
            System.out.println(Thread.currentThread().getName()+"count:"+count);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(null!=lock)lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
