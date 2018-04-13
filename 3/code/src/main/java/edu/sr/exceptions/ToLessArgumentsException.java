package edu.sr.exceptions;

public class ToLessArgumentsException extends Exception {
    public ToLessArgumentsException() {
        super("To less arguments in message");
    }
}
