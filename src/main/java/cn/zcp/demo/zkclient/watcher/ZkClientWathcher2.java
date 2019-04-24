package cn.zcp.demo.zkclient.watcher;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-22 23:49
 * @describe demo-parent 监听当前节点
 *
 * 1、监听当前节点的数据变化及删除该节点
 *
 */
public class ZkClientWathcher2 {

    /**定义zookeeper的服务器地址*/
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";

    public static void main(String[] args) throws Exception {

        ZkClient zkc = new ZkClient(new ZkConnection(ZOOKEEPER_HOST));



        zkc.subscribeDataChanges("/super", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("节点:"+dataPath+",数据变化："+data.toString());
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("节点:"+dataPath+"删除");
            }
        });

        zkc.createPersistent("/super");
        System.out.println("---------添加/super----------");
        Thread.sleep(1000);
        zkc.createPersistent("/super/c1");
        System.out.println("---------添加子节点c1----------");
        Thread.sleep(1000);
        zkc.writeData("/super","更新");
        System.out.println("---------更新节点/super值----------");
        Thread.sleep(1000);

        zkc.deleteRecursive("/super");
        System.out.println("---------删除节点super----------");
        Thread.sleep(4000);
        zkc.close();
    }

}
