package cn.zcp.demo.curator.barrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-23 22:23
 * @describe demo-parent 同时成功或者开始
 */
public class CuratorBarrier1 {

    /** zookeeper地址 */
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";
    static CuratorFramework cf = null;
    public static void main(String[] args) throws Exception {


        cf = CuratorFrameworkFactory.newClient(ZOOKEEPER_HOST, new ExponentialBackoffRetry(1000, 1));

        cf.start();

        final DistributedBarrier barrier = new DistributedBarrier(cf,"/barrier");

        for (int i=0;i<5;i++){

            final int c = i;
            new Thread(()->{
                System.out.println("创建线程："+c);
                try {
                    barrier.waitOnBarrier();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("----------start-------");
            },"T"+i).start();
        }

        barrier.removeBarrier();
    }
}
