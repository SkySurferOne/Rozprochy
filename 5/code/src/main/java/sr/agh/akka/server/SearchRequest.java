package sr.agh.akka.server;

public class SearchRequest extends BaseRequest {
    private String query;
    private boolean passToOrderActor = false;

    public SearchRequest(String query) {
        this.query = query;
    }

    public SearchRequest(String query, boolean passToOrderActor) {
        this.query = query;
        this.passToOrderActor = passToOrderActor;
    }

    public String getQuery() {
        return query;
    }

    public boolean isPassToOrderActor() {
        return passToOrderActor;
    }
}
