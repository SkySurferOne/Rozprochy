package sr.agh.akka.server;

import java.io.Serializable;

public abstract class BaseRequest implements Serializable {
    abstract String getQuery();
}
