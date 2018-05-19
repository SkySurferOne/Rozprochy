package sr.agh.akka.server;

public class OrderRequest extends BaseRequest {
    private final String title;

    public OrderRequest(String title) {
        this.title = title;
    }

    @Override
    public String getQuery() {
        return title;
    }
}
