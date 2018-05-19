package sr.agh.akka.server;

import java.io.Serializable;

public class SearchResponse implements Serializable {
    private final String message;
    private final boolean found;
    private final String title;
    private boolean passToOrderActor = false;

    public SearchResponse(String message, boolean found, String title) {
        this.message = message;
        this.found = found;
        this.title = title;
    }

    public SearchResponse(String message, boolean found, String title, boolean passToOrderActor) {
        this.message = message;
        this.found = found;
        this.title = title;
        this.passToOrderActor = passToOrderActor;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFound() {
        return found;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPassToOrderActor() {
        return passToOrderActor;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "message='" + message + '\'' +
                ", found=" + found +
                '}';
    }
}
