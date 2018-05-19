package sr.agh.akka.server;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class OrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final File file;
    private final String ORDERS_PATH = "src/main/resources/db/orders.csv";

    public OrderActor() throws IOException {
        this.file = new File(ORDERS_PATH);
        if(!file.exists()) {
            file.createNewFile();
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRequest.class, orderRequest -> {
                    log.info(orderRequest.getQuery());
                    context().parent().forward(new SearchRequest(orderRequest.getQuery(), true), context());
                })
                .match(SearchResponse.class, searchResponse -> {
                    if (searchResponse.isFound()) {
                        saveOrder(searchResponse.getTitle());
                        context().parent().forward(new OrderResponse(true,
                                String.format("%s was successfully ordered.", searchResponse.getTitle())),
                                context());
                    } else {
                        context().parent().forward(new OrderResponse(false,
                                        String.format("%s was not found.", searchResponse.getMessage())),
                                context());
                    }
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }

    private void saveOrder(String title) throws IOException {
        synchronized (file) {
            final CSVWriter writer = new CSVWriter(
                    new FileWriter(file, true),
                    ';',
                    '"',
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END
            );

            writer.writeNext(new String[] { title });
            writer.close();
        }
    }
}
