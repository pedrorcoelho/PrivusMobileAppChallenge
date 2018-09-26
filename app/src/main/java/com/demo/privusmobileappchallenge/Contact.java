package com.demo.privusmobileappchallenge;

import android.content.Context;

public class Contact {

    private String m_name;
    private String m_number;
    private String m_voip_number;
    private String m_photo_uri;
    private boolean m_is_active;
    private int id;

    public Contact(String name, String number, String voip_number, String photo_uri, Context context) {
        m_name = name;
        m_number = number;
        m_voip_number = voip_number;
        m_photo_uri = photo_uri;
        m_is_active = true;
        id = DataManager.getNewContactId(context);
    }

    public void editContact(String name, String number, String voip_number) {
        m_name = name;
        m_number = number;
        m_voip_number = voip_number;
    }

    public String get_name() {
        return m_name;
    }

    public String get_number() {
        return m_number;
    }

    public String get_voip_number() {
        return m_voip_number;
    }

    public String get_photo_uri() {
        return m_photo_uri;
    }

    public boolean is_is_active() {
        return m_is_active;
    }

    public void set_is_active(boolean m_is_active) {
        this.m_is_active = m_is_active;
    }

    public long getId() {
        return id;
    }
}
