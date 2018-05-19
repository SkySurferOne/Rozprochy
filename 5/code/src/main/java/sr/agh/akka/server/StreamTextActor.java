package sr.agh.akka.server;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.OverflowStrategy;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.collection.Iterator;
import scala.concurrent.duration.FiniteDuration;
import scala.io.Codec;

import java.util.concurrent.TimeUnit;

public class StreamTextActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final String BOOKS_PATH = "src/main/resources/db/books/";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StreamTextRequest.class, streamTextRequest -> {
                    ActorMaterializer materializer = ActorMaterializer.create(context());
                    ActorRef run = Source.actorRef(1000, OverflowStrategy.dropNew())
                            .throttle(1, FiniteDuration.create(1, TimeUnit.SECONDS), 10, ThrottleMode.shaping())
                            .to(Sink.actorRef(getSender(), NotUsed.getInstance()))
                            .run(materializer);
                    String filename = String.format("%s%s", BOOKS_PATH, streamTextRequest.getQuery());
                    Iterator<String> lines = scala.io.Source.fromFile(filename, Codec.UTF8()).getLines();
                    scala.collection.JavaConversions.asJavaIterator(lines).forEachRemaining(line -> {
                        run.tell(new StreamTextResponse(line), getSelf());
                    });
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }
}
