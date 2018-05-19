package sr.agh.akka.server;

public class StreamTextRequest extends BaseRequest {
    private final String filename;

    public StreamTextRequest(String filename) {
        this.filename = filename;
    }

    @Override
    String getQuery() {
        return filename;
    }
}
