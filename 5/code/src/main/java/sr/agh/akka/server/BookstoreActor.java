package sr.agh.akka.server;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class BookstoreActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchRequest.class, searchRequest -> {
                    context().child("searchActor").get().forward(searchRequest, context());
                })
                .match(OrderRequest.class, orderRequest -> {
                    context().child("orderActor").get().forward(orderRequest, context());
                })
                .match(SearchResponse.class, searchResponse -> {
                    if (searchResponse.isPassToOrderActor()) {
                        context().child("orderActor").get().forward(searchResponse, context());
                    } else {
                        getSender().tell(searchResponse, getSelf());
                    }
                })
                .match(OrderResponse.class, orderResponse -> {
                    getSender().tell(orderResponse, getSelf());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(SearchActor.class), "searchActor");
        context().actorOf(Props.create(OrderActor.class), "orderActor");
    }

    private static SupervisorStrategy strategy
        = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                matchAny(o -> SupervisorStrategy.restart()).
                build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
