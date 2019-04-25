package cn.zcp.demo.curator.atomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-23 22:17
 * @describe demo-parent 分布式计数器
 *
 * 1、查看API还可以做分布式队列等...
 *
 */
public class CuratorAtomicInteger {

    /** zookeeper地址 */
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";
    static CuratorFramework cf = null;
    public static void main(String[] args) throws Exception {


        cf = CuratorFrameworkFactory.newClient(ZOOKEEPER_HOST, new ExponentialBackoffRetry(1000, 1));

        cf.start();

        DistributedAtomicInteger di = new DistributedAtomicInteger(cf,"/di",new RetryNTimes(3,1000));
        System.out.println(di.get().preValue());
        di.forceSet(0);
        System.out.println(di.get().postValue());
        di.increment();
        System.out.println(di.get().postValue());
        di.add(2);
        System.out.println(di.get().postValue());


        cf.close();
    }
}
