package sr.agh.akka.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientMain {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Provide config file name as argument.");
            return;
        }

        File configFile = new File(args[0]);
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("client_system", config);
        final ActorRef actorRef = system.actorOf(Props.create(ClientSupervisorActor.class), "client");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line;

            try {
                line = br.readLine();

                if (line.equals("q")) {
                    break;
                }
                actorRef.tell(line, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        system.terminate();
    }

}
