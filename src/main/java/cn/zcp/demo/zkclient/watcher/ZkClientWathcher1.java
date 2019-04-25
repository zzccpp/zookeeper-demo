package cn.zcp.demo.zkclient.watcher;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.List;

/**
 * @author zhongchunping
 * @version 1.0
 * @Time 2019-04-22 23:49
 * @describe demo-parent 监听
 *
 * 1、监听子节点的（包括当前节点,不包括子孙节点）
 * 新增子节点
 * 删除子节点
 * 注意： 不监听节点内容的变化
 */
public class ZkClientWathcher1 {

    /** zookeeper地址 */
    private final static String ZOOKEEPER_HOST="182.61.44.56:2181,182.61.44.56:2182,182.61.44.56:2183";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 10000;//ms


    public static void main(String[] args) throws Exception {
        ZkClient zkc = new ZkClient(new ZkConnection(ZOOKEEPER_HOST), SESSION_OUTTIME);

        //对父节点添加监听子节点变化。
        zkc.subscribeChildChanges("/super", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("parentPath: " + parentPath);
                System.out.println("currentChilds: " + currentChilds);
            }
        });

        Thread.sleep(3000);

        zkc.createPersistent("/super");
        System.out.println("---------添加父节点----------");
        Thread.sleep(1000);

        zkc.createPersistent("/super" + "/" + "c1", "c1内容");
        System.out.println("---------添加子节点c1----------");
        Thread.sleep(1000);

        zkc.createPersistent("/super" + "/" + "c2", "c2内容");
        System.out.println("---------添加子节点c2----------");
        Thread.sleep(2000);

        zkc.createPersistent("/super" + "/" + "c2/C3", "c2内容");
        System.out.println("---------添加子节点c2/c3----------");
        Thread.sleep(2000);

        zkc.writeData("/super" + "/" + "c2", "c2更新");
        System.out.println("---------更新子节点c2----------");
        Thread.sleep(2000);

        zkc.delete("/super/c1");
        System.out.println("---------删除子节点c1----------");
        Thread.sleep(1000);

        zkc.deleteRecursive("/super");
        System.out.println("---------删除父节点/super----------");
        Thread.sleep(5000);
        zkc.close();
    }
}
