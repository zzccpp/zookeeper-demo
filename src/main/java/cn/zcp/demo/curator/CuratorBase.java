package cn.zcp.demo.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-23 22:08
 * @describe demo-parent 使用curator操作zookeeper
 *
 * 总结:
 *  1、创建和赋值分开的,可以递归创建节点
 *
 *
 */
public class CuratorBase{

    /** zookeeper地址 */
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";


    public static void main(String[] args) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);//尝试3次，间隔1s
        CuratorFramework cf = CuratorFrameworkFactory.newClient(ZOOKEEPER_HOST,retryPolicy);
        cf.start();

        cf.create().forPath("/super");
        System.out.println("使用curator创建super节点");

        //只会触发一次
        cf.getData().usingWatcher((CuratorWatcher) event ->
            System.out.println("触发了watcher事件，节点路径为："+event.getPath()+"，事件类型为："+event.getType()))
        .forPath("/super");

        cf.setData().forPath("/super","123".getBytes());
        cf.setData().forPath("/super","4444".getBytes());

        cf.create()/*.withProtection()*/.withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/super/temp");
        System.out.println("使用curator创建临时super/temp节点");

        cf.create().creatingParentContainersIfNeeded().forPath("/super/ccc/ddd");
        System.out.println("递归创建/super/ccc/ddd");

        Stat stat = cf.checkExists().forPath("/super/ccc");
        System.out.println("查看是否存在："+stat.toString());

        cf.setData().forPath("/super/ccc/ddd","xxxx".getBytes());

        byte[] bytes = cf.getData().forPath("/super/ccc/ddd");
        System.out.println("/super/ccc/ddd节点值："+new String(bytes));

        List<String> paths = cf.getChildren().forPath("/super");
        for (String path :paths){
            System.out.println("子节点："+path);
        }

        cf.delete().deletingChildrenIfNeeded().forPath("/super");
        System.out.println("递归删除super节点");


        cf.close();
    }


}
