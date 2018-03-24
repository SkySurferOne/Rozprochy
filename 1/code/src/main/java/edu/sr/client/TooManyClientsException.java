package edu.sr.client;

public class TooManyClientsException extends Exception {
    public TooManyClientsException() {
        super("Too many clients connected");
    }
}
