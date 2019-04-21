package cn.zcp.demo.zookeeper.base;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.omg.CORBA.TIMEOUT;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-21 16:52
 * @describe demo-parent 使用原生
 */
public class ZookeeperBase {

    /**定义zookeeper的服务器地址*/
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";
    /**sessio超时时间*/
    private final static int SESSION_TIMEOUT=5000;//ms
    /**因为是zookeeper是异步连接,所以定义一个计数锁*/
    private final static CountDownLatch countDownLatch = new CountDownLatch(1);
    /**设置字符编码*/
    private final static String ENCODING = "UTF-8";

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(ZOOKEEPER_HOST, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //获取时间状态
                Event.KeeperState keeperState = event.getState();
                Event.EventType eventType = event.getType();
                //如果建立连接
                if(Event.KeeperState.SyncConnected==keeperState){
                    if(Event.EventType.None==eventType){
                        countDownLatch.countDown();
                    }
                }
            }
        });
        //等待zookeeper连接,进行阻塞
        countDownLatch.await();
        System.out.println("zookeeper连接成功!");
        //创建父节点
        zooKeeper.create("/demo-root","rootTxt".getBytes(ENCODING), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        System.out.println("创建根节点demo-root完成!");
        //创建子节点
        zooKeeper.create("/demo-root/children1","childrenData1".getBytes(ENCODING), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        System.out.println("创建子节点demo-root/children1完成!");
        //创建子节点
        zooKeeper.create("/demo-root/children2","childrenData2".getBytes(ENCODING), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        System.out.println("创建子节点demo-root/children2完成!");
        //获取节点的值
        byte[] data = zooKeeper.getData("/demo-root", false, null);
        System.out.println("获取根节点demo-root的值为:"+new String(data,ENCODING));
        //获取子节点信息
        List<String> childrens = zooKeeper.getChildren("/demo-root", false, null);
        String nodePath;
        for(String childNode : childrens){
            nodePath ="/demo-root/"+childNode;
            data = zooKeeper.getData(nodePath, false, null);
            System.out.println("获取根节点"+nodePath+"的值为:"+new String(data,ENCODING));
        }
        //创建临时节点,常利用于分布式锁
        zooKeeper.create("/demo-root/temp-children3","childrenData3".getBytes(ENCODING), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println("创建临时子节点demo-root/temp-children3完成!");
        //删除子节点
        zooKeeper.delete("/demo-root/children2", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                System.out.println("rc = [" + rc + "], path = [" + path + "], ctx = [" + ctx + "]");
            }
        }, "xxx");

        //判断节点是否存在
        Stat exists = zooKeeper.exists("/demo-root/children1", null);
        System.out.println(exists);
        Thread.sleep(30000);
        zooKeeper.close();
    }
}
