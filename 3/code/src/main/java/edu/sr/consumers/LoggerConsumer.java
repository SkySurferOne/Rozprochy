package edu.sr.consumers;

import com.rabbitmq.client.Envelope;

public class LoggerConsumer extends AbstractConsumer {
    private String prefix = "";

    public LoggerConsumer(String topicName) {
        super(topicName, true);
    }

    public LoggerConsumer(String topicName, String prefix) {
        super(topicName, true);
        this.prefix = prefix+": ";
    }

    @Override
    public void handle(String message, Envelope envelope) {
        System.out.println(prefix + message);
    }
}
