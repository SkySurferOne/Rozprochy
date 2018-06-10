package sr.agh.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class TreeStructurePrinter {
    private final static String POINTER = "-> ";
    private final static int BEGINNING_LEVEL = 0;
    private final ZooKeeper zk;

    public TreeStructurePrinter(ZooKeeper zooKeeper) {
        this.zk = zooKeeper;
    }

    public void print(String znode) {
        printLevel(znode, BEGINNING_LEVEL);
    }

    private void printLevel(String zNodeName, int level) {
        System.out.println(prepareTreeStringForNode(zNodeName,level));
        try {
            zk.getChildren(zNodeName, false)
                    .forEach(child -> printLevel(zNodeName.concat("/" + child), level + 1));
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String prepareTreeStringForNode(String zNodeName, int level) {
        StringBuffer st = new StringBuffer();
        for(int i = 0 ; i < level; i++) st.append('\t');
        st.append(POINTER);
        st.append(zNodeName);

        return new String(st);
    }
}
