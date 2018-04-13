package edu.sr.consumers;

import com.rabbitmq.client.*;
import edu.sr.Constants.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static edu.sr.Constants.EXCHANGE_NAME;

public abstract class AbstractConsumer extends Thread {
    private final String topicName;
    private String queueName = null;
    private final boolean autoAck;
    private Connection connection;
    Channel channel;

    public AbstractConsumer(String topicName, boolean autoAck) {
        this.topicName = topicName;
        this.autoAck = autoAck;
    }

    public AbstractConsumer(String topicName, boolean autoAck, String queueName) {
        this.topicName = topicName;
        this.autoAck = autoAck;
        this.queueName = queueName;
    }

    private void configure() throws IOException, TimeoutException {
        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.basicQos(1);

        // exchange
        if (queueName == null) {
            queueName = channel.queueDeclare().getQueue();
        } else {
            channel.queueDeclare(queueName, false, false, false, null);
        }

        channel.queueBind(queueName, EXCHANGE_NAME, topicName);
    }

    public abstract void handle(String message, Envelope envelope) throws IOException;

    private void handleMessage() throws IOException {
        // consumer (message handling)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                handle(message, envelope);
            }
        };

        // start listening
        channel.basicConsume(queueName, autoAck, consumer);
    }

    public void close () throws IOException, TimeoutException {
         channel.close();
         connection.close();
    }

    public void run() {
        try {
            configure();
            handleMessage();

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            try {
                close();
            } catch (IOException | TimeoutException e1) {
                e1.printStackTrace();
            }
        }
    }
}
