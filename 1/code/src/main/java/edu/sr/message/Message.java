package edu.sr.message;

import java.io.Serializable;

public class Message implements Serializable {
    private final int id;
    private final String nick;
    private final String text;
    private final MessageType type;

    public Message(int id, String nick, String text, MessageType type) {
        this.id = id;
        this.nick = nick;
        this.text = text;
        this.type = type;
    }

    public Message(String nick, String text, MessageType type) {
        this(-1, nick, text, type);
    }

    public int getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public String getText() {
        return text;
    }

    public MessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return nick + "("+id+"): "+text;
    }

    public void print() {
        System.out.println(toString());
    }
}
