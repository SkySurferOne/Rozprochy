package sr.agh.akka.server;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;

public class SearchChildActor extends AbstractActor {
    private final File file;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final String DB_PATH = "src/main/resources/db/";
    private final CSVParser parser;

    public SearchChildActor(String filename) throws FileNotFoundException, URISyntaxException {
        this.file = new File(String.format("%s%s", DB_PATH, filename));
        if(!file.exists()) {
            throw new FileNotFoundException();
        }

        parser = new CSVParserBuilder()
                    .withSeparator(';')
                    .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchRequest.class, searchRequest -> {
                    synchronized (file) {
                        Thread.sleep(2000);

                        final CSVReader reader = new CSVReaderBuilder(new FileReader(file))
                                .withCSVParser(parser)
                                .build();

                        for (String[] line : reader) {
                            String title = line[0];
                            if (title.toLowerCase().startsWith(searchRequest.getQuery().toLowerCase())) {
                                context().parent().forward(new SearchResponse(line[3], true, title,
                                        searchRequest.isPassToOrderActor()), context());
                                return;
                            }
                        }

                        context().parent().forward(new SearchResponse(searchRequest.getQuery(),
                                false, null, searchRequest.isPassToOrderActor()), context());
                    }
                })
                .matchAny(o -> log.info("Received unknown message."))
                .build();
    }
}
