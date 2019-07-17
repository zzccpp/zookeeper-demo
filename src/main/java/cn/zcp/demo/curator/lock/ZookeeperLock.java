package cn.zcp.demo.curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

        for (int i=0;i<2;i++){

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

        Thread.sleep(300000);

        cf.close();


    }

    /**
     * 源码分析：2019-07-17
     *
     * 1、本节点会创建threadData的MAP,Key：线程名  VALUE:锁定节点 ,会在/local1节点下生成顺序临时节点(多少个并发就会生成多少个)
     * 2、在lock1下的所有节点,StandardLockInternalsDriver.getsTheLock去获取当前临时节点的顺序，如果是0则获取到锁对象
     *
     * 3、如果是index>0的话，首先给index-1节点添加watcher对象,则等待LockInternals#internalLockLoop设置的超时时间，如果没有超时时间则一直等待。
     *
     * 4、当前一个获取锁的对象，释放锁（删除临时及节点时）触发watcher，notify所有等待的线程，继续去获取锁，lock1节点下的所有临时节点数变少，则原先index1则变为了index0
     * 从而获取到锁，这样不停的循环.依次获取到锁对象
     *
     * 临时如果没有释放，一定时间后会释放（删除），使得其他进程能获取到锁对象
     */
    public static void dowork(){
        InterProcessLock lock=null;
        try {
            lock = new InterProcessMutex(cf,"/lock1");
            //lock = new InterProcessSemaphoreMutex(cf,"/lock1");
            //lock.acquire();
            boolean acquire = lock.acquire(120, TimeUnit.SECONDS);
            System.out.println(acquire);
            //if(lock.isAcquiredInThisProcess()){
                System.out.println("000000000"+acquire);
                Thread.sleep(2000);
                System.out.println("111111111111");
                count+=1;
                System.out.println(Thread.currentThread().getName()+"count:"+count);
            //}
            System.out.println("222222");
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
