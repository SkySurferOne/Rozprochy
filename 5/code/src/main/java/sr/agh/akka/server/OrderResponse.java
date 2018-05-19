package sr.agh.akka.server;

import java.io.Serializable;

public class OrderResponse implements Serializable {
    private final boolean ordered;
    private final String message;

    public OrderResponse(boolean ordered, String message) {
        this.ordered = ordered;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOrdered() {
        return ordered;
    }
}
