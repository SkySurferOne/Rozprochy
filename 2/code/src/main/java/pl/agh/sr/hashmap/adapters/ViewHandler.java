package pl.agh.sr.hashmap.adapters;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MergeView;
import org.jgroups.View;
import java.util.List;

public class ViewHandler extends Thread {
    private final JChannel channel;
    private final MergeView view;

    ViewHandler(JChannel channel, MergeView view) {
        this.channel = channel;
        this.view = view;
    }

    public void run() {
        List<View> subgroups = view.getSubgroups();
        View tmpView = subgroups.get(0);
        Address localAddr = channel.getAddress();
        if(!tmpView.getMembers().contains(localAddr)) {
            System.out.println("Not member of the new primary partition ("
                    + tmpView + "), will re-acquire the state");
            try {
                channel.getState(null, 30000);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            System.out.println("Not member of the new primary partition ("
                    + tmpView + "), will do nothing");
        }
    }
}
