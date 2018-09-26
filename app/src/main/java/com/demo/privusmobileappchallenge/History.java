package com.demo.privusmobileappchallenge;

public class History {

    private Contact m_contact;
    private String m_date;
    private Type m_type;

    public enum Type {
        RECEIVED,
        SENT,
        NOT_ANSWERED;
    }

    public History(Contact contact, String date, Type type) {
        m_contact = contact;
        m_date = date;
        m_type = type;
    }

    public Contact get_contact() {
        return m_contact;
    }

    public String get_date() {
        return m_date;
    }

    public Type get_type() {
        return m_type;
    }
}
