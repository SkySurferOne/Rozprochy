package sr.agh.akka.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerMain {

    public static void main(String[] args) {
        File configFile = new File("server.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("server_system", config);
        system.actorOf(Props.create(BookstoreActor.class), "bookstore");

        System.out.println("Server is running...");
        System.out.println("Press 'q' to quit.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line;

            try {
                line = br.readLine();

                if (line.equals("q")) {
                    system.terminate();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
