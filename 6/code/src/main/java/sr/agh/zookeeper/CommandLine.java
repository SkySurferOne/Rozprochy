package sr.agh.zookeeper;

import org.apache.zookeeper.ZooKeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLine implements Runnable {
    private TreeStructurePrinter treeStructurePrinter;
    private String znode;
    private Executor executor;

    public CommandLine(ZooKeeper zk, String znode, Executor executor) {
        this.treeStructurePrinter = new TreeStructurePrinter(zk);
        this.znode = znode;
        this.executor = executor;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line;

            try {
                line = br.readLine();

                if (line.equals("q")) {
                    executor.close();
                    break;
                }

                if (line.equals("p") || line.equals("print")) {
                    treeStructurePrinter.print(znode);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
