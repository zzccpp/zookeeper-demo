package cn.zcp.demo.curator.watcher;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.prefs.NodeChangeListener;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-23 22:09
 * @describe demo-parent <描述>
 */
public class CuratorWathcher1 {

    /** zookeeper地址 */
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";
    static CuratorFramework cf = null;
    public static void main(String[] args) throws Exception {


        cf = CuratorFrameworkFactory.newClient(ZOOKEEPER_HOST, new ExponentialBackoffRetry(1000, 1));

        cf.start();

        Executor executor = Executors.newFixedThreadPool(3);

        NodeCache nodeCache = new NodeCache(cf,"/nodeCache");

        nodeCache.getListenable().addListener(() -> {

            System.out.println("路径为:"+nodeCache.getCurrentData().getPath());
            System.out.println("数据为:"+new String(nodeCache.getCurrentData().getData()));
            System.out.println("状态为:"+nodeCache.getCurrentData().getStat());

        },executor);

        Thread.sleep(1000);
        cf.create().forPath("/nodeCache","123".getBytes());
        Thread.sleep(1000);
        cf.setData().forPath("/nodeCache","cccc".getBytes());

        Thread.sleep(50000);
        cf.close();
    }
}
