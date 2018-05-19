package sr.agh.akka.client;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import sr.agh.akka.server.*;

public class ClientWorkerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private static final String SERVER_PATH = "akka.tcp://server_system@127.0.0.1:2553/user/";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    String[] cmd = s.split(" ");
                    switch (cmd[0]) {
                        case "search":
                            getContext().actorSelection(SERVER_PATH + "bookstore")
                                    .tell(new SearchRequest(s.substring(s.indexOf(" ") + 1)),
                                            getSelf());
                            break;
                        case "order":
                            getContext().actorSelection(SERVER_PATH + "bookstore")
                                    .tell(new OrderRequest(s.substring(s.indexOf(" ") + 1)),
                                            getSelf());
                            break;
                        case "read":
                            getContext().actorSelection(SERVER_PATH + "bookstore")
                                    .tell(new StreamTextRequest("a_tale_of_two_cities-charles_dickens.txt"),
                                            getSelf());
                            break;
                        default:
                            System.out.println("There is not such a command.");
                    }
                })
                .match(SearchResponse.class, searchResponse -> {
                    if (searchResponse.isFound()) {
                        System.out.println(String.format("Found '%s'", searchResponse.getTitle()));
                        System.out.println(String.format("Prize is %s", searchResponse.getMessage()));
                    } else {
                        System.out.println(String.format("There is no %s title in bookstore.", searchResponse.getMessage()));
                    }
                })
                .match(OrderResponse.class, orderResponse -> {
                    System.out.println(orderResponse.getMessage());
                })
                .match(StreamTextResponse.class, streamTextResponse -> {
                    System.out.println(streamTextResponse.getLine());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }


}
