package sr.agh.akka.server;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;

public class SearchActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private SearchResponse searchResponsePrevious;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchRequest.class, searchRequest -> {
                    log.info(searchRequest.getQuery());

                    context().child("searchChild1").get().forward(searchRequest, context());
                    context().child("searchChild2").get().forward(searchRequest, context());
                })
                .match(SearchResponse.class, searchResponse -> {
                    // delete
                    System.out.println(searchResponsePrevious);
                    System.out.println(searchResponse.toString());

                    if (searchResponsePrevious == null) {
                        searchResponsePrevious = searchResponse;
                        if (searchResponse.isFound()) {
                            context().parent().forward(searchResponse, context());
                        }
                    } else {
                        if (!searchResponsePrevious.isFound()) {
                            context().parent().forward(searchResponse, context());
                        }
                        searchResponsePrevious = null;
                    }
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder
            .match(FileNotFoundException.class, o -> SupervisorStrategy.stop())
            .matchAny(o -> SupervisorStrategy.restart())
            .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(SearchChildActor.class, "books_registry_1.csv"), "searchChild1");
        context().actorOf(Props.create(SearchChildActor.class, "books_registry_2.csv"), "searchChild2");
    }
}
