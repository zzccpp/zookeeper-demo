package cn.zcp.demo.curator.watcher;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-23 22:09
 * @describe demo-parent
 *
 * pathChildCache  监听一个节点下子节点的创建、删除、更新
 * NodeCache 监听一个节点的更新和创建事件
 * TreeCache 综合pathChildCache和NodeCache的特性
 */
public class CuratorWathcher3 {

    /** zookeeper地址 */
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";
    static CuratorFramework cf = null;
    public static void main(String[] args) throws Exception {

        cf = CuratorFrameworkFactory.newClient(ZOOKEEPER_HOST, new ExponentialBackoffRetry(1000, 1));

        cf.start();

        //监听子节点事件
        TreeCache cache = new TreeCache(cf,"/20190803");

        cache.getListenable().addListener((cf, event) -> {
            System.out.println("TreeCache事件："+event.getType()+"--"+event.getData().getPath());
        });

        cache.start();

        System.in.read();
    }

}
