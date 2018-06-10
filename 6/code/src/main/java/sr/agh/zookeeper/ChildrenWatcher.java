package sr.agh.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class ChildrenWatcher implements Watcher {
    private final ZooKeeper zk;
    private String watchingZnode;

    public ChildrenWatcher(ZooKeeper zk, String watchingZnode) {
        this.zk = zk;
        this.watchingZnode = watchingZnode;
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            Stat stat = zk.exists(watchingZnode, false);
            if(stat != null) {
                zk.getChildren(watchingZnode, this);
            }

            countChildren();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void countChildren() throws KeeperException, InterruptedException {
        Stat stat = zk.exists(watchingZnode, false);
        if(stat != null) {
            int childNumber = countChildrenOfNode(watchingZnode);
            stat = zk.exists(watchingZnode, false);
            if(stat != null) {
                System.out.println(String.format("Number of child znodes: %d", childNumber));
            }
        }
    }

    private int countChildrenOfNode(String node) {
        try {
            List<String> children  = zk.getChildren(node, false);
            return children.size() + children.stream().mapToInt(child -> countChildrenOfNode(node.concat("/" + child))).sum();
        } catch (Exception e) {
            return 0;
        }
    }
}
