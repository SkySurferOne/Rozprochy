package sr.agh.akka.server;

import java.io.Serializable;

public class StreamTextResponse implements Serializable {
    private final String line;

    public StreamTextResponse(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }
}
