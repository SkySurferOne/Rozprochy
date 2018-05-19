package sr.agh.akka.client;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class ClientSupervisorActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    context().child("clientWorker").get().tell(s, getSelf());
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(ClientWorkerActor.class), "clientWorker");
    }

    private SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("30 seconds"), DeciderBuilder
            .match(ArrayIndexOutOfBoundsException.class, o -> {
                log.warning("Provide second parameter.");
                return SupervisorStrategy.restart();
            })
            .matchAny(o -> SupervisorStrategy.restart())
            .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
