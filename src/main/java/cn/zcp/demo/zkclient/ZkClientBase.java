package cn.zcp.demo.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-22 23:38
 * @describe demo-parent 使用zkClient操作
 *
 * 1、可以递归添加节点，但不能递归赋值
 * 2、可以递归删除节点
 *
 */
public class ZkClientBase {


    /**定义zookeeper的服务器地址*/
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";

    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient(new ZkConnection(ZOOKEEPER_HOST));
        System.out.println("zkClient连接成功!");
        //递归删除节点
        zkClient.deleteRecursive("/zkClient");
        System.out.println("递归删除/zkClient节点");
        //创建节点
        zkClient.create("/zkClient","xxxxx", CreateMode.PERSISTENT);
        System.out.println("创建/zkClient节点");
        zkClient.create("/zkClient/cc","xxxxx", CreateMode.PERSISTENT);
        System.out.println("创建/zkClient/cc节点");
        zkClient.createEphemeral("/zkClient/temp","temp");
        System.out.println("创建临时节点/zkClient/temp节点");

        //递归创建
        zkClient.createPersistent("/zkClient/pp/ppp",true);
        System.out.println("递归创建/zkClient/pp/ppp节点");
        //删除节点
        String node = "/zkClient/cc";
        zkClient.delete(node);
        System.out.println("删除节点:"+node);

        //获取子节点
        node = "/zkClient";
        List<String> childrens = zkClient.getChildren(node);
        for (String path:childrens){
            path=node+"/"+path;
            String val = zkClient.readData(path,true);
            System.out.println("子节点:"+path+",值："+val);
        }

        zkClient.writeData("/zkClient/pp","修改值");

        String val = zkClient.readData("/zkClient/pp",true);
        System.out.println("修改后子节点:/zkClient/pp 值："+val);

        boolean exists = zkClient.exists(node);
        System.out.println(exists);
        zkClient.close();
    }
}
